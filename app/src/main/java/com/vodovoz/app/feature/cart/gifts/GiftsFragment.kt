package com.vodovoz.app.feature.cart.gifts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.effects.LifecycleEffect
import com.vodovoz.app.feature.cart.gifts.model.GiftsEvent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GiftsFragment : Fragment() {

    private val viewModel: GiftsViewModel by viewModels()

    @Inject
    lateinit var tabManager: TabManager

    override fun onStart() {
        super.onStart()
        tabManager.changeTabVisibility(false)
    }

    override fun onStop() {
        super.onStop()
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
                    val viewState by viewModel.state.collectAsStateWithLifecycle()

                    GiftsScreen(viewModel = viewModel, viewState = viewState)

                    LifecycleEffect {
                        viewModel.events.collect { event ->
                            when (event) {
                                GiftsEvent.GoBack -> {
                                    findNavController().popBackStack()
                                }

                                is GiftsEvent.GoToCart -> {
                                    val navController = findNavController()

                                    navController.previousBackStackEntry?.savedStateHandle?.set(
                                        "gift", event.currentGift
                                    )
                                    navController.popBackStack()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}