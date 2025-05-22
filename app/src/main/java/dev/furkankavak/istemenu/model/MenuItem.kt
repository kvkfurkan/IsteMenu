package dev.furkankavak.istemenu.model

import com.google.gson.annotations.SerializedName

data class MenuItem(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("calories") val calories: Int
)