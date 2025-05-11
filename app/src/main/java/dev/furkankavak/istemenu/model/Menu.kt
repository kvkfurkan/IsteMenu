package dev.furkankavak.istemenu.model

data class Menu(
    val id: Int,
    val date: String,
    val extra: String,
    val total_calories: Int,
    val menu_items: List<MenuItem>
)