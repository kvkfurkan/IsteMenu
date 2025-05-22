package dev.furkankavak.istemenu.fragments

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.furkankavak.istemenu.R
import dev.furkankavak.istemenu.databinding.DialogDevelopersBinding
import dev.furkankavak.istemenu.databinding.FragmentProfileBinding
import dev.furkankavak.istemenu.model.AuthManager

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var authManager: AuthManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authManager = AuthManager(requireContext())

        // Set the status bar color to orange_very_light
        activity?.window?.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.orange_very_light)

        // Set user email if available
        authManager.getUserEmail()?.let { email ->
            binding.tvEmail.text = email
        }

        setupListeners()
    }

    private fun setupListeners() {
        // Set up logout button
        binding.btnLogout.setOnClickListener {
            performLogout()
        }

        // Set up developers button
        binding.btnDevelopers.setOnClickListener {
            showDevelopersDialog()
        }
    }

    private fun performLogout() {
        // Show loading
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogout.isEnabled = false
        binding.btnDevelopers.isEnabled = false

        // Clear token directly
        authManager.clearToken()

        // Show message
        Toast.makeText(context, "Çıkış yapılıyor...", Toast.LENGTH_SHORT).show()

        // Small delay to ensure progress bar is visible
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToLogin()
        }, 500)
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
    }

    private fun showDevelopersDialog() {
        val dialog = Dialog(requireContext())
        val dialogBinding = DialogDevelopersBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        // Set up close button
        dialogBinding.btnClose.setOnClickListener {
            dialog.dismiss()
        }

        // Make dialog width match parent
        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        dialog.show()
    }

    override fun onPause() {
        super.onPause()
        // Reset status bar color when navigating away from this fragment
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}