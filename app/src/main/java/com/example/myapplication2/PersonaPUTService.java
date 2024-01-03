package com.example.myapplication2;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PersonaPUTService {
    @PUT("{endpoint}")
    Call<Void> updatePersona(@Path(value = "endpoint", encoded = true) String endpoint, @Body Persona2 persona);
}
