package dev.furkankavak.istemenu.model

import android.content.Context

class AuthManager(context: Context) {
    private val sharedPreferences =
        context.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE)

    fun saveToken(token:String){
        with(sharedPreferences.edit()){
            putString("auth_token", token)
            apply()
        }
    }

    fun getToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }

    fun clearToken(){
        with(sharedPreferences.edit()){
            remove("auth_token")
            apply()
        }
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    fun saveUserEmail(email: String){
        with(sharedPreferences.edit()){
            putString("user_email", email)
            apply()
        }
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString("user_email", null)
    }
}