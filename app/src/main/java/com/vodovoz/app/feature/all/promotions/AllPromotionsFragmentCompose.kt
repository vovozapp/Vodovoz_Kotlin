package com.vodovoz.app.feature.all.promotions

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.placeholders.NetworkErrorPlaceholder
import com.vodovoz.app.feature.all.AllClickListener
import com.vodovoz.app.feature.home.banneradvinfo.BannerAdvInfoBottomSheetFragment
import com.vodovoz.app.feature.home.viewholders.homepromotions.model.PromotionAdvEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class AllPromotionsFragment : Fragment() {

    companion object {
        const val PROMOTION_FILTER = "PROMOTION_FILTER"
    }

    private val viewModel: AllPromotionsFlowViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoadSorted()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val navController = findNavController()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.Default)
            setContent {
                VodovozTheme {
                    val viewState by viewModel.observeUiState().collectAsStateWithLifecycle()

                    when (viewState.data.uiState) {
                        AllPromotionsFlowViewModel.UiState.Error -> {
                            NetworkErrorPlaceholder { viewModel.firstLoadSorted() }
                        }

                        else -> {
                            AllPromotionsScreen(
                                viewModel = viewModel,
                                viewState = viewState.data,
                                navController = navController
                            )
                        }
                    }

                }
            }
        }
    }

    sealed class DataSource : Parcelable {
        @Parcelize
        class ByBanner(val categoryId: Long) : DataSource()

        @Parcelize
        data object All : DataSource()
    }

}
