package com.vodovoz.app.ui.components.fragment.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ActivityMainBinding
import com.vodovoz.app.databinding.FragmentMainBinding
import com.vodovoz.app.ui.components.fragment.home.HomeFragment
import com.vodovoz.app.util.Keys
import com.vodovoz.app.util.LogSettings
import com.yandex.mapkit.MapKitFactory

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMainBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        setupBottomNav()
    }.root

    private fun setupBottomNav() {
        val navHostFragment = (childFragmentManager.findFragmentById(R.id.mainContainer)) as NavHostFragment
        val navController = navHostFragment.navController

        binding.navView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.homeFragment -> navController.navigate(R.id.homeFragment, bundleOf(Pair(HomeFragment.IS_SHOW_POPUP_NEWS, false)))
                else -> navController.navigate(menuItem.itemId)
            }
            true
        }

        navController.addOnDestinationChangedListener { _, destination, args ->
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
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(requireActivity(), listOf(Manifest.permission.ACCESS_FINE_LOCATION).toTypedArray(), 0)

                // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

}