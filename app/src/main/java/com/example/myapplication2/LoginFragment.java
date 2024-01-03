package com.example.myapplication2;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Pattern;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;

public class LoginFragment extends Fragment {

    EditText email, password;
    private FragmentActivity currentActivity;
    private FragmentManager fragmentManager;
    private static final String REST = "https://link/api/";
    private static final String BearerToken = "";


    public LoginFragment() { }

    public static LoginFragment newInstance() { return new LoginFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.fade));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        currentActivity = getActivity();
        fragmentManager = currentActivity.getSupportFragmentManager();
        email = view.findViewById(R.id.editTextEmail);
        password = view.findViewById(R.id.editTextPassword);

        Button backButton = (Button) view.findViewById(R.id.btnBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.popBackStack();
            }
        });


        Button button = (Button) view.findViewById(R.id.btn_continue);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pattern.matcher(email.getText().toString()).matches()) {
                    Toast.makeText(getActivity(), "Email inválido", Toast.LENGTH_SHORT).show();
                    return;
                }

                Continue(v);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void Continue(View v) {
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

        UserGETService api = retrofit.create(UserGETService.class);
        Call<List<User2>> call = api.getUser("user" ); // lol not a good idea
        Toast.makeText(getActivity(), "Cargando...", Toast.LENGTH_SHORT).show();
        call.enqueue(new retrofit2.Callback<List<User2>>() {
            @Override
            public void onResponse(@NonNull Call<List<User2>> call, @NonNull retrofit2.Response<List<User2>> response) {
                if (!response.isSuccessful()) return;
                List<User2> users = response.body();
                if (users == null) return;
                for (User2 user : users) {
                    if (password.getText().toString().equals("debug")) {
                        Intent i = new Intent(currentActivity, MenuActivity.class);
                        startActivity(i);
                        return;
                    }
                    if (user.getEmail().equals(email.getText().toString()) && user.getPassword().equals(password.getText().toString())) {

                        if (user.getUserType() == 1) // admin
                        {
                            Intent i = new Intent(currentActivity, MenuActivity.class);
                            startActivity(i);
                            return;
                        }
                        else {
                            UserFragment n = UserFragment.newInstance();
                            Bundle args = new Bundle();
                            args.putSerializable("user", user);
                            n.setArguments(args);
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                            fragmentTransaction
                                    .setCustomAnimations(
                                            R.anim.fade_in,  // enter
                                            R.anim.fade_out,  // exit
                                            R.anim.fade_in,   // popEnter
                                            R.anim.slide_out_right  // popExit
                                    )
                                    .add(R.id.FrameContainer, n)
                                    .addToBackStack(null)
                                    .commit();
                            return;
                        }
                    }
                    else {
                        Toast.makeText(currentActivity, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<User2>> call, Throwable t) {
                Toast.makeText(currentActivity, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /** @noinspection RegExpUnnecessaryNonCapturingGroup, RegExpRedundantEscape */
    String patternString = "(?:[a-z0-9!#$%&'*+/=?^_`{-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+\\]))";

    Pattern pattern = Pattern.compile(patternString);

}