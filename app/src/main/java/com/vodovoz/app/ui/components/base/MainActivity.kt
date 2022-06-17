package com.vodovoz.app.ui.components.base

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private var bottomMenuHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply { setContentView(root) }
        
        setupBottomNav()

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
    }

    private fun setupBottomNav() {
        navController = (supportFragmentManager.findFragmentById(R.id.mainContainer) as NavHostFragment).navController
        binding.navView.setupWithNavController(navController)

        binding.navView.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.navView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    bottomMenuHeight = binding.navView.height
                }
            }
        )

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.filtersFragment,
                R.id.concreteFilterFragment,
                R.id.loginFragment,
                R.id.registerFragment -> {
                    binding.navView.visibility = View.GONE
                    binding.mainContainer.setPadding(0, 0, 0, 0)
                }
                else -> {
                    if (binding.navView.visibility == View.GONE) {
                        binding.navView.visibility = View.VISIBLE
                        binding.mainContainer.setPadding(0, 0, 0, bottomMenuHeight)
                    }
                }
            }
        }
    }

}