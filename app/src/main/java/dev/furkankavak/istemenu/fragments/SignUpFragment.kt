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
import dev.furkankavak.istemenu.databinding.FragmentSignUpBinding


class SignUpFragment : Fragment() {

    private var _binding : FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(ExperimentalAssetLoader::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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

                val password = etSignUpPassword.text.toString()
                val passwordVerification = etSignUpPasswordVerification.text.toString()

                Handler(Looper.getMainLooper()).postDelayed({
                    if (password == passwordVerification){
                        riveView.fireState(STATE_MACHINE_NAME, "success")
                    }else{
                        riveView.fireState(STATE_MACHINE_NAME, "fail")
                    }
                },1150L)
            }

            tvLogin.setOnClickListener {
                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
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