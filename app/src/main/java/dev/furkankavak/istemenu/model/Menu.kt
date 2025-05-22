package dev.furkankavak.istemenu.model

import com.google.gson.annotations.SerializedName

data class Menu(
    @SerializedName("id") val id: Int,
    @SerializedName("date") val date: String,
    @SerializedName("extra") val extra: String,
    @SerializedName("total_calories") val total_calories: Int,
    @SerializedName("menu_items") val menu_items: List<MenuItem>,
    @SerializedName("likes_count") val likesCount: Int = 0,
    @SerializedName("dislikes_count") val dislikesCount: Int = 0,
    @SerializedName("user_reaction") val userReaction: String? = null
)