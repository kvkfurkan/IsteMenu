package dev.furkankavak.istemenu.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import app.rive.runtime.kotlin.core.ExperimentalAssetLoader
import dev.furkankavak.istemenu.R
import dev.furkankavak.istemenu.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(ExperimentalAssetLoader::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
                etLoginEmail.clearFocus()
                etLoginPassword.clearFocus()
                val email = etLoginEmail.text.toString()
                val password = etLoginPassword.text.toString()
                Handler(Looper.getMainLooper()).postDelayed({
                    if (email == "admin@furkankavak.dev" && password == "123456"){
                        riveView.fireState(STATE_MACHINE_NAME, "success")
                    }else{
                        riveView.fireState(STATE_MACHINE_NAME, "fail")
                    }
                },1150L)
            }

            tvSignup.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
            }
        }
    }

    companion object{
        private const val STATE_MACHINE_NAME = "State Machine 1"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}