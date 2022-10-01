package com.vodovoz.app.ui.fragment.full_screen_history_slider
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.R
import com.vodovoz.app.data.model.common.ActionEntity
import com.vodovoz.app.databinding.FragmentFullscreenHistorySliderBinding
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseDialogFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.fragment.all_promotions.AllPromotionsFragment
import com.vodovoz.app.ui.fragment.paginated_products_catalog_without_filters.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.ui.fragment.products_catalog.ProductsCatalogFragment
import com.vodovoz.app.ui.interfaces.IOnChangeHistory
import com.vodovoz.app.ui.interfaces.IOnInvokeAction

class FullScreenHistoriesSliderFragment : ViewStateBaseDialogFragment(),
    IOnChangeHistory, IOnInvokeAction
{

    private lateinit var binding: FragmentFullscreenHistorySliderBinding
    private lateinit var viewModel: FullScreenHistoriesSliderViewModel

    private var startHistoryId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogWithoutStatusBar)
        initViewModel()
        getArgs()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[FullScreenHistoriesSliderViewModel::class.java]
        viewModel.updateData()
    }

    private fun getArgs() {
        FullScreenHistoriesSliderFragmentArgs.fromBundle(requireArguments()).let { args ->
            startHistoryId = args.startHistoryId
        }
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentFullscreenHistorySliderBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Success -> onStateSuccess()
                is ViewState.Hide -> onStateHide()
                is ViewState.Loading -> onStateLoading()
                is ViewState.Error -> onStateError(state.errorMessage)
            }
        }

        viewModel.historyUIListLD.observe(viewLifecycleOwner) { historyUIList ->
            binding.vpHistories.adapter = HistoriesDetailStateAdapter(
                fragment = this,
                historyUIList = historyUIList
            )
            binding.vpHistories.currentItem = historyUIList.indexOfFirst { it.id == startHistoryId }
        }
    }

    override fun nextHistory() {
        if (binding.vpHistories.currentItem == viewModel.historyUIList.indices.last) {
            findNavController().popBackStack()
        } else {
            binding.vpHistories.currentItem = binding.vpHistories.currentItem + 1
        }
    }

    override fun previousHistory() {
        binding.vpHistories.currentItem = binding.vpHistories.currentItem -1
    }

    override fun close() {
        findNavController().popBackStack()
    }

    override fun update() {
        viewModel.updateData()
    }

    override fun onInvokeAction(actionEntity: ActionEntity) {
        actionEntity.invoke(findNavController(), requireActivity())
    }

    private fun ActionEntity.invoke(navController: NavController, activity: FragmentActivity)  {
        val navDirect = when(this) {
            is ActionEntity.Brand ->
                FullScreenHistoriesSliderFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                    PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Brand(brandId = this.brandId)
                )
            is ActionEntity.Brands -> {
                FullScreenHistoriesSliderFragmentDirections.actionToAllBrandsFragment().also { navDirect ->
                    navDirect.brandIdList = this.brandIdList.toLongArray()
                }
            }
            is ActionEntity.Product ->
                FullScreenHistoriesSliderFragmentDirections.actionToProductDetailFragment(this.productId)
            is ActionEntity.Products ->
                FullScreenHistoriesSliderFragmentDirections.actionToProductsCatalogFragment(
                    ProductsCatalogFragment.DataSource.BannerProducts(categoryId = this.categoryId)
                )
            is ActionEntity.Promotion ->
                FullScreenHistoriesSliderFragmentDirections.actionToPromotionDetailFragment(this.promotionId)
            is ActionEntity.Promotions -> FullScreenHistoriesSliderFragmentDirections.actionToAllPromotionsFragment(
                AllPromotionsFragment.DataSource.ByBanner(this.categoryId)
            )
            is ActionEntity.AllPromotions -> FullScreenHistoriesSliderFragmentDirections.actionToAllPromotionsFragment(
                AllPromotionsFragment.DataSource.All()
            )
            is ActionEntity.Link -> {
                val openLinkIntent = Intent(Intent.ACTION_VIEW, Uri.parse(this.url))
                activity.startActivity(openLinkIntent)
                null
            }
            is ActionEntity.Category ->
                FullScreenHistoriesSliderFragmentDirections.actionToPaginatedProductsCatalogFragment(this.categoryId)
            is ActionEntity.Discount -> FullScreenHistoriesSliderFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Discount()
            )
            is ActionEntity.Novelties -> FullScreenHistoriesSliderFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Novelties()
            )
        }
        navDirect?.let { navController.navigate(navDirect) }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}