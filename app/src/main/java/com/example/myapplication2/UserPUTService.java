package com.example.myapplication2;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserPUTService {
    @PUT("{endpoint}")
    Call<User2> updateUser(@Path(value = "endpoint", encoded = true) String endpoint, @Body User2 user);
}