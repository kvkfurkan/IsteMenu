package dev.furkankavak.istemenu.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import dev.furkankavak.istemenu.R
import dev.furkankavak.istemenu.databinding.FragmentSplashBinding
import dev.furkankavak.istemenu.model.AuthManager
import dev.furkankavak.istemenu.model.AuthResponse
import dev.furkankavak.istemenu.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashFragment : Fragment() {

    private var _binding : FragmentSplashBinding ?= null
    private val binding get() = _binding!!
    private lateinit var authManager: AuthManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authManager = AuthManager(requireContext())

        Handler(Looper.getMainLooper()).postDelayed({
            checkLoginStatus()
        }, 2000)
    }

    private fun checkLoginStatus() {
        if (authManager.isLoggedIn()) {
            // User has a token, validate it by making an API call

            RetrofitClient.apiService.getUserInfo().enqueue(object : Callback<AuthResponse> {
                override fun onResponse(
                    call: Call<AuthResponse>,
                    response: Response<AuthResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        // Token is valid, navigate to home
                        findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                    } else {
                        // Token is invalid, clear it and navigate to login
                        authManager.clearToken()
                        findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    // Error checking token, navigate to login
                    Toast.makeText(context, "Bağlantı hatası", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                }
            })
        } else {
            // No token, navigate to login
            findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}