package com.vodovoz.app.feature.profile.waterapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.feature.profile.waterapp.composables.WaterAppUserDataScreen
import com.vodovoz.app.feature.profile.waterapp.composables.WaterAppWelcomeScreen
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppUiState
import com.vodovoz.app.util.extensions.addOnBackPressedCallback
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WaterAppFragment : Fragment() {

    private val viewModel: WaterAppViewModel by viewModels()

    @Inject
    lateinit var tabManager: TabManager

    @Inject
    lateinit var waterAppHelper: WaterAppHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        waterAppHelper.fetchWaterAppUserData()
        waterAppHelper.fetchWaterAppNotificationData()
        waterAppHelper.fetchWaterAppRateData()
    }

    override fun onStart() {
        super.onStart()
        tabManager.changeTabVisibility(false)
    }

    override fun onStop() {
        super.onStop()
        debugLog { "save water rate" }
        waterAppHelper.saveWaterAppRateData()
        tabManager.changeTabVisibility(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                VodovozTheme {
                    val pagingState by viewModel.observeUiState().collectAsStateWithLifecycle()
                    val viewState by rememberUpdatedState(newValue = pagingState.data)

                    AnimatedContent(
                        targetState = viewState.uiState,
                        label = "Animated Water app screens"
                    ) { uiState ->
                        when (uiState) {
                            WaterAppUiState.GoalCompleted -> TODO()
                            WaterAppUiState.Main -> TODO()
                            WaterAppUiState.Settings -> TODO()
                            is WaterAppUiState.UserData -> {
                                WaterAppUserDataScreen(
                                    userDataStage = uiState,
                                    userData = viewState.userData,
                                    onGenderSelect = { isMan -> viewModel.selectGender(isMan) },
                                    onBackClick = {},
                                    onCloseClick = { })
                            }

                            WaterAppUiState.Welcome -> {
                                WaterAppWelcomeScreen(
                                    onCloseClick = {
                                        viewModel.navigateBack()
                                    },
                                    onStartClick = {
                                        viewModel.moveToUserFields()
                                    }
                                )
                            }
                        }

                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = waterAppHelper.fetchAppNotificationData()
        bindBackPressed()
    }

    private fun bindBackPressed() {
        addOnBackPressedCallback {
            findNavController().popBackStack()
        }
    }

}