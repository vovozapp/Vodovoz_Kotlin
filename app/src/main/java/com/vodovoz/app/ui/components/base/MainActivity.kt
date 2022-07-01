package com.vodovoz.app.ui.components.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply { setContentView(root) }
        
        setupBottomNav()

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
    }

    private fun setupBottomNav() {
        navController = (supportFragmentManager.findFragmentById(R.id.mainContainer) as NavHostFragment).navController
        binding.navView.setOnItemSelectedListener { menuItem ->
            navController.navigate(menuItem.itemId)
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
//            when(destination.id) {
//                R.id.homeFragment,
//                R.id.catalogFragment,
//                R.id.cartFragment,
//                R.id.accountFragment -> showBottomNavigation()
//                R.id.filtersFragment,
//                R.id.concreteFilterFragment,
//                R.id.loginFragment,
//                R.id.registerFragment -> hideBottomNavigation()
//            }
        }
    }

    private fun hideBottomNavigation() {
        binding.navView.visibility = View.GONE
    }

    private fun showBottomNavigation() {
        binding.navView.visibility = View.VISIBLE
    }

}