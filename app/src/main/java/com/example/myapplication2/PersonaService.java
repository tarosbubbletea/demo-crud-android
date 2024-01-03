package com.example.myapplication2;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PersonaService {
    @POST("{endpoint}")
    Call<Void> createUser(@Path("endpoint") String endpoint, @Body Persona persona);
}
