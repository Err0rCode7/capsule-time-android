package com.example.capsuletime

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize

data class Success (
        val success : String
)

data class Content (
        val content_id: Int?,
        val url: String?
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

data class CapsuleLogData (
        val capsule_id: Int,
        val d_day: String,
        val iv_url: String,
        val tv_title: String,
        val tv_tags: String,
        val tv_created_date: String,
        val tv_opened_date: String,
        val tv_location: String,
        val state_temp: Int
)

data class User(
        var user_id: String?,
        var nick_name: String?,
        var fist_name: String?,
        var last_name: String?,
        var email_id: String?,
        var email_domain: String?,
        var date_created: String?,
        var date_updated: String?,
        var image_url: String?,
        var image_name: String?) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(user_id)
        parcel.writeString(nick_name)
        parcel.writeString(fist_name)
        parcel.writeString(last_name)
        parcel.writeString(email_id)
        parcel.writeString(email_domain)
        parcel.writeString(date_created)
        parcel.writeString(date_updated)
        parcel.writeString(image_url)
        parcel.writeString(image_name)
    }
    override fun describeContents(): Int {
        return 0;
    }
    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}

