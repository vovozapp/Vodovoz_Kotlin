package com.vodovoz.app.feature.productlistnofilter

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.permissions.PermissionsController
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.common.speechrecognizer.SpeechDialogFragment
import com.vodovoz.app.core.navigation.navigateToCategories
import com.vodovoz.app.core.navigation.navigateToProductDetails
import com.vodovoz.app.core.navigation.navigateToSearch
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.effects.LifecycleEffect
import com.vodovoz.app.feature.catalog.model.CatalogCategoryUi
import com.vodovoz.app.feature.home.model.CategoryUi
import com.vodovoz.app.ui.model.CategoryUI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@AndroidEntryPoint
class PaginatedProductsCatalogWithoutFiltersFragment : Fragment() {

    internal val viewModel: ProductsListNoFilterFlowViewModel by viewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    @Inject
    lateinit var ratingProductManager: RatingProductManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchProductListData()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        findNavController().currentBackStackEntry?.savedStateHandle?.remove<CategoryUi>("category")?.let { category ->
            viewModel.selectCategory(category)
        }

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.Default)
            setContent {
                VodovozTheme {
                    val pagingState by viewModel.observeUiState().collectAsStateWithLifecycle()
                    val viewState = pagingState.data
                    val lazyGridState = rememberLazyGridState()

                    ProductsNoFiltersScreen(
                        viewModel = viewModel,
                        viewState = viewState,
                        lazyGridState = lazyGridState
                    )

                    LifecycleEffect {
                        viewModel.observeEvent().collect { event ->
                            when (event) {
                                ProductsListNoFilterFlowViewModel.ProductListNoFilterEvent.GoBack -> {
                                    findNavController().popBackStack()
                                }

                                is ProductsListNoFilterFlowViewModel.ProductListNoFilterEvent.GoToSearch -> {
                                    findNavController().navigateToSearch(event.query)
                                }

                                is ProductsListNoFilterFlowViewModel.ProductListNoFilterEvent.GoToCategories -> {
                                    findNavController().navigateToCategories(
                                        category = event.currentCategory,
                                        categories = event.categories
                                    )
                                }

                                is ProductsListNoFilterFlowViewModel.ProductListNoFilterEvent.GoToProductDetails -> {
                                    findNavController().navigateToProductDetails(event.productId)
                                }

                                ProductsListNoFilterFlowViewModel.ProductListNoFilterEvent.ScrollToTop -> {
                                    lazyGridState.animateScrollToItem(0);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBackButton()
    }


    private fun initBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            }
        )
    }


    private fun shapeProductsCatalog(categoryUI: CategoryUI?) {
        if (categoryUI == null || categoryUI.shareUrl.isEmpty()) return

        runCatching {
            val intent = Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, categoryUI.shareUrl)
                },
                "Shearing Option"
            )
            startActivity(intent)
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

    sealed class DataSource : Parcelable {
        @Parcelize
        class Brand(val brandId: Long) : DataSource()

        @Parcelize
        class Country(val countryId: Long) : DataSource()

        @Parcelize
        data object HurryBuyUpProducts : DataSource()

        @Parcelize
        data object NewProducts : DataSource()

        @Parcelize
        data object ViewedProducts : DataSource()

        @Parcelize
        data class ButtonProducts(val buttonId: Int) : DataSource()

        @Parcelize
        data class Products(val groupId: Int, val blockId: Int) : DataSource()

        @Parcelize
        data class Search(val query: String) : DataSource()

        //TODO - fix
        @Parcelize
        data class Category(val catalogCategory: CatalogCategoryUi) : DataSource()

        @Parcelize
        data object Missing : DataSource()
    }

}
