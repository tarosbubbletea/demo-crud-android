package com.example.myapplication2;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UserGETService {
    @GET("{endpoint}")
    Call<List<User2>> getUser(@Path(value = "endpoint", encoded = true) String endpoint);
}
