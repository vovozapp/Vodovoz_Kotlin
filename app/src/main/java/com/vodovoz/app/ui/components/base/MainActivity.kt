package com.vodovoz.app.ui.components.base

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
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

    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, listOf(Manifest.permission.ACCESS_FINE_LOCATION).toTypedArray(), 0)

                // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

}