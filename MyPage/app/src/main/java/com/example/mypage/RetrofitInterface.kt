package com.example.mypage

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
}