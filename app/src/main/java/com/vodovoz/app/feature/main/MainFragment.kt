package com.vodovoz.app.feature.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.notification.NotificationConfig
import com.vodovoz.app.common.permissions.PermissionsController
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.core.navigation.setupWithNavController
import com.vodovoz.app.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : BaseFragment() {

    @Inject
    lateinit var tabManager: TabManager


    private val permissionsController by lazy {
        PermissionsController(requireContext())
    }

    private val viewModel: MainViewModel by viewModels()

    override fun layout(): Int = R.layout.fragment_main

    private val binding: FragmentMainBinding by viewBinding {
        FragmentMainBinding.bind(
            contentView
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeTabState()
        observeCartState()
        observeProfileState()
        observeCartLoading()
        observeTabVisibility()
    }

    override fun onStart() {
        super.onStart()
        if (!viewModel.isBottomBarInited) {
            setupBottomNavigationBar()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.isBottomBarInited = false
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

    private fun observeTabVisibility() {
        lifecycleScope.launchWhenStarted {
            tabManager
                .observeTabVisibility()
                .collect {
                    binding.nvNavigation.isVisible = it
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

    private fun observeProfileState() {
        lifecycleScope.launchWhenStarted {
            tabManager
                .observeBottomNavProfileState()
                .collect { state ->
                    if (state == null) {
                        binding.circleAmountProfile.isVisible = false
                    } else {
                        binding.circleAmountProfile.text = state.toString()
                        binding.circleAmountProfile.isVisible = true
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
        permissionsController.methodRequiresLocationsPermission(requireActivity())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsController.methodRequiresNotificationPermission(requireActivity())
        }
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {

        viewModel.isBottomBarInited = true

        val navGraphIds = listOfNotNull(
            R.navigation.nav_graph_home,
            R.navigation.nav_graph_catalog,
            R.navigation.nav_graph_cart,
            R.navigation.nav_graph_favorite,
            R.navigation.nav_graph_profile
        )

        // Setup the bottom navigation view with a list of navigation graphs
        binding.nvNavigation.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = childFragmentManager,
            containerId = R.id.fgvContainer,
            intent = requireActivity().intent,
            recyclerViewToTop = {
                tabManager.reselect(it)
            },
            activity = requireActivity(),
            lifecycleOwner = viewLifecycleOwner
        ).observe(viewLifecycleOwner) {
            Navigation.setViewNavController(requireView(), it)
        }
    }
}