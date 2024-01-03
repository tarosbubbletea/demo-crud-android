package com.example.myapplication2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Pattern;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditarFragment extends Fragment {

    EditText txtRut, txtNombre, txtCorreo, txtClave;
    private static final String REST = "https://link/api/";
    private static final String BearerToken = "";
    String patternString = "(?:[a-z0-9!#$%&'*+/=?^_`{-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+\\]))";

    Pattern pattern = Pattern.compile(patternString);
    Bundle bundle;
    Persona2 p;
    User2 u;
    private FragmentManager fragmentManager;
    private FragmentActivity currentActivity;


    public EditarFragment() {
        // Required empty public constructor
    }

    public static EditarFragment newInstance(String param1, String param2) { return new EditarFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.fade));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_editar, container, false);
        currentActivity = getActivity();
        fragmentManager = currentActivity.getSupportFragmentManager();
        Button backButton = (Button) view.findViewById(R.id.btnBack);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.popBackStack();
            }
        });

        Button nextButton = (Button) view.findViewById(R.id.btn_continue);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Continue(v);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtNombre = view.findViewById(R.id.editTextNombre);
        txtRut = view.findViewById(R.id.editTextRut);
        txtCorreo = view.findViewById(R.id.editTextEmail);
        txtClave = view.findViewById(R.id.editTextClave);
        bundle = this.getArguments();
        p = (Persona2) bundle.getSerializable("persona");

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
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build();

        UserGETService service = retrofit.create(UserGETService.class);
        Call<List<User2>> call = service.getUser("user" + "/" + p.getUser_id());

        call.enqueue(new retrofit2.Callback<List<User2>>() {
            @Override
            public void onResponse(Call<List<User2>> call, retrofit2.Response<List<User2>> response) {

                if (currentActivity == null) return;

                if (!response.isSuccessful()) {
                    Log.e("Request failed", "Code: " + response.code());
                    Toast.makeText(getActivity(), "Error al obtener datos del usuario.", Toast.LENGTH_SHORT).show();
                    fragmentManager.popBackStack();
                    return;
                }

                List<User2> users = response.body();
                u = users.get(0);
                assert u != null;
                txtCorreo.setText(u.getEmail());
//                txtClave.setText(u.getPassword());
            }

            @Override
            public void onFailure(Call<List<User2>> call, Throwable t) {
                // broken callback
            }
        });

        txtNombre.setText(p.getName());
        txtRut.setText(p.getRut());

    }

    public void Continue(View view) {
        // get bundle

        assert bundle != null;


        String strNombre = txtNombre.getText().toString();
        String strRut = txtRut.getText().toString();
        String strCorreo = txtCorreo.getText().toString();
        String strClave = txtClave.getText().toString();

        if (!RutValidator.validateRut(strRut).equals("1")) {
            Toast.makeText(getActivity(), RutValidator.validateRut(strRut), Toast.LENGTH_SHORT).show();
            return;
        }

        if (strNombre.length() < 4) {
            Toast.makeText(getActivity(), "El nombre debe tener al menos 4 caracteres.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pattern.matcher(strCorreo).matches()) {
            Toast.makeText(getActivity(), "El correo no es válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (strClave.length() < 5) {
            Toast.makeText(getActivity(), "La clave debe tener al menos 5 caracteres.", Toast.LENGTH_SHORT).show();
            return;
        }

        EditarFragment2 nextFrag = EditarFragment2.newInstance();

        Bundle args = new Bundle();
        assert p != null;
        p.setName(strNombre);
        p.setRut(strRut);
        args.putInt("user_id", u.getId());
        args.putInt("usertype_id", u.getUserType());
        args.putInt("persona_id", p.getId());
        args.putString("strCorreo", strCorreo);
        args.putString("strClave", strClave);
        args.putSerializable("persona", p);

        nextFrag.setArguments(args);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction
                .setCustomAnimations(
                        R.anim.slide_in_right,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out_right  // popExit
                )
                .add(R.id.FrameContainer, nextFrag,"findThisFragment")
                .addToBackStack(null)
                .commit();
    }
}