package com.vodovoz.app.feature.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.feature.cart.CartFlowViewModel
import com.vodovoz.app.feature.home.HomeFlowViewModel
import com.vodovoz.app.feature.catalog.CatalogFlowViewModel
import com.vodovoz.app.feature.favorite.FavoriteFlowViewModel
import com.vodovoz.app.feature.profile.ProfileFlowViewModel
import com.vodovoz.app.feature.sitestate.SiteStateManager
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_splash

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
        lifecycleScope.launchWhenStarted {
            siteStateManager.requestSiteState()
        }
        viewModel.sendFirebaseToken()
        flowViewModel.firstLoad()
        flowViewModel.secondLoad()
        catalogViewModel.firstLoad()
        cartFlowViewModel.firstLoad()
        favoriteViewModel.firstLoad()
        favoriteViewModel.firstLoadSorted()
        profileViewModel.firstLoad()
        accountManager.fetchAccountId()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeFlowViewModel()
    }

    private fun observeFlowViewModel() {
        lifecycleScope.launchWhenStarted {
            flowViewModel.observeUiState()
                .collect { state ->
                    debugLog {
                        "${state.data.items.map { it.position }}"
                    }
                    if (state.isFirstLoad) {
                        val active = siteStateManager.fetchSiteStateActive()
                        debugLog { "site state active $active" }
                        if (active) {
                            findNavController().navigate(R.id.mainFragment)
                        } else {
                            findNavController().navigate(R.id.blockAppFragment)
                        }
                    }
                    if (state.error is ErrorState.NetworkError) {
                        showError(state.error)
                    }
                }
        }
    }
}