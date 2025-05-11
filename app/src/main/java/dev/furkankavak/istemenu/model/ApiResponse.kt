package dev.furkankavak.istemenu.model

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: ApiData
)

data class ApiData(
    @SerializedName("daily_menus")
    val dailyMenus: List<Menu>
)