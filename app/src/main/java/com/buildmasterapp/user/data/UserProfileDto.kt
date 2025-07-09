package com.buildmasterapp.user.data

import com.google.gson.annotations.SerializedName

// Modelo para Retrofit
// Coincide con el JSON del endpoint

data class UserProfileDto(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("profile_picture_url") val profilePictureUrl: String,
    @SerializedName("description") val description: String,
    @SerializedName("age") val age: Int,
    @SerializedName("phone_number") val phoneNumber: String
)

