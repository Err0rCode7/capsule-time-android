package com.example.mypage

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {

    @GET("/capsules/user")
    fun requestSearchUserCapsule(
            @Query("user_id") user_id: String
    ): Call<List<Capsule>>

    @DELETE("/capsules/{capsule_id}")
    fun requestDeleteCapsule(
            @Path("capsule_id") capsule_id: Int
    ): Call<Success>

    @Multipart
    @PUT ("/capsules/")
    fun requestPutCapsule(
            @Part("capsule_id") capsule_id: RequestBody,
            @Part("title") title: RequestBody,
            @Part("text") text: RequestBody,
            //@Part("tag") tag: List<String>,
            //@Part file: MultipartBody.Part
            @Part file: List<MultipartBody.Part>
    ) : Call<Success>

}