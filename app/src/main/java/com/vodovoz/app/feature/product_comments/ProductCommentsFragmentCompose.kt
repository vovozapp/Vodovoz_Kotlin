package com.vodovoz.app.feature.product_comments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.vodovoz.app.R
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.effects.LifecycleEffect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProductCommentsFragment : Fragment() {

    private val viewModel: ProductCommentsFlowViewModel by viewModels()

    @Inject
    lateinit var tabManager: TabManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoadSorted()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.Default)
            setContent {
                val viewState by viewModel.observeUiState().collectAsStateWithLifecycle()
                val lazyListState = rememberLazyListState()

                VodovozTheme {
                    ProductCommentsScreen(
                        viewModel = viewModel,
                        viewState = viewState.data,
                        lazyListState = lazyListState
                    )


                    LifecycleEffect {
                        viewModel.observeEvent().collect { event ->
                            when (event) {
                                ProductCommentsFlowViewModel.ProductCommentsEvents.GoToProfile -> {

                                }

                                ProductCommentsFlowViewModel.ProductCommentsEvents.ScrollToTop -> {
                                    lazyListState.animateScrollToItem(0)
                                }

                                ProductCommentsFlowViewModel.ProductCommentsEvents.SendComment -> {

                                }

                                ProductCommentsFlowViewModel.ProductCommentsEvents.GoBack -> {
                                    findNavController().popBackStack()
                                }
                            }

                        }
                    }

                    BackHandler { viewModel.navigateBack() }
                }

            }
        }
    }
}
