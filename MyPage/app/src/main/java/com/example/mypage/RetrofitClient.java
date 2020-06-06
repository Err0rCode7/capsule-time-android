package com.example.mypage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private String BASE_URL = "http://118.44.168.218";
    private String PORT = ":7070";

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL+PORT)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
}
