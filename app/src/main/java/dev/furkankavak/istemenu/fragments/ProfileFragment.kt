package dev.furkankavak.istemenu.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.furkankavak.istemenu.R
import dev.furkankavak.istemenu.databinding.DialogDevelopersBinding
import dev.furkankavak.istemenu.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    private fun setupListeners() {
        // Set up logout button
        binding.btnLogout.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }

        // Set up developers button
        binding.btnDevelopers.setOnClickListener {
            showDevelopersDialog()
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}