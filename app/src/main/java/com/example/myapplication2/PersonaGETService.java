package com.example.myapplication2;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PersonaGETService {
    @GET("{endpoint}")
    Call<List<Persona2>> getPersonas(@Path(value = "endpoint", encoded = true) String endpoint);
}