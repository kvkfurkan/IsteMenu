package dev.furkankavak.istemenu.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import app.rive.runtime.kotlin.core.ExperimentalAssetLoader
import dev.furkankavak.istemenu.R
import dev.furkankavak.istemenu.databinding.FragmentLoginBinding
import dev.furkankavak.istemenu.model.AuthManager
import dev.furkankavak.istemenu.model.AuthResponse
import dev.furkankavak.istemenu.model.LoginRequest
import dev.furkankavak.istemenu.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var authManager: AuthManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(ExperimentalAssetLoader::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        authManager = AuthManager(requireContext())

        with(binding){
            riveView.setRiveResource(R.raw.login_anim, stateMachineName = STATE_MACHINE_NAME)
            
            etLoginEmail.setOnFocusChangeListener { _, hasFocus ->
                riveView.setBooleanState(STATE_MACHINE_NAME, "Check", hasFocus)
            }

            etLoginPassword.setOnFocusChangeListener { _, hasFocus ->
                riveView.setBooleanState(STATE_MACHINE_NAME, "hands_up", hasFocus)
            }
            
            etLoginEmail.doOnTextChanged { text, _, _, _ ->
                text?.let {
                    riveView.setNumberState(STATE_MACHINE_NAME, "Look", 2 * it.length.toFloat())
                }
            }

            btnLogin.setOnClickListener {
//                etLoginEmail.clearFocus()
//                etLoginPassword.clearFocus()
//                val email = etLoginEmail.text.toString()
//                val password = etLoginPassword.text.toString()
//                Handler(Looper.getMainLooper()).postDelayed({
//                    if (email == "admin@furkankavak.dev" && password == "123456"){
//                        riveView.fireState(STATE_MACHINE_NAME, "success")
//                        //findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
//                    }else{
//                        riveView.fireState(STATE_MACHINE_NAME, "fail")
//                    }
//                },1150L)

                performLogin()
            }

            tvSignup.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
            }
        }
    }

    companion object{
        private const val STATE_MACHINE_NAME = "State Machine 1"
    }

    fun performLogin(){
        val email = binding.etLoginEmail.text.toString().trim()
        val password = binding.etLoginPassword.text.toString().trim()
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(context, "Email ve şifre boş olamaz", Toast.LENGTH_SHORT).show()
            return
        }

        if(!isValidEmail(email)){
            Toast.makeText(context, "Geçersiz email", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)

        val loginRequest = LoginRequest(email, password)

        //API CALL

        RetrofitClient.apiService.login(loginRequest).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                showLoading(false)

                if(response.isSuccessful){
                    val authResponse = response.body()
                    if(authResponse?.success == true){
                        Toast.makeText(context, "Giriş başarılı!", Toast.LENGTH_SHORT).show()
                        authResponse.data?.access_token?.let { authManager.saveToken(it) }
                        authManager.saveUserEmail(email)

                        binding.riveView.fireState(STATE_MACHINE_NAME, "success")
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    }
                }else{
                    Log.d("LoginFragmentTest", response.body().toString())
                    when (response.code()){
                        400 -> Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(context, "Bilinmeyen bir hata oluştu!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(context, "Bağlantı hatası: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })

    }

    fun isValidEmail(email: String) : Boolean{
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun showLoading(isLoading: Boolean){
        binding.progressBar.visibility = if(isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
        binding.tvSignup.isEnabled = !isLoading
        binding.etLoginEmail.isEnabled = !isLoading
        binding.etLoginPassword.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}