package com.vodovoz.app.feature.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.databinding.FragmentSplashBinding
import com.vodovoz.app.feature.cart.CartFlowViewModel
import com.vodovoz.app.feature.catalog.CatalogFlowViewModel
import com.vodovoz.app.feature.favorite.FavoriteFlowViewModel
import com.vodovoz.app.feature.home.HomeFlowViewModel
import com.vodovoz.app.feature.profile.ProfileFlowViewModel
import com.vodovoz.app.feature.sitestate.SiteStateManager
import com.vodovoz.app.ui.extensions.ContextExtensions.isTablet
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_splash

    private val binding: FragmentSplashBinding by viewBinding {
        FragmentSplashBinding.bind(contentView)
    }

    private val viewModel: SplashViewModel by viewModels()
    private val flowViewModel: HomeFlowViewModel by activityViewModels()
    private val catalogViewModel: CatalogFlowViewModel by activityViewModels()
    private val cartFlowViewModel: CartFlowViewModel by activityViewModels()
    private val favoriteViewModel: FavoriteFlowViewModel by activityViewModels()
    private val profileViewModel: ProfileFlowViewModel by activityViewModels()

    @Inject
    lateinit var accountManager: AccountManager

    @Inject
    lateinit var siteStateManager: SiteStateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firstLoad()
    }

    private fun firstLoad() {
        lifecycleScope.launchWhenStarted {
            siteStateManager.requestSiteState()
        }
        viewModel.sendFirebaseToken()
        flowViewModel.firstLoad()
        catalogViewModel.firstLoad()
        cartFlowViewModel.firstLoad()
        favoriteViewModel.firstLoad()
        favoriteViewModel.firstLoadSorted()
        profileViewModel.fetchFirstUserData()
        accountManager.fetchAccountId()
    }

    private fun refreshLoad() {
        lifecycleScope.launchWhenStarted {
            siteStateManager.requestSiteState()
        }
        viewModel.sendFirebaseToken()
        flowViewModel.refresh()
        catalogViewModel.refresh()
        cartFlowViewModel.refreshIdle()
        favoriteViewModel.refreshIdle()
        profileViewModel.refresh()
        accountManager.fetchAccountId()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //accountManager.reportYandexMetrica("Зашел в приложение") //todo релиз

        if (requireContext().isTablet()) {
            binding.lottieSplashView.cancelAnimation()
            binding.lottieSplashView.visibility = View.GONE
            binding.logoLayout.visibility = View.VISIBLE
        }

        handlePushData()
        observeFlowViewModel()
        bindErrorRefresh {
            refreshLoad()
        }
    }

    private fun handlePushData() {
        debugLog { "splash args $arguments" }
        if (arguments != null) {
            debugLog { "splash containsKey ${requireArguments().containsKey("push")}" }
            if (requireArguments().containsKey("push")) {
                val extra = requireArguments().getString("push")
                debugLog { "splash get push extra $extra" }
                if (!extra.isNullOrEmpty()) {
                    lifecycleScope.launchWhenCreated {
                        siteStateManager.savePushData(JSONObject(extra))
                    }
                }
            }
        }
    }

    private fun observeFlowViewModel() {
        lifecycleScope.launchWhenStarted {
            flowViewModel.observeUiState()
                .collect { state ->
                    if (state.isFirstLoad) {
                        if (state.error is ErrorState.NetworkError) {
                            showError(state.error)
                        } else {
                            checkSiteStateWithNavigate()
                        }
                    }

                }
        }
    }

    private suspend fun checkSiteStateWithNavigate() {
        val active = siteStateManager.fetchSiteStateActive()
        debugLog { "site state active $active" }
        if (active) {
            flowViewModel.secondLoad()
            findNavController().navigate(R.id.mainFragment)
        } else {
            findNavController().navigate(R.id.blockAppFragment)
        }
    }
}