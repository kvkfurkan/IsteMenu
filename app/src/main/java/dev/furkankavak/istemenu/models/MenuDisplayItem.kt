package dev.furkankavak.istemenu.models

data class MenuDisplayItem(
    val id: Int,
    val date: String,
    val mainDish: String,
    val sideDish1: String,
    val sideDish2: String,
    val dessert: String,
    val calories: Int,
    val likes: Int = 0,
    val dislikes: Int = 0
)