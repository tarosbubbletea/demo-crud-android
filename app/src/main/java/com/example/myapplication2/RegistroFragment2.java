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
 * Use the {@link RegistroFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistroFragment2 extends Fragment {

    private String strRut, strNombre, strCorreo, strClave;
    private FragmentManager fragmentManager;
    private static final String REST = "https://worker.nanachi.store/api/";
    private static final String BearerToken = "2d02a830-68a8-4d38-a154-e3a3bf15294f";
    private FragmentActivity currentActivity;
    /** @noinspection FieldMayBeFinal*/
    private Map<String,Integer> CareerData = new HashMap<>();
    /** @noinspection FieldMayBeFinal*/
    private Map<String,Integer> CampusData = new HashMap<>();
    /** @noinspection FieldMayBeFinal*/
    private Map<String,Integer> UserTypeData = new HashMap<>();


    public RegistroFragment2() {
        // Required empty public constructor
    }

    public static RegistroFragment2 newInstance() {
        return new RegistroFragment2();
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registro2, container, false);

        Button backButton = (Button) view.findViewById(R.id.btnBack);
        Button continueButton = (Button) view.findViewById(R.id.btnContinue);
        currentActivity = getActivity();
        fragmentManager = currentActivity.getSupportFragmentManager();


        Spinner spinCareer = view.findViewById(R.id.spinnerCarrera);
        Spinner spinCampus = view.findViewById(R.id.spinnerSede);
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
        PersonaService personaService = retrofit.create(PersonaService.class);
        UserService userService = retrofit.create(UserService.class);

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

                Toast.makeText(currentActivity, "Subiendo datos...", Toast.LENGTH_SHORT).show();

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

                User user = new User(strCorreo, strClave, idUserType);
                Persona persona = new Persona(0, strNombre, strRut, idCampus, idCareer, semester, year);

                sendData(userService, "user", user, personaService, "persona", persona);



            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // ...

        Bundle args = getArguments();
        assert args != null;
        strRut = args.getString("strRut");
        strNombre = args.getString("strNombre");
        strCorreo = args.getString("strCorreo");
        strClave = args.getString("strClave");

        if (args == null || TextUtils.isEmpty(strRut) || TextUtils.isEmpty(strNombre) || TextUtils.isEmpty(strCorreo) || TextUtils.isEmpty(strClave)) {
            // Handle the error here...
            return;
        }

        // Use the data...
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

            }

            @Override
            public void onFailure(Call<List<Data>> call, Throwable t) {
                Log.e("Failed", t.getMessage());
            }
        });
    }

    private void sendDataPersona(PersonaService service, String endpoint, Persona persona) {
        Call<Void> personaCall = service.createUser(endpoint, persona);

        personaCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {

                if (currentActivity == null) return;

                if (!response.isSuccessful()) {
                    Log.e("Request failed", "Code: " + response.code());
                    return;
                }

                Toast.makeText(currentActivity, "Persona añadida exitosamente", Toast.LENGTH_SHORT).show();

                fragmentManager.popBackStack();
                fragmentManager.popBackStack();

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Failed", t.getMessage());
            }
        });
    }

    private void sendData(UserService service, String endpoint, User user, PersonaService personaService, String endpoint2, Persona persona) {
        Call<User2> userCall = service.createUser(endpoint, user);

        userCall.enqueue(new Callback<User2>() {
            @Override
            public void onResponse(Call<User2> call, retrofit2.Response<User2> response) {

                if (currentActivity == null) return;

                if (!response.isSuccessful()) {
                    Log.e("Request failed", "Code: " + response.code());
                    return;
                }

                // read response
                User2 responseBody = response.body();
                Integer userId = responseBody.getId();

                Toast.makeText(currentActivity, "Usuario creado, subiendo persona...", Toast.LENGTH_SHORT).show();

                persona.setUser_id(userId);

                sendDataPersona(personaService, endpoint2, persona);
            }

            @Override
            public void onFailure(Call<User2> call, Throwable t) {
                Log.e("Failed", t.getMessage());
            }
        });
    }

}