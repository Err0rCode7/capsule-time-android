package com.example.capsuletime;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    //객체생성
    String URL = "http://118.44.168.218:7070";
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
}
