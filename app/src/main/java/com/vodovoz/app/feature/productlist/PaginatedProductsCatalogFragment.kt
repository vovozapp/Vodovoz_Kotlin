package com.vodovoz.app.feature.productlist

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
import com.vodovoz.app.databinding.FragmentProductsFlowBinding
import com.vodovoz.app.feature.favorite.FavoriteFlowViewModel
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.productlist.brand.BrandFlowClickListener
import com.vodovoz.app.feature.productlist.brand.BrandFlowController
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.FilterValueUI
import com.vodovoz.app.ui.model.SortTypeListUI
import com.vodovoz.app.ui.model.SortTypeUI
import com.vodovoz.app.ui.model.custom.FiltersBundleUI
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PaginatedProductsCatalogFragment : BaseFragment() {

    companion object {
        const val CATEGORY_ID = "CATEGORY_ID"
        const val SORT_TYPE = "SORT_TYPE"
        const val FILTER_BUNDLE = "FILTER_BUNDLE"
    }

    override fun layout(): Int = R.layout.fragment_products_flow

    private val binding: FragmentProductsFlowBinding by viewBinding {
        FragmentProductsFlowBinding.bind(
            contentView
        )
    }

    internal val viewModel: ProductsListFlowViewModel by viewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    @Inject
    lateinit var ratingProductManager: RatingProductManager

    private val space: Int by lazy { resources.getDimension(R.dimen.space_16).toInt() }

    private val brandController by lazy { BrandFlowController(brandClickListener(), space) }
    private val productsListFlowController by lazy {
        ProductsListFlowController(
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

        brandController.bind(binding.brandRecycler)
        productsListFlowController.bind(binding.productRecycler)

        observeUiState()
        observeResultLiveData()
        observeChangeLayoutManager()
        initBackButton()
        initSearch()

        bindErrorRefresh {
            viewModel.refreshSorted()
        }
    }

    private fun initSearch() {
        initSearchToolbar(
            { findNavController().navigate(PaginatedProductsCatalogFragmentDirections.actionToSearchFragment()) },
            { findNavController().navigate(PaginatedProductsCatalogFragmentDirections.actionToSearchFragment()) },
            { navigateToQrCodeFragment() },
            { startSpeechRecognizer() },
            true
        )
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

    private fun observeChangeLayoutManager() {
        lifecycleScope.launchWhenStarted {
            viewModel
                .observeChangeLayoutManager()
                .collect {
                    productsListFlowController.changeLayoutManager(
                        it,
                        binding.productRecycler,
                        binding.imgViewMode
                    )
                }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect { state ->

                    bindHeader(state.data)

                    if (state.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                        binding.sortContainer.isVisible = true
                        binding.appBar.elevation = 4F
                    }

                    bindShare(state.data.categoryHeader)

                    val data = state.data
                    if (state.bottomItem != null && state.data.layoutManager == FavoriteFlowViewModel.LINEAR) {
                        productsListFlowController.submitList(data.itemsList + state.bottomItem)
                    } else {
                        productsListFlowController.submitList(data.itemsList)
                    }

                    if (state.error is ErrorState.Empty) {
                        binding.emptyErrorTv.isVisible = true
                    } else {
                        binding.emptyErrorTv.isVisible = false
                        showError(state.error)
                    }
                }
        }
    }

    private fun bindShare(categoryUI: CategoryUI?) {
        if (categoryUI == null) return

        if (categoryUI.shareUrl.isNotEmpty()) {
            binding.imgShare.isVisible = true
            binding.imgShare.setOnClickListener {
                val intent = Intent.createChooser(
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, categoryUI.shareUrl)
                    },
                    "Shearing Option"
                )
                startActivity(intent)
            }
        } else {
            binding.imgShare.isVisible = false
        }
    }

    private fun bindHeader(state: ProductsListFlowViewModel.ProductsListState) {

        if (state.categoryHeader == null) return

        binding.imgViewMode.setOnClickListener { viewModel.changeLayoutManager() }
        binding.tvSort.visibility = View.INVISIBLE
        state.categoryHeader.sortTypeList?.let {sortTypeList->
            binding.tvSort.setOnClickListener {
                showBottomSortSettings(
                    state.sortType,
                    sortTypeList
                )
            }
            binding.tvSort.text = state.sortType.sortName
            binding.tvSort.visibility = View.VISIBLE
        }
        binding.imgFilters.setOnClickListener {
            val filterBundle = state.filterBundle
            val id = state.categoryId
            if (id == 0L) return@setOnClickListener
            showAllFiltersFragment(filterBundle, id)
        }

        binding.changeCategoryContainer.isVisible = state.showCategoryContainer

        binding.categoryContainer.setOnClickListener {
            val id = state.categoryId
            if (id == 0L) return@setOnClickListener
            showSingleRootCatalogCatalog(id)
        }

        when (state.filtersAmount) {
            0 -> binding.tvFiltersAmount.visibility = View.INVISIBLE
            else -> {
                binding.tvFiltersAmount.text = state.filtersAmount.toString()
                binding.tvFiltersAmount.visibility = View.VISIBLE
            }
        }


        binding.tvCategoryName.text = state.categoryHeader.name
        binding.tvProductAmount.text = state.categoryHeader.productAmount.toString()
        binding.additionalName.text = state.categoryHeader.primaryFilterName

        val brandList = state.categoryHeader.primaryFilterValueList

        when (brandList.isNotEmpty()) {
            true -> {
                binding.brandTabsContainer.visibility = View.VISIBLE
            }
            else -> {
                binding.brandTabsContainer.visibility = View.GONE
            }
        }
        brandController.submitList(brandList)
    }

    private fun brandClickListener(): BrandFlowClickListener {
        return object : BrandFlowClickListener {
            override fun onBrandClick(filterValue: FilterValueUI) {
                viewModel.addPrimaryFilterValue(filterValue)
            }

        }
    }

    private fun showAllFiltersFragment(filterBundle: FiltersBundleUI, id: Long) =
        findNavController().navigate(
            PaginatedProductsCatalogFragmentDirections.actionToProductFiltersFragment(
                filterBundle,
                id
            )
        )

    private fun showSingleRootCatalogCatalog(id: Long) = findNavController().navigate(
        PaginatedProductsCatalogFragmentDirections.actionToSingleRootCatalogBottomFragment(id)
    )

    private fun showBottomSortSettings(
        sortType: SortTypeUI,
        sortTypeList: SortTypeListUI?,
    ) = sortTypeList?.let {
        findNavController().navigate(
            PaginatedProductsCatalogFragmentDirections.actionToSortProductsSettingsBottomFragment(
                sortType,
                it
            )
        )
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(CATEGORY_ID)?.observe(viewLifecycleOwner) { categoryId ->
                viewModel.updateByCat(categoryId)
            }

        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<SortTypeUI>(SORT_TYPE)?.observe(viewLifecycleOwner) { sortType ->
                viewModel.updateBySortType(sortType)
            }

        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<FiltersBundleUI>(FILTER_BUNDLE)
            ?.observe(viewLifecycleOwner) { filterBundle ->
                viewModel.updateFilterBundle(filterBundle)
            }
    }

    private fun getProductsClickListener(): ProductsClickListener {
        return object : ProductsClickListener {
            override fun onProductClick(id: Long) {
                findNavController().navigate(
                    PaginatedProductsCatalogFragmentDirections.actionToProductDetailFragment(
                        id
                    )
                )
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                findNavController().navigate(
                    PaginatedProductsCatalogFragmentDirections.actionToPreOrderBS(
                        id,
                        name,
                        detailPicture
                    )
                )
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

    private val permissionsController by lazy {
        PermissionsController(requireContext())
    }

    private fun navigateToQrCodeFragment() {
        permissionsController.methodRequiresCameraPermission(requireActivity()) {
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
        permissionsController.methodRequiresRecordAudioPermission(requireActivity()) {
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