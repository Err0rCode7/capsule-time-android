package com.example.login_test

import android.provider.ContactsContract
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface{
    // input & output interface
    @FormUrlEncoded// form url encode
    @POST("/users/auth") // url
    fun requestLogin(
            // input definition
            @Field("user_id") user_id:String,
            @Field("password") password:String
    ) : Call<Success>

    @FormUrlEncoded// form url encode
    @POST("/users/") // url
    fun requestPostUser(
            // input definition
            @Field("user_id") user_id:String,
            @Field("password") password:String,
            @Field("nick_name") nick_name:String,
            @Field("first_name") first_name:String,
            @Field("last_name") last_name:String,
            @Field("email") email: String
    ) : Call<Success>

    @Multipart
    @POST("/users/with/image")
    fun requestPostUserWithImage(
            @Part("user_id") user_id: RequestBody,
            @Part("password") password: RequestBody,
            @Part("nick_name") nick_name: RequestBody,
            @Part("first_name") first_name: RequestBody,
            @Part("last_name") last_name: RequestBody,
            @Part("email") email: RequestBody,
            @Part file: MultipartBody.Part
    ) : Call<Success>

}