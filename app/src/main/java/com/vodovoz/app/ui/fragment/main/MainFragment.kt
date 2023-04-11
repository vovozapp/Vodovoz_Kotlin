package com.vodovoz.app.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.permissions.LocationController
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.databinding.FragmentMainBinding
import com.vodovoz.app.databinding.FragmentMainHomeFlowBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : BaseFragment() {

    @Inject
    lateinit var tabManager: TabManager


    private val locationController by lazy {
        LocationController(requireContext())
    }

    override fun layout(): Int = R.layout.fragment_main

    private val binding: FragmentMainBinding by viewBinding {
        FragmentMainBinding.bind(
            contentView
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomNav()
        observeTabState()
        observeCartState()
        observeCartLoading()
    }

    private fun observeCartLoading() {
        lifecycleScope.launchWhenStarted {
            tabManager
                .observeAddToCartLoading()
                .collect { state ->
                    if (state == null || state.count == 0) {
                        binding.circleAmount.isVisible = false
                        binding.nvNavigation.menu.getItem(2).title = "Корзина"
                    } else {
                        binding.circleAmount.text = state.count.toString()
                        binding.circleAmount.isVisible = true
                        binding.nvNavigation.menu.getItem(2).title = "..."
                    }
                }
        }
    }

    private fun observeCartState() {
        lifecycleScope.launchWhenStarted {
            tabManager
                .observeBottomNavCartState()
                .collect { state ->
                    if (state == null || state.count == 0) {
                        binding.circleAmount.isVisible = false
                        binding.nvNavigation.menu.getItem(2).title = "Корзина"
                    } else {
                        binding.circleAmount.text = state.count.toString()
                        binding.circleAmount.isVisible = true
                        binding.circleAmount
                            .animate()
                            .scaleX(1.4f)
                            .scaleY(1.4f)
                            .setDuration(300)
                            .setInterpolator(AccelerateInterpolator())
                            .withEndAction {
                                binding.circleAmount.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                            }
                            .start()
                        binding.nvNavigation.menu.getItem(2).title = state.total.toString() + " ₽"
                    }
                }
        }
    }

    private fun observeTabState() {
        lifecycleScope.launchWhenStarted {
            tabManager
                .observeTabState()
                .collect {
                    binding.nvNavigation.selectedItemId = it
                }
        }
    }

    override fun onResume() {
        super.onResume()
        locationController.methodRequiresTwoPermission(requireActivity())
    }

    private fun setupBottomNav() {
        val navHostFragment =
            (childFragmentManager.findFragmentById(R.id.fgvContainer)) as NavHostFragment
        val navController = navHostFragment.navController

        binding.nvNavigation.setSetupWithNavController(navController)
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