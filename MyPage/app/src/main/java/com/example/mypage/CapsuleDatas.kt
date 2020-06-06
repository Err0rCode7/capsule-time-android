package com.example.mypage

data class Content (
        val content_id: Int,
        val url: String
)

data class Capsule (
        val capsule_id: Int,
        val user_id: String,
        val title: String,
        val likes: Int,
        val views: Int,
        val date_created: String,
        val date_opened: String,
        val status_temp: Int,
        val lat: Double,
        val lng: Double,
        val content: List<Content>
)

