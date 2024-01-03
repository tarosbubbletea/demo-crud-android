package com.example.myapplication2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import java.util.List;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

public class UserFragment extends Fragment {

    private TextView tv1, tv2;
    private User2 u;
    private static final String REST = "https://worker.nanachi.store/api/";
    private static final String BearerToken = "2d02a830-68a8-4d38-a154-e3a3bf15294f";
    private FragmentActivity currentActivity;
    private FragmentManager fragmentManager;


    public UserFragment() { }

    public static UserFragment newInstance() { return new UserFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_user, container, false);
        currentActivity = getActivity();
        fragmentManager = currentActivity.getSupportFragmentManager();
        tv1 = v.findViewById(R.id.textView);
        tv2 = v.findViewById(R.id.textView2);

        Bundle args = getArguments();
        assert args != null;
        u = (User2) args.getSerializable("user");



        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button b = view.findViewById(R.id.btn_salir);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.popBackStack();
                fragmentManager.popBackStack();
            }
        });


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

        PersonaGETService api = retrofit.create(PersonaGETService.class);
        retrofit2.Call<List<Persona2>> call = api.getPersonas("persona");

        call.enqueue(new retrofit2.Callback<List<Persona2>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Persona2>> call, retrofit2.Response<List<Persona2>> response) {
                if (response.isSuccessful()) {
                    List<Persona2> personas = response.body();
                    for (Persona2 p : personas) {
                        if (p.getUser_id() == u.getId()) {
                            tv1.setText(p.getName());
//                            tv2.setText(p.getRut());

                            DataService api2 = retrofit.create(DataService.class);
                            retrofit2.Call<List<Data>> call2 = api2.getDataList("careers");
                            call2.enqueue(new retrofit2.Callback<List<Data>>() {
                                @Override
                                public void onResponse(retrofit2.Call<List<Data>> call, retrofit2.Response<List<Data>> response) {
                                    if (response.isSuccessful()) {
                                        List<Data> data = response.body();
                                        assert data != null;
                                        for (Data d : data) {
                                            if (d.getId() == p.getCareer_id()) {
                                                String s = d.getName() + " - " + p.getRut();
                                                tv2.setText(s);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(retrofit2.Call<List<Data>> call, Throwable t) {
                                    //
                                }
                            });

                        }
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<Persona2>> call, Throwable t) {
                tv1.setText(t.getMessage());
            }
        });

    }
}