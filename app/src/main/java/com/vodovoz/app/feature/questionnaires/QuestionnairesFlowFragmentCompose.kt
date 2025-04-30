package com.vodovoz.app.feature.questionnaires

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vodovoz.app.R
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.core.navigation.navigateToWebView
import com.vodovoz.app.core.network.ApiConfig
import com.vodovoz.app.core.network.interceptor.BaseUrlInterceptor
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.placeholders.LoadingPlaceholder
import com.vodovoz.app.design_system.composables.placeholders.NetworkErrorPlaceholder
import com.vodovoz.app.design_system.composables.placeholders.VodovozLongPlaceholder
import com.vodovoz.app.design_system.effects.LifecycleEffect
import com.vodovoz.app.feature.cart.CartFlowViewModel
import com.vodovoz.app.feature.catalog.CatalogFlowViewModel
import com.vodovoz.app.feature.favorite.FavoriteFlowViewModel
import com.vodovoz.app.feature.home.HomeFlowViewModel
import com.vodovoz.app.feature.profile.ProfileFlowViewModel
import com.vodovoz.app.feature.sitestate.SiteStateManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class QuestionnairesFlowFragment : Fragment() {

    private val viewModel: QuestionnairesFlowViewModel by viewModels()

    @Inject
    lateinit var tabManager: TabManager
    private val homeViewModel: HomeFlowViewModel by activityViewModels()
    private val cartFlowViewModel: CartFlowViewModel by activityViewModels()
    private val favoriteViewModel: FavoriteFlowViewModel by activityViewModels()
    private val catalogFlowViewModel: CatalogFlowViewModel by activityViewModels()
    private val profileViewModel: ProfileFlowViewModel by activityViewModels()

    @Inject
    lateinit var baseUrlInterceptor: BaseUrlInterceptor

    @Inject
    lateinit var siteStateManager: SiteStateManager

    private var threeFingerTouchCount = 0

    override fun onStart() {
        super.onStart()
        tabManager.changeTabVisibility(false)
    }

    override fun onStop() {
        super.onStop()
        tabManager.changeTabVisibility(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchQuestionnaireTypes()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.Default)

            setContent {
                VodovozTheme {
                    val pagingState by viewModel.observeUiState().collectAsStateWithLifecycle()
                    val viewState by rememberUpdatedState(newValue = pagingState.data)
                    val scrollState = rememberScrollState()

                    when (val uiState = viewState.uiState) {
                        QuestionnairesFlowViewModel.QuestionnairesUiState.Body -> {
                            QuestionnairesScreen(
                                viewModel = viewModel,
                                viewState = viewState,
                                scrollState = scrollState)
                        }

                        QuestionnairesFlowViewModel.QuestionnairesUiState.Loading -> {
                            LoadingPlaceholder()
                        }

                        is QuestionnairesFlowViewModel.QuestionnairesUiState.Success -> {
                            VodovozLongPlaceholder(
                                data = uiState.placeholder,
                                onCloseClick = {
                                    viewModel.navigateBack()
                                },
                                onButtonClick = {
                                    viewModel.navigateBack()
                                }
                            )
                        }

                        is QuestionnairesFlowViewModel.QuestionnairesUiState.Welcome -> {
                            WelcomeQuestionnairesScreen(
                                uiState = uiState,
                                onWelcomeButtonClick = { btn ->
                                    viewModel.activateWelcomeButton(btn)
                                },
                                onBackClick = {
                                    viewModel.navigateBack()
                                }
                            )
                        }

                        QuestionnairesFlowViewModel.QuestionnairesUiState.Error -> {
                            NetworkErrorPlaceholder { viewModel.fetchWelcomeDetails() }
                        }
                    }


                    LifecycleEffect {
                        viewModel.observeEvent().collect { event ->
                            when (event) {
                                QuestionnairesFlowViewModel.QuestionnaireEvents.GoBack -> {
                                    findNavController().popBackStack()
                                }

                                is QuestionnairesFlowViewModel.QuestionnaireEvents.GoToWebView -> {
                                    findNavController().navigateToWebView(
                                        event.url,
                                        requireContext().getString(R.string.space)
                                    )
                                }

                                QuestionnairesFlowViewModel.QuestionnaireEvents.ScrollToTop -> {
                                    launch {
                                        scrollState.animateScrollTo(0)
                                    }
                                }
                            }
                        }
                    }

                    BackHandler {
                        viewModel.navigateBack()
                    }
                }
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackDoor()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setBackDoor() {
        view?.setOnTouchListener { _, event ->
            val handled = true
            val action = event.action and MotionEvent.ACTION_MASK
            val count = event.pointerCount
            if (action == MotionEvent.ACTION_POINTER_DOWN) {
                if (3 == count) {
                    threeFingerTouchCount++
                    if (threeFingerTouchCount == 3) {
                        threeFingerTouchCount = 0
                        MaterialAlertDialogBuilder(requireContext())
                            .setMessage("Выберете текущий путь к серверу")
                            .setNegativeButton("Рабочий") { dialog, _ ->
                                dialog.dismiss()
                                loadHomeFragmentWithNewServerURL(null)
                            }
                            .setPositiveButton("Тестовый") { dialog, _ ->
                                dialog.dismiss()
                                lifecycleScope.launch {
                                    siteStateManager.requestSiteState()
                                    siteStateManager.siteStateFlow.collect { state ->
                                        if (state != null) {
                                            val newLink = "${state.testUrl}/"
                                            loadHomeFragmentWithNewServerURL(newLink)
                                        }
                                    }
                                }
                            }.show()
                    }
                }
            }
            return@setOnTouchListener handled
        }
    }

    private fun loadHomeFragmentWithNewServerURL(serverUrl: String?) {
        ApiConfig.VODOVOZ_URL = serverUrl ?: ""

        if (serverUrl == null) {
            baseUrlInterceptor.clear()
        } else {
            baseUrlInterceptor.updateBaseUrl(serverUrl)
        }

        lifecycleScope.launch {
            delay(1000)
            homeViewModel.refresh()
            cartFlowViewModel.refresh()
            favoriteViewModel.refresh()
            catalogFlowViewModel.refresh()
            profileViewModel.refresh()
            tabManager.selectTab(R.id.graph_home)
        }
    }
}