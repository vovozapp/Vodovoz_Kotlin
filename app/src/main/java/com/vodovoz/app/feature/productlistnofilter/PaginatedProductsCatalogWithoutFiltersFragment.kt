package com.vodovoz.app.feature.productlistnofilter

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.permissions.PermissionsController
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.common.speechrecognizer.SpeechDialogFragment
import com.vodovoz.app.databinding.FragmentProductsWithoutFiltersFlowBinding
import com.vodovoz.app.feature.favorite.FavoriteFlowViewModel
import com.vodovoz.app.feature.favorite.categorytabsdadapter.CategoryTabsFlowClickListener
import com.vodovoz.app.feature.favorite.categorytabsdadapter.CategoryTabsFlowController
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.SortTypeListUI
import com.vodovoz.app.ui.model.SortTypeUI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject

@AndroidEntryPoint
class PaginatedProductsCatalogWithoutFiltersFragment : BaseFragment() {

    companion object {
        const val CATEGORY_ID = "CATEGORY_ID"
        const val SORT_TYPE = "SORT_TYPE"
    }

    override fun layout(): Int = R.layout.fragment_products_without_filters_flow

    private val binding: FragmentProductsWithoutFiltersFlowBinding by viewBinding {
        FragmentProductsWithoutFiltersFlowBinding.bind(
            contentView
        )
    }

    internal val viewModel: ProductsListNoFilterFlowViewModel by viewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    @Inject
    lateinit var ratingProductManager: RatingProductManager

    private val space: Int by lazy { resources.getDimension(R.dimen.space_16).toInt() }

    private val categoryTabsController by lazy {
        CategoryTabsFlowController(categoryTabsClickListener(), space)
    }

    private val productsListNoFilterFlowController by lazy {
        ProductsListNoFilterFlowController(
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
        productsListNoFilterFlowController.bind(binding.productRecycler)

        observeUiState()
        observeResultLiveData()
        observeChangeLayoutManager()
        initBackButton()
        initSearch()
        bindErrorRefresh {
            viewModel.refreshSorted()
        }
    }

    private fun categoryTabsClickListener(): CategoryTabsFlowClickListener {
        return object : CategoryTabsFlowClickListener {
            override fun onTabClick(id: Long) {
                viewModel.onTabClick(id)
            }
        }
    }

    private fun initSearch() {
        initSearchToolbar(
            { findNavController().navigate(PaginatedProductsCatalogWithoutFiltersFragmentDirections.actionToSearchFragment()) },
            { findNavController().navigate(PaginatedProductsCatalogWithoutFiltersFragmentDirections.actionToSearchFragment()) },
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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel
                    .observeChangeLayoutManager()
                    .collect {
                        productsListNoFilterFlowController.changeLayoutManager(
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
                            binding.sortContainer.isVisible = true
                            binding.appBar.elevation = 4F
                        }

                        bindShare(state.data.categoryHeader)

                        val data = state.data
                        if (state.bottomItem != null && state.data.layoutManager == FavoriteFlowViewModel.LINEAR) {
                            productsListNoFilterFlowController.submitList(data.itemsList + state.bottomItem)
                        } else {
                            productsListNoFilterFlowController.submitList(data.itemsList)
                        }

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

    private fun bindHeader(state: ProductsListNoFilterFlowViewModel.ProductListNoFilterState) {

        if (state.categoryHeader == null) return

        val categoryUIList = state.categoryHeader.categoryUIList

        when (categoryUIList.isNotEmpty()) {
            true -> {
                binding.categoriesRecycler.visibility = View.VISIBLE
                binding.imgCategories.visibility = View.VISIBLE
            }
            else -> {
                binding.imgCategories.visibility = View.GONE
                binding.categoriesRecycler.visibility = View.GONE
            }
        }
        categoryTabsController.submitList(categoryUIList, "")

        binding.imgViewMode.setOnClickListener { viewModel.changeLayoutManager() }

        binding.tvSort.visibility = View.INVISIBLE
        state.categoryHeader.sortTypeList?.let { sortTypeList ->
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
            val category = state.categoryHeader// ?: return@setOnClickListener
            val id = state.selectedCategoryId //?: return@setOnClickListener
            showMiniCatalog(category, id)
        }

        binding.tvCategoryName.text = state.categoryHeader.name
        binding.tvProductAmount.text = state.categoryHeader.productAmount.toString()

    }

    private fun showBottomSortSettings(
        sortType: SortTypeUI,
        sortTypeList: SortTypeListUI?,
    ) = sortTypeList?.let {
        findNavController().navigate(
            PaginatedProductsCatalogWithoutFiltersFragmentDirections.actionToSortProductsSettingsBottomFragment(
                sortType,
                it
            )
        )
    }

    private fun showMiniCatalog(categoryUI: CategoryUI, id: Long) = findNavController().navigate(
        PaginatedProductsCatalogWithoutFiltersFragmentDirections.actionToMiniCatalogBottomFragment(
            categoryUI.categoryUIList.toTypedArray(),
            id
        )
    )

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(CATEGORY_ID)
            ?.observe(viewLifecycleOwner) { categoryId ->
                viewModel.updateByCat(categoryId)
            }

        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<SortTypeUI>(SORT_TYPE)
            ?.observe(viewLifecycleOwner) { sortType ->
                viewModel.updateBySortType(sortType)
                binding.tvSort.text = sortType.sortName
            }
    }

    private fun getProductsClickListener(): ProductsClickListener {
        return object : ProductsClickListener {
            override fun onProductClick(id: Long) {
                findNavController().navigate(
                    PaginatedProductsCatalogWithoutFiltersFragmentDirections.actionToProductDetailFragment(
                        id
                    )
                )
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                findNavController().navigate(
                    PaginatedProductsCatalogWithoutFiltersFragmentDirections.actionToPreOrderBS(
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

    sealed class DataSource : Serializable {
        class Brand(val brandId: Long) : DataSource()
        class Country(val countryId: Long) : DataSource()
        class Discount : DataSource()
        class Novelties : DataSource()
        class Slider(val categoryId: Long) : DataSource()
    }

}
