package dev.furkankavak.istemenu

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        setStatusBarColor()

        // Get the bottom navigation view
        bottomNav = findViewById(R.id.bottomNavigation)

        // Set up navigation controller
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set up navigation once - don't do it multiple times
        setupBottomNavigation()

        // Add destination change listener
        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateBottomNavigationVisibility(destination)
        }
    }

    private fun setStatusBarColor() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.pink_very_light)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            true
    }

    private fun setupBottomNavigation() {
        // Set up the bottom navigation with nav controller ONCE
        bottomNav.setupWithNavController(navController)

        // Prevent double-clicking navigation items
        bottomNav.setOnItemReselectedListener { /* Do nothing on reselect */ }
    }

    private fun updateBottomNavigationVisibility(destination: NavDestination) {
        val showBottomNav = when (destination.id) {
            R.id.loginFragment, R.id.signUpFragment, R.id.splashFragment -> false
            else -> true
        }

        // Only update visibility - don't reset the controller setup
        bottomNav.visibility = if (showBottomNav) View.VISIBLE else View.GONE
    }
}