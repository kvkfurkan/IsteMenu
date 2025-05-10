package dev.furkankavak.istemenu.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.furkankavak.istemenu.R
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

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.menu.findItem(R.id.navigation_profile).isChecked = true

        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_daily -> {
                    findNavController().navigate(R.id.homeFragment)
                    true
                }

                R.id.navigation_weekly -> {
                    findNavController().navigate(R.id.weeklyFragment)
                    true
                }

                R.id.navigation_profile -> true
                else -> false
            }
        }


        binding.bottomNavigation.selectedItemId = R.id.navigation_profile
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}