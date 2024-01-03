package com.example.myapplication2;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Path;

public interface UserDELETEService {
    @DELETE("{endpoint}")
    Call<Void> deleteUser(@Path(value = "endpoint", encoded = true) String endpoint);
}
