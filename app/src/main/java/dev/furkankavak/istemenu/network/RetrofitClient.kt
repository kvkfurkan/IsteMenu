package dev.furkankavak.istemenu.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://iste-yemekhane-production.up.railway.app"

    // Create a custom Gson instance that's more forgiving with type mismatches
    private val gson: Gson = GsonBuilder()
        .setLenient() // Be lenient with malformed JSON
        .create()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}