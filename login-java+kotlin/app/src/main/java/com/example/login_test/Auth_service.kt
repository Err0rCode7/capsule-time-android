package com.example.login_test

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Auth_service{
    // input & output interface
    @FormUrlEncoded// form url encode
    @POST("/users/auth") // url
    fun requestLogin(
            // input definition
            @Field("user_id") user_id:String,
            @Field("password") password:String
    ) : Call<Auth_data> // output definition ( Auth_data.kt )
}