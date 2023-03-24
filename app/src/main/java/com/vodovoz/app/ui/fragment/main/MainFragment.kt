package com.vodovoz.app.ui.fragment.main

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vodovoz.app.R
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.databinding.FragmentMainBinding
import com.vodovoz.app.feature.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    @Inject
    lateinit var tabManager: TabManager

    private lateinit var binding: FragmentMainBinding

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
        val navHostFragment =
            (childFragmentManager.findFragmentById(R.id.fgvContainer)) as NavHostFragment
        val navController = navHostFragment.navController

        binding.nvNavigation.setSetupWithNavController(navController)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            tabManager
                .observeTabState()
                .collect {
                    binding.nvNavigation.selectedItemId = it
                }
        }

        lifecycleScope.launchWhenStarted {
            tabManager
                .observeBottomNavCartState()
                .collect {state ->
                    if (state == null) {
                        binding.circleAmount.isVisible = false
                        binding.nvNavigation.menu.getItem(2).title = "Корзина"
                    } else {
                        binding.circleAmount.text = state.count.toString()
                        binding.circleAmount.isVisible = true
                        binding.nvNavigation.menu.getItem(2).title = state.total.toString() + " ₽"
                    }
                }
        }
    }

    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(
                    requireActivity(),
                    listOf(Manifest.permission.ACCESS_FINE_LOCATION).toTypedArray(),
                    0
                )

                // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    private fun BottomNavigationView.setSetupWithNavController(navController: NavController?) {
        navController?.let {
            setupWithNavController(navController)
        }

        setOnItemSelectedListener { menuItem ->
            val builder = NavOptions.Builder().setLaunchSingleTop(true).setRestoreState(true)
            val graph = navController?.currentDestination?.parent
            val destination = graph?.findNode(menuItem.itemId)

            if (menuItem.order and Menu.CATEGORY_SECONDARY == 0) {
                navController?.graph?.findStartDestination()?.id?.let {
                    builder.setPopUpTo(
                        it,
                        inclusive = false,
                        saveState = true
                    )
                }
            }

            val options = builder.build()
            destination?.id?.let { id -> navController.navigate(id, null, options) }

            return@setOnItemSelectedListener true
        }
    }
}