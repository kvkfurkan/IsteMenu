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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        setStatusBarColor()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


        setupBottomNavigation()

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
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)


        bottomNav.setupWithNavController(navController)
    }

    private fun updateBottomNavigationVisibility(destination: NavDestination) {
        val showBottomNav = when (destination.id) {
            R.id.loginFragment, R.id.signUpFragment, R.id.splashFragment -> false
            else -> true
        }

        findViewById<BottomNavigationView>(R.id.bottomNavigation)?.let { bottomNav ->
            bottomNav.visibility = if (showBottomNav) View.VISIBLE else View.GONE

            if (showBottomNav) {
                bottomNav.setupWithNavController(navController)
            }
        }
    }
}