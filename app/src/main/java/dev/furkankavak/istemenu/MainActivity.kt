package dev.furkankavak.istemenu

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.furkankavak.istemenu.network.RetrofitClient

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize RetrofitClient with application context
        RetrofitClient.init(applicationContext)

        bottomNav = findViewById(R.id.bottomNavigation)


        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


        setupBottomNavigation()


        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateBottomNavigationVisibility(destination)
        }
    }

    private fun setupBottomNavigation() {
        bottomNav.setupWithNavController(navController)

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