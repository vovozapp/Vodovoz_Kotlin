package com.vodovoz.app.feature.favorite

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.permissions.PermissionsController
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.common.speechrecognizer.SpeechDialogFragment
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.databinding.FragmentMainFavoriteFlowBinding
import com.vodovoz.app.feature.favorite.bestforyouadapter.BestForYouController
import com.vodovoz.app.feature.favorite.categorytabsdadapter.CategoryTabsFlowClickListener
import com.vodovoz.app.feature.favorite.categorytabsdadapter.CategoryTabsFlowController
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitle
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.productlistnofilter.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.feature.products_slider.ProductsSliderConfig
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.SortTypeListUI
import com.vodovoz.app.ui.model.SortTypeUI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_main_favorite_flow

    private val binding: FragmentMainFavoriteFlowBinding by viewBinding {
        FragmentMainFavoriteFlowBinding.bind(
            contentView
        )
    }

    internal val viewModel: FavoriteFlowViewModel by activityViewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    @Inject
    lateinit var ratingProductManager: RatingProductManager

    @Inject
    lateinit var tabManager: TabManager

    private val space: Int by lazy { resources.getDimension(R.dimen.space_16).toInt() }

    private val categoryTabsController by lazy {
        CategoryTabsFlowController(categoryTabsClickListener(), space)
    }

    private val bestForYouController by lazy {
        BestForYouController(
            cartManager,
            likeManager,
            getProductsShowClickListener(),
            getProductsClickListener()
        )
    }
    private val favoritesController by lazy {
        FavoritesListController(
            viewModel,
            cartManager,
            likeManager,
            getProductsClickListener(),
            requireContext(),
            ratingProductManager
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoad()
        viewModel.firstLoadSorted()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryTabsController.bind(binding.categoriesRecycler)
        bestForYouController.bind(binding.bestForYouRv)
        favoritesController.bind(binding.productRecycler, binding.refreshEmptyFavoriteContainer)
        viewModel.clearScrollState()
        observeUiState()
        observeEvents()
        observeResultLiveData()
        initSearch()
        observeChangeLayoutManager()
        bindErrorRefresh {
            viewModel.refreshSorted()
        }
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel
                    .observeEvent()
                    .collect {
                        when (it) {
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
                                        it.id,
                                        it.name,
                                        it.detailPicture
                                    )
                                )
                            }
                        }
                    }
            }
        }
    }

    private fun observeChangeLayoutManager() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel
                    .observeChangeLayoutManager()
                    .collect {
                        favoritesController.changeLayoutManager(
                            it,
                            binding.productRecycler,
                            binding.imgViewMode
                        )
                    }
            }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeUiState()
                    .collect { state ->

                        bindHeader(state.data)

                        if (state.loadingPage) {
                            showLoader()
                        } else {
                            hideLoader()
                        }

                        val data = state.data
                        if (state.bottomItem != null && state.data.layoutManager == FavoriteFlowViewModel.LINEAR && state.page != 2) {
                            favoritesController.submitList(data.itemsList + state.bottomItem)
                        } else {
                            favoritesController.submitList(data.itemsList)
                        }
                        if (data.scrollToTop) {
                            binding.productRecycler.scrollToPosition(0)
                        }

                        if (state.error !is ErrorState.Empty) {
                            showError(state.error)
                        }

                    }
            }
        }
    }

    private fun bindHeader(state: FavoriteFlowViewModel.FavoriteState) {

        binding.imgViewMode.setOnClickListener { viewModel.changeLayoutManager() }

        if (state.favoriteCategory != null) {
            showContainer(true)
        }

        if (state.bestForYouCategoryDetailUI != null) {
            val homeProducts = HomeProducts(
                2,
                listOf(state.bestForYouCategoryDetailUI),
                ProductsSliderConfig(
                    containShowAllButton = false,
                    largeTitle = true
                ),
                HomeProducts.DISCOUNT,
                prodList = state.bestForYouCategoryDetailUI.productUIList
            )
            val homeTitle = HomeTitle(
                id = 1,
                type = HomeTitle.VIEWED_TITLE,
                name = "Лучшее для вас",
                showAll = false,
                showAllName = "СМ.ВСЕ",
                categoryProductsName = state.bestForYouCategoryDetailUI.name
            )
            bestForYouController.submitList(listOf(homeTitle, homeProducts))
            showContainer(false)
        }

        val categoryUiList = state.favoriteCategory?.categoryUIList ?: emptyList()

        if (state.isAvailable) {
            bindTabsVisibility(categoryUiList.isNotEmpty())
        } else {
            bindTabsVisibility(false)
        }

        categoryTabsController.submitList(categoryUiList, "")

        binding.tvEmptyTitle.text = state.emptyTitle
        binding.tvEmptyMessage.text = state.emptyMessage

        binding.tvCategoryName.text = state.favoriteCategory?.name
        binding.tvProductAmount.text = state.favoriteCategory?.productAmount.toString()
        binding.availableTitle.text = state.availableTitle
        binding.notAvailableTitle.text = state.notAvailableTitle
        binding.availableContainer.isVisible =
            state.availableTitle != null || state.notAvailableTitle != null

        binding.tvSort.visibility = View.INVISIBLE
        state.favoriteCategory?.sortTypeList?.let { sortTypeList ->
            binding.tvSort.setOnClickListener {
                showBottomSortSettings(
                    state.sortType,
                    sortTypeList
                )
            }
            binding.tvSort.text = state.sortType.sortName
            binding.tvSort.visibility = View.VISIBLE
        }
        binding.imgCategories.setOnClickListener {
            val category = state.favoriteCategory ?: return@setOnClickListener
            val id = state.selectedCategoryId //?: return@setOnClickListener
            showMiniCatalog(category, id)
        }

        binding.availableButton.setOnClickListener {
            binding.availableButton.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            binding.availableButton.elevation = resources.getDimension(R.dimen.elevation_1)
            binding.notAvailableButton.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.light_gray
                )
            )
            binding.notAvailableButton.elevation = 0f
            viewModel.updateByIsAvailable(true)
        }

        binding.notAvailableButton.setOnClickListener {
            binding.notAvailableButton.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            binding.notAvailableButton.elevation = resources.getDimension(R.dimen.elevation_1)
            binding.availableButton.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.light_gray
                )
            )
            binding.availableButton.elevation = 0f
            viewModel.updateByIsAvailable(false)
        }

    }

    private fun bindTabsVisibility(vis: Boolean) {
        when (vis) {
            true -> {
                binding.categoriesRecycler.visibility = View.VISIBLE
                binding.imgCategories.visibility = View.VISIBLE
            }

            else -> {
                binding.imgCategories.visibility = View.GONE
                binding.categoriesRecycler.visibility = View.GONE
            }
        }
    }

    private fun showContainer(bool: Boolean) {
        binding.emptyFavoriteContainer.isVisible = !bool
        binding.favoriteContainer.isVisible = bool
    }

    private fun categoryTabsClickListener(): CategoryTabsFlowClickListener {
        return object : CategoryTabsFlowClickListener {
            override fun onTabClick(id: Long) {
                viewModel.onTabClick(id)
            }
        }
    }

    private fun showMiniCatalog(categoryUI: CategoryUI, id: Long) = findNavController().navigate(
        FavoriteFragmentDirections.actionToMiniCatalogBottomFragment(
            categoryUI.categoryUIList.toTypedArray(),
            id
        )
    )

    private fun showBottomSortSettings(
        sortType: SortTypeUI,
        sortTypeList: SortTypeListUI?,
    ) = sortTypeList?.let {
        findNavController().navigate(
            FavoriteFragmentDirections.actionToSortProductsSettingsBottomFragment(
                sortType,
                it
            )
        )
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(PaginatedProductsCatalogWithoutFiltersFragment.CATEGORY_ID)
            ?.observe(viewLifecycleOwner) { categoryId ->
                viewModel.onTabClick(categoryId)
            }

        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<SortTypeUI>(PaginatedProductsCatalogWithoutFiltersFragment.SORT_TYPE)
            ?.observe(viewLifecycleOwner) { sortType ->
                viewModel.updateBySortType(sortType)
            }
    }

    private fun getProductsClickListener(): ProductsClickListener {
        return object : ProductsClickListener {
            override fun onProductClick(id: Long) {
                findNavController().navigate(
                    FavoriteFragmentDirections.actionToProductDetailFragment(
                        id
                    )
                )
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                viewModel.onPreOrderClick(id, name, detailPicture)
            }

            override fun onChangeProductQuantity(id: Long, cartQuantity: Int, oldQuantity: Int) {
                viewModel.changeCart(id, cartQuantity, oldQuantity)
            }

            override fun onFavoriteClick(id: Long, isFavorite: Boolean) {
                viewModel.changeFavoriteStatus(id, isFavorite)
            }

            override fun onChangeRating(id: Long, rating: Float, oldRating: Float) {
                viewModel.changeRating(id, rating, oldRating)
            }
        }
    }

    private fun getProductsShowClickListener(): ProductsShowAllListener {
        return object : ProductsShowAllListener {
            override fun showAllDiscountProducts(id: Long) {}
            override fun showAllTopProducts(id: Long) {}
            override fun showAllNoveltiesProducts(id: Long) {}
            override fun showAllBottomProducts(id: Long) {}
        }
    }

    private fun initSearch() {
        initSearchToolbar(
            { findNavController().navigate(FavoriteFragmentDirections.actionToSearchFragment()) },
            { findNavController().navigate(FavoriteFragmentDirections.actionToSearchFragment()) },
            { navigateToQrCodeFragment() },
            { startSpeechRecognizer() }
        )
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
