package dev.furkankavak.istemenu.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import app.rive.runtime.kotlin.core.ExperimentalAssetLoader
import dev.furkankavak.istemenu.R
import dev.furkankavak.istemenu.databinding.FragmentSignUpBinding
import dev.furkankavak.istemenu.model.AuthManager
import dev.furkankavak.istemenu.model.AuthResponse
import dev.furkankavak.istemenu.model.SignUpRequest
import dev.furkankavak.istemenu.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignUpFragment : Fragment() {

    private var _binding : FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var authManager: AuthManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(ExperimentalAssetLoader::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        authManager = AuthManager(requireContext())

        with(binding){
            riveView.setRiveResource(R.raw.login_anim, stateMachineName = STATE_MACHINE_NAME)

            etSignUpEmail.setOnFocusChangeListener { _, hasFocus ->
                riveView.setBooleanState(STATE_MACHINE_NAME, "Check", hasFocus)
            }

            etSignUpPassword.setOnFocusChangeListener { _, hasFocus ->
                riveView.setBooleanState(STATE_MACHINE_NAME, "hands_up", hasFocus)
            }

            etSignUpPasswordVerification.setOnFocusChangeListener { _, hasFocus ->
                riveView.setBooleanState(STATE_MACHINE_NAME, "hands_up", hasFocus)
            }

            etSignUpEmail.doOnTextChanged { text, _, _, _ ->
                text?.let {
                    riveView.setNumberState(STATE_MACHINE_NAME, "Look", 2 * it.length.toFloat())
                }
            }

            btnSignUp.setOnClickListener {
                etSignUpEmail.clearFocus()
                etSignUpPassword.clearFocus()
                etSignUpPasswordVerification.clearFocus()
//
//                val password = etSignUpPassword.text.toString()
//                val passwordVerification = etSignUpPasswordVerification.text.toString()
//
//                Handler(Looper.getMainLooper()).postDelayed({
//                    if (password == passwordVerification){
//                        riveView.fireState(STATE_MACHINE_NAME, "success")
//                        findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
//                    }else{
//                        riveView.fireState(STATE_MACHINE_NAME, "fail")
//                    }
//                },1150L)

                performSignUp()
            }

            tvLogin.setOnClickListener {
                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
            }
        }
    }

    @OptIn(ExperimentalAssetLoader::class)
    fun performSignUp(){
        val name = "user"
        val email = binding.etSignUpEmail.text.toString().trim()
        val password = binding.etSignUpPassword.text.toString().trim()
        val passwordVerification = binding.etSignUpPasswordVerification.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || passwordVerification.isEmpty()){
            Toast.makeText(context, "Tüm alanlar doldurulmalıdır", Toast.LENGTH_SHORT).show()
            return
        }

        if(!isValidEmail(email)){
            Toast.makeText(context, "Geçerli bir e-posta adresi giriniz", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 8){
            Toast.makeText(context, "Şifre en az 8 karakter olmalıdır", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != passwordVerification){
            Toast.makeText(context, "Şifreler eşleşmiyor", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)

        val signUpRequest = SignUpRequest(name, email, password, passwordVerification)

        //API CALL
        RetrofitClient.apiService.signup(signUpRequest).enqueue(object : Callback<AuthResponse>{
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                showLoading(false)

                if (response.isSuccessful){
                    val authResponse = response.body()
                    if (authResponse?.success == true){
                        Toast.makeText(context, "Kayıt başarılı", Toast.LENGTH_SHORT).show()
                        authResponse.data?.access_token?.let { authManager.saveToken(it) }
                        authManager.saveUserEmail(email)

                        binding.riveView.fireState(STATE_MACHINE_NAME, "success")
                        findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
                    }
                }else{
                    binding.riveView.fireState(STATE_MACHINE_NAME, "fail")
                    when(response.code()){
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSignUp.isEnabled = !isLoading
        binding.tvLogin.isEnabled = !isLoading
        binding.etSignUpEmail.isEnabled = !isLoading
        binding.etSignUpPassword.isEnabled = !isLoading
        binding.etSignUpPasswordVerification.isEnabled = !isLoading
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    companion object{
        private const val STATE_MACHINE_NAME = "State Machine 1"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}