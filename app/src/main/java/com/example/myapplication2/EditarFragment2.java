package com.example.myapplication2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditarFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditarFragment2 extends Fragment {

    private String strCorreo, strClave;
    private TextView txtYear, txtSemestre;
    private int user_id;
    private Persona2 p;
    private FragmentManager fragmentManager;
    private static final String REST = "https://link/api/";
    private static final String BearerToken = "";
    private FragmentActivity currentActivity;
    /** @noinspection FieldMayBeFinal*/
    private Map<String,Integer> CareerData = new HashMap<>();
    /** @noinspection FieldMayBeFinal*/
    private Map<String,Integer> CampusData = new HashMap<>();
    /** @noinspection FieldMayBeFinal*/
    private Map<String,Integer> UserTypeData = new HashMap<>();
    private Spinner spinCareer;
    private Spinner spinCampus;

    public EditarFragment2() { }

    public static EditarFragment2 newInstance() { return new EditarFragment2(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_editar2, container, false);

        Button backButton = (Button) view.findViewById(R.id.btnBack);
        Button continueButton = (Button) view.findViewById(R.id.btnContinue);
        txtYear = view.findViewById(R.id.editTextYear);
        txtSemestre = view.findViewById(R.id.editTextSemester);
        currentActivity = getActivity();
        fragmentManager = currentActivity.getSupportFragmentManager();


        spinCareer = view.findViewById(R.id.spinnerCarrera);
        spinCampus = view.findViewById(R.id.spinnerSede);
        Spinner spinUserType = view.findViewById(R.id.spinnerUsuario);

        // initialize retrofit instance
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public okhttp3.Response intercept(@NotNull Chain chain) throws java.io.IOException {
                        Request originalRequest = chain.request();

                        Request.Builder builder = originalRequest.newBuilder()
                                .header("Authorization", "Bearer " + BearerToken);

                        Request newRequest = builder.build();
                        return chain.proceed(newRequest);
                    }
                })
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(REST)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DataService dataService = retrofit.create(DataService.class);
        PersonaPUTService personaPUTService = retrofit.create(PersonaPUTService.class);
        UserPUTService userPUTService = retrofit.create(UserPUTService.class);

        // API calls for each spinner

        fetchDataForSpinner(dataService, "careers", spinCareer, CareerData);
        fetchDataForSpinner(dataService, "campus", spinCampus, CampusData);
        fetchDataForSpinner(dataService, "usertypes", spinUserType, UserTypeData);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.popBackStack();
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(currentActivity, "Actualizando datos...", Toast.LENGTH_SHORT).show();

                String strCareer = ((Spinner) view.findViewById(R.id.spinnerCarrera)).getSelectedItem().toString();
                String strCampus = ((Spinner) view.findViewById(R.id.spinnerSede)).getSelectedItem().toString();
                String strUserType = ((Spinner) view.findViewById(R.id.spinnerUsuario)).getSelectedItem().toString();
                Integer idCareer = CareerData.get(strCareer);
                Integer idCampus = CampusData.get(strCampus);
                Integer idUserType = UserTypeData.get(strUserType);

                int year, semester;

                try {
                    year = Integer.parseInt(((EditText) view.findViewById(R.id.editTextYear)).getText().toString());
                    semester = Integer.parseInt(((EditText) view.findViewById(R.id.editTextSemester)).getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(currentActivity, "Ingrese un año y número de semestre válidos", Toast.LENGTH_SHORT).show();
                    return;
                }

                User2 u = new User2(user_id, strCorreo, strClave, idUserType);
                p.setCampus_id(idCampus);
                p.setCareer_id(idCareer);
                p.setYear(year);
                p.setSemester(semester);

                sendData(userPUTService, "user", u, personaPUTService, "persona", p);



            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // ...

        Bundle args = getArguments();
        p = (Persona2) args.getSerializable("persona");
        user_id = args.getInt("user_id");
        strCorreo = args.getString("strCorreo");
        strClave = args.getString("strClave");
        txtYear.setText(String.valueOf(p.getYear()));
        txtSemestre.setText(String.valueOf(p.getSemester()));



        if (args == null || p == null || TextUtils.isEmpty(strCorreo) || TextUtils.isEmpty(strClave)) {
            // Handle the error here...
            return;
        }


    }

    private void fetchDataForSpinner(DataService service, String endpoint, Spinner spinner, Map<String, Integer> namesToIds) {
        Call<List<Data>> dataCall = service.getDataList(endpoint);

        dataCall.enqueue(new Callback<List<Data>>() {
            @Override
            public void onResponse(Call<List<Data>> call, retrofit2.Response<List<Data>> response) {

                if (currentActivity == null) return;

                if (!response.isSuccessful()) {
                    Log.e("Request failed", "Code: " + response.code());
                    return;
                }

                List<Data> dataList = response.body();
                List<String> names = new ArrayList<>();

                assert dataList != null;
                for (Data data : dataList) {
                    names.add(data.getName());
                    namesToIds.put(data.getName(), data.getId());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(currentActivity,
                        R.layout.spinner_layout,
                        names);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                spinCareer.setSelection(p.getCareer_id()-1);
                spinCampus.setSelection(p.getCampus_id()-1);
            }

            @Override
            public void onFailure(Call<List<Data>> call, Throwable t) {
                Log.e("Failed", t.getMessage());
            }
        });
    }

    private void sendDataPersona(PersonaPUTService service, String endpoint, Persona2 persona) {
        Call<Void> personaCall = service.updatePersona(endpoint+"/"+persona.getId(), persona);

        personaCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {

                if (currentActivity == null) return;

                if (!response.isSuccessful()) {
                    Log.e("Request failed", "Code: " + response.code());
                    Toast.makeText(getActivity(), "Error al actualizar datos de la persona.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(currentActivity, "Persona actualizada exitosamente", Toast.LENGTH_SHORT).show();

                fragmentManager.popBackStack();
                fragmentManager.popBackStack();

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Failed", t.getMessage());
            }
        });
    }

    private void sendData(UserPUTService service, String endpoint, User2 user, PersonaPUTService personaService, String endpoint2, Persona2 persona) {
        Call<User2> userCall = service.updateUser(endpoint+"/"+user.getId(), user);

        userCall.enqueue(new Callback<User2>() {
            @Override
            public void onResponse(Call<User2> call, retrofit2.Response<User2> response) {

                if (currentActivity == null) return;

                if (!response.isSuccessful()) {
                    Log.e("Request failed", "Code: " + response.code());
                    return;
                }

                sendDataPersona(personaService, endpoint2, persona);
            }

            @Override
            public void onFailure(Call<User2> call, Throwable t) {
                Log.e("Failed", t.getMessage());
            }
        });
    }
}