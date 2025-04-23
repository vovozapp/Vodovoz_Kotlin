package com.vodovoz.app.feature.all.orders.detail

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.effects.LifecycleEffect
import com.vodovoz.app.feature.all.orders.detail.composables.AboutOrderBottomSheet
import com.vodovoz.app.util.extensions.copyText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class OrderDetailsFragment : Fragment() {

    internal val viewModel: OrderDetailsFlowViewModel by viewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    @Inject
    lateinit var accountManager: AccountManager

    @Inject
    lateinit var ratingProductManager: RatingProductManager

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.Default)

            setContent {
                VodovozTheme {
                    val pagingState by viewModel.observeUiState().collectAsStateWithLifecycle()
                    val viewState by rememberUpdatedState(newValue = pagingState.data)

                    OrderDetailsScreen(
                        viewModel = viewModel,
                        viewState = viewState
                    )

                    val currentAboutOrder = viewState.currentAboutOrderBS
                    if(viewState.showAboutOrderBS && currentAboutOrder != null){
                        AboutOrderBottomSheet(data = currentAboutOrder) {
                            viewModel.closeAboutOrderBottomSheet()
                        }
                    }


                    LifecycleEffect {
                        viewModel.observeEvent().collectLatest { event ->
                            when (event) {
                                is OrderDetailsFlowViewModel.OrderDetailsEvent.CopyText -> {
                                    requireContext().copyText(event.text)
                                }

                                OrderDetailsFlowViewModel.OrderDetailsEvent.GoBack -> {
                                    findNavController().popBackStack()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
