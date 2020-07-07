package com.example.capsuletime

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {
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

    @GET("/capsules/user")
    fun requestSearchUserCapsule(
            @Query("user_id") user_id: String
    ): Call<List<Capsule>>

    @DELETE("/capsules/{capsule_id}")
    fun requestDeleteCapsule(
            @Path("capsule_id") capsule_id: Int
    ): Call<Success>

    @Multipart
    @PUT ("/capsules/with/images")
    fun requestPutCapsuleWithImages(
            @Part("capsule_id") capsule_id: RequestBody,
            @Part("title") title: RequestBody,
            @Part("text") text: RequestBody,
            @Part file: List<MultipartBody.Part>
    ) : Call<Success>

    @FormUrlEncoded
    @PUT ("/capsules")
    fun requestPutCapsule(
            // input definition
            @Field("capsule_id") capsule_id: Int,
            @Field("title") title: String,
            @Field("text") text: String
    ) : Call<Success>

    @GET ("/users/{user_id}")
    fun requestUserData (
            @Path("user_id") user_id: String
    ): Call<User>

    @GET("/users/{user_id}")
    fun requestSearchUser(
            @Path("user_id") user_id: String
    ):Call <User>

    @GET("/capsules/")
    fun requestAllCapsules():Call <List<cap>>

}