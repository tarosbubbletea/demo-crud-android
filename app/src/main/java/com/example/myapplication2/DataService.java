package com.example.myapplication2;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DataService {
    @GET("{endpoint}")
    Call<List<Data>> getDataList(@Path("endpoint") String endpoint);
}