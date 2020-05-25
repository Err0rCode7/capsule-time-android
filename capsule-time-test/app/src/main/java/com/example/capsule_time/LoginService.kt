package com.example.capsule_time

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoginService{

    // input & output interface
    @FormUrlEncoded // form url encode
    @POST("/users/auth") // url
    fun requestLogin(
        // input definition
        @Field("user_id") user_id:String,
        @Field("password") password:String
    ) : Call<Login> // output definition ( login.kt )

}