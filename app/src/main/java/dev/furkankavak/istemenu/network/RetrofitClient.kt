package dev.furkankavak.istemenu.network

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dev.furkankavak.istemenu.MainActivity
import dev.furkankavak.istemenu.model.AuthError
import dev.furkankavak.istemenu.model.AuthManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://iste-yemekhane-production.up.railway.app/api/"

    // Create a custom Gson instance that's more forgiving with type mismatches
    private val gson: Gson = GsonBuilder()
        .setLenient() // Be lenient with malformed JSON
        .create()

    // Context for auth
    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    // Auth interceptor that adds the token to all requests
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()

        // If applicationContext is not initialized yet, proceed without token
        if (!::applicationContext.isInitialized) {
            return@Interceptor chain.proceed(originalRequest)
        }

        val authManager = AuthManager(applicationContext)
        val token = authManager.getToken()

        // If there's no token, proceed with the original request
        if (token == null) {
            chain.proceed(originalRequest)
        } else {
            // Add token to request
            val requestWithToken = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(requestWithToken)
        }
    }

    // Response interceptor to handle 401 errors and show server messages
    private val responseInterceptor = Interceptor { chain ->
        val request: Request = chain.request()
        val response: Response = chain.proceed(request)

        if (response.code == 401) {
            // Token expired or invalid
            if (::applicationContext.isInitialized) {
                val authManager = AuthManager(applicationContext)

                // Clear the token
                authManager.clearToken()

                // Show toast and redirect to login page on the main thread
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        applicationContext,
                        "Oturumunuz sonlandı. Lütfen tekrar giriş yapın",
                        Toast.LENGTH_LONG
                    ).show()

                    // Restart app to go to login screen
                    val intent = Intent(applicationContext, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    applicationContext.startActivity(intent)
                }
            }
        }

        // Show server error messages as toast using AuthError class
        if (!response.isSuccessful && response.body != null) {
            try {
                val errorBody = response.peekBody(Long.MAX_VALUE).string()
                val authError = Gson().fromJson(errorBody, AuthError::class.java)

                // Use error message from the AuthError class
                val errorMessage = authError.message ?: "Sunucu hatası: ${response.code}"

                // Show toast on main thread
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Ignore parsing errors
            }
        }

        response
    }

    // OkHttpClient with the auth interceptor
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(responseInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Main API service instance that automatically includes token authentication when available
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }

    // This method is now deprecated as the default apiService will handle authentication
    @Deprecated(
        "Use the default apiService instead which automatically handles authentication",
        replaceWith = ReplaceWith("apiService")
    )
    fun createAuthenticatedApiService(context: Context): ApiService {
        return apiService
    }
}