package com.vodovoz.app.feature.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.account.data.ReloginManager
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
import com.vodovoz.app.util.SplashFileConfig
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.FileInputStream
import java.io.InputStream
import javax.inject.Inject


@AndroidEntryPoint
class SplashFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_splash

    private val binding: FragmentSplashBinding by viewBinding {
        FragmentSplashBinding.bind(contentView)
    }

    private val viewModel: SplashViewModel by viewModels()
    private val homeViewModel: HomeFlowViewModel by activityViewModels()
    private val catalogViewModel: CatalogFlowViewModel by activityViewModels()
    private val cartFlowViewModel: CartFlowViewModel by activityViewModels()
    private val favoriteViewModel: FavoriteFlowViewModel by activityViewModels()
    private val profileViewModel: ProfileFlowViewModel by activityViewModels()

    @Inject
    lateinit var accountManager: AccountManager

    @Inject
    lateinit var siteStateManager: SiteStateManager

    @Inject
    lateinit var reloginManager: ReloginManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firstLoad()
    }

    private fun firstLoad() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                siteStateManager.requestSiteState()
            }
        }

        lifecycleScope.launch {
            reloginManager.userReloginEnded.collect {
                when (it) {
                    is ReloginManager.ReloginState.ReloginSuccess -> {
                        cartFlowViewModel.firstLoad()
                        profileViewModel.fetchFirstUserData()
                        viewModel.sendFirebaseToken()
                    }
                    is ReloginManager.ReloginState.ReloginError -> {
                        showError(ErrorState.NetworkError())
                    }
                    else -> {}
                }
            }
        }
        homeViewModel.firstLoad()
        catalogViewModel.firstLoad()
        favoriteViewModel.firstLoad()
        favoriteViewModel.firstLoadSorted()
    }

    private fun refreshLoad() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                siteStateManager.requestSiteState()
            }
        }
        viewModel.sendFirebaseToken()
        homeViewModel.refresh()
        catalogViewModel.refresh()
        cartFlowViewModel.refreshIdle()
        favoriteViewModel.refreshIdle()
        profileViewModel.refresh()
        accountManager.fetchAccountId()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        accountManager.reportYandexMetrica("Зашел в приложение")

        if (requireContext().isTablet()) {
            binding.lottieSplashView.cancelAnimation()
            binding.lottieSplashView.visibility = View.GONE
            binding.logoLayout.visibility = View.VISIBLE
        } else {
            binding.lottieSplashView.setOutlineMasksAndMattes(true)
            binding.lottieSplashView.enableMergePathsForKitKatAndAbove(true)
            val file = SplashFileConfig.getSplashFile(requireContext())
            if (!file.exists()) {
                debugLog { "file is not exist" }
                binding.lottieSplashView.visibility = View.GONE
                binding.logoLayout.visibility = View.VISIBLE
            } else {
                initAnimation()
            }
        }

        handlePushData()
        observeHomeViewModel()
        bindErrorRefresh {
            refreshLoad()
        }
    }

    private fun initAnimation() {
        val localFile = SplashFileConfig.getSplashFile(requireContext())
        val inputStream: InputStream = FileInputStream(localFile)
        if (localFile.exists()) {
            binding.lottieSplashView.setAnimation(inputStream, null)
            binding.lottieSplashView.playAnimation()
        } else {
            debugLog { "setAnimationFromLocal but file not exist" }
        }
    }

//    private suspend fun download(link: String, path: String, doAfter: (String) -> Unit) {
//        withContext(Dispatchers.IO) {
//            URL(link).openStream()
//        }.use { input ->
//            val file = File(requireContext().filesDir, path)
//            if (!file.exists()) {
//                file.createNewFile()
//            }
//            FileOutputStream(file).use { output ->
//                input.copyTo(output)
//                doAfter(path)
//            }
//
//        }
//    }

    private fun handlePushData() {
        debugLog { "splash args $arguments" }
        if (arguments != null) {
            debugLog { "splash containsKey ${requireArguments().containsKey("push")}" }
            if (requireArguments().containsKey("push")) {
                val extra = requireArguments().getString("push")
                debugLog { "splash get push extra $extra" }
                if (!extra.isNullOrEmpty()) {
                    lifecycleScope.launch {
                        repeatOnLifecycle(Lifecycle.State.CREATED) {
                            siteStateManager.savePushData(JSONObject(extra))
                        }
                    }
                }
            }
        }
    }

    private fun observeHomeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.observeUiState()
                    .collect { state ->
                        if (state.isFirstLoad) {
                            if (state.error is ErrorState.NetworkError) {
                                showError(state.error)
                            } else {
                                checkSiteStateWithNavigate(state.data.isSecondLoad)
                            }
                        }

                    }
            }
        }
    }

    private suspend fun checkSiteStateWithNavigate(isSecond: Boolean) {
        if (isSecond) {
            findNavController().navigate(R.id.mainFragment)
            return
        }
        val active = siteStateManager.fetchSiteStateActive()
        debugLog { "site state active $active" }
        if (active) {
            homeViewModel.secondLoad()
            findNavController().navigate(R.id.mainFragment)
        } else {
            findNavController().navigate(R.id.blockAppFragment)
        }
    }
}