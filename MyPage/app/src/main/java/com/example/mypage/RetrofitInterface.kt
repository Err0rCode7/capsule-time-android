package com.example.mypage

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RetrofitInterface {

    @GET("/capsules/user")
    fun requestSearchUserCapsule(
            @Query("user_id") user_id: String
    ): Call<List<Capsule>>
}