package com.vodovoz.app.feature.favorite

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.permissions.PermissionsController
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.common.speechrecognizer.SpeechDialogFragment
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.core.navigation.navigateToCategories
import com.vodovoz.app.core.navigation.navigateToProductDetails
import com.vodovoz.app.core.navigation.navigateToSearch
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.placeholders.NetworkErrorPlaceholder
import com.vodovoz.app.design_system.effects.LifecycleEffect
import com.vodovoz.app.feature.home.model.CategoryUi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteFragment : Fragment() {


    internal val viewModel: FavoriteFlowViewModel by activityViewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    @Inject
    lateinit var ratingProductManager: RatingProductManager

    @Inject
    lateinit var tabManager: TabManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchFavoriteProducts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.checkFavoritesChanges()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        findNavController().currentBackStackEntry?.savedStateHandle?.remove<CategoryUi>(
            "category"
        )?.let { category -> viewModel.selectCategory(category) }
            ?: viewModel.checkFavoritesChanges()

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.Default)
            setContent {
                VodovozTheme {
                    val pagingState by viewModel.observeUiState().collectAsStateWithLifecycle()
                    val viewState = pagingState.data
                    val lazyGridState = rememberLazyGridState()

                    when (viewState.uiState) {
                        FavoriteFlowViewModel.FavoriteUiState.Error -> {
                            NetworkErrorPlaceholder(onTryAgainClick = { viewModel.fetchFavoriteProducts() })
                        }

                        else -> {
                            FavoriteScreen(
                                viewModel = viewModel,
                                viewState = viewState,
                                lazyGridState = lazyGridState
                            )
                        }
                    }


                    LifecycleEffect {
                        observeEvents(lazyGridState)
                    }

//                    LifecycleEffect {
//                        viewModel.listenFavorites()
//                    }

                }
            }
        }
    }

    private suspend fun observeEvents(lazyGridState: LazyGridState) {
        viewModel.observeEvent()
            .collect { event ->
                when (event) {
                    is FavoriteFlowViewModel.FavoriteEvents.GoToProfile -> {
                        tabManager.setAuthRedirect(findNavController().graph.id)
                        tabManager.selectTab(R.id.graph_profile)
                    }

                    is FavoriteFlowViewModel.FavoriteEvents.GoToPreOrder -> {
                        if (findNavController().currentBackStackEntry?.destination?.id == R.id.preOrderBS) {
                            findNavController().popBackStack()
                        }
                        findNavController().navigate(
                            FavoriteFragmentDirections.actionToPreOrderBS(
                                event.id,
                                event.name,
                                event.detailPicture
                            )
                        )
                    }

                    is FavoriteFlowViewModel.FavoriteEvents.GoToCategories -> {
                        findNavController().navigateToCategories(
                            categories = event.categories,
                            category = event.category,
                        )
                    }

                    is FavoriteFlowViewModel.FavoriteEvents.GoToProductDetails -> {
                        findNavController().navigateToProductDetails(event.productId)
                    }

                    FavoriteFlowViewModel.FavoriteEvents.ScrollToTop -> {
                        lazyGridState.animateScrollToItem(0)
                    }

                    FavoriteFlowViewModel.FavoriteEvents.GoToSearch -> {
                        findNavController().navigateToSearch()
                    }
                }
            }


    }


    @Inject
    lateinit var permissionsControllerFactory: PermissionsController.Factory
    private val permissionsController by lazy { permissionsControllerFactory.create(requireActivity()) }

    private fun navigateToQrCodeFragment() {
        permissionsController.methodRequiresCameraPermission {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@methodRequiresCameraPermission
            }

            findNavController().navigate(R.id.qrCodeFragment)

        }
    }

    private fun startSpeechRecognizer() {
        permissionsController.methodRequiresRecordAudioPermission {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@methodRequiresRecordAudioPermission
            }

            SpeechDialogFragment().show(childFragmentManager, "TAG")
        }
    }

}
