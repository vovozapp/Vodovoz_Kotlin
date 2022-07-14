package com.vodovoz.app.ui.fragment.promotion_details

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentPromotionDetailBinding
import com.vodovoz.app.ui.adapter.LinearProductsAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.decoration.ListMarginDecoration
import com.vodovoz.app.ui.diffUtils.ProductDiffUtilCallback
import com.vodovoz.app.ui.fragment.home.HomeFragmentDirections
import com.vodovoz.app.ui.fragment.paginated_products_catalog_without_filters.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderConfig
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderFragment
import com.vodovoz.app.ui.extensions.ScrollViewExtensions.setScrollElevation
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.PromotionDetailUI
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class PromotionDetailsFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentPromotionDetailBinding
    private lateinit var viewModel: PromotionDetailsViewModel

    private val bestForYouProductsSliderFragment: ProductsSliderFragment by lazy {
        ProductsSliderFragment.newInstance(ProductsSliderConfig(
            containShowAllButton = false
        )) }

    private val compositeDisposable = CompositeDisposable()

    private val onProductClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val onChangeProductQuantitySubject: PublishSubject<Pair<Long, Int>> = PublishSubject.create()
    private val onFavoriteClickSubject: PublishSubject<Pair<Long, Boolean>> = PublishSubject.create()

    private val linearProductAdapter = LinearProductsAdapter(
        onProductClickSubject = onProductClickSubject,
        onChangeProductQuantitySubject = onChangeProductQuantitySubject,
        onFavoriteClickSubject = onFavoriteClickSubject
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        getArgs()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[PromotionDetailsViewModel::class.java]
    }

    private fun getArgs() {
        viewModel.updateArgs(PromotionDetailsFragmentArgs.fromBundle(requireArguments()).promotionId)
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentPromotionDetailBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initActionBar()
        initPromotionProductRecycler()
        initBestForYouProductsSliderFragment()
        observeViewModel()
    }

    private fun initActionBar() {
        with((requireActivity() as AppCompatActivity)) {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initPromotionProductRecycler() {
        binding.productRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.productRecycler.adapter = linearProductAdapter
        binding.contentContainer.setScrollElevation(binding.appBar)
        binding.productRecycler.addItemDecoration(ListMarginDecoration(
            resources.getDimension(R.dimen.primary_space).toInt()
        ))
    }

    private fun initBestForYouProductsSliderFragment() {
        bestForYouProductsSliderFragment.initCallbacks(
            iOnProductClick = { productId -> onProductClickSubject.onNext(productId) },
            iOnChangeProductQuantity = { pair -> onChangeProductQuantitySubject.onNext(pair) },
            iOnFavoriteClick = { pair -> onFavoriteClickSubject.onNext(pair) },
            iOnShowAllProductsClick = {
                findNavController().navigate(HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                    PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Discount()
                ))
            }
        )

        childFragmentManager.beginTransaction().replace(
            R.id.bestForYouProductsSliderFragment,
            bestForYouProductsSliderFragment
        ).commit()

    }

    override fun update() {
        viewModel.updateData()
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Loading -> onStateLoading()
                is ViewState.Hide -> onStateHide()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Success -> onStateSuccess()
            }
        }

        viewModel.promotionDetailUILD.observe(viewLifecycleOwner) { promotionDetailUI ->
            fillView(promotionDetailUI)
        }

        viewModel.errorLD.observe(viewLifecycleOwner) { errorMessage ->
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun fillView(promotionDetailUI: PromotionDetailUI) {
        fillPromotionHeader(promotionDetailUI)
        fillPromotionProductRecycler(promotionDetailUI.promotionCategoryDetailUI)
        fillForYouProductSlider(promotionDetailUI.forYouCategoryDetailUI)
    }

    private fun fillPromotionHeader(promotionDetailUI: PromotionDetailUI) {
        binding.toolbar.title = promotionDetailUI.name
        binding.customerCategoryCard.setCardBackgroundColor(Color.parseColor(promotionDetailUI.statusColor))
        binding.customerCategory.text = promotionDetailUI.status
        binding.timeLeft.text = promotionDetailUI.timeLeft
        binding.detail.text = HtmlCompat.fromHtml(promotionDetailUI.detailText, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
        Glide.with(requireContext())
            .load(promotionDetailUI.detailPicture)
            .into(binding.image)
    }

    private fun fillForYouProductSlider(categoryDetailUI: CategoryDetailUI) {
        bestForYouProductsSliderFragment.updateData(listOf(categoryDetailUI))
    }

    private fun fillPromotionProductRecycler(categoryDetailUI: CategoryDetailUI?) {
        when(categoryDetailUI) {
            null -> {
                binding.promotionProductsTitle.visibility = View.GONE
                binding.productRecycler.visibility = View.GONE
            }
            else -> {
                val diffUtil = ProductDiffUtilCallback(
                    oldList = linearProductAdapter.productUIList,
                    newList = categoryDetailUI.productUIList
                )

                DiffUtil.calculateDiff(diffUtil).let { diffResult ->
                    linearProductAdapter.productUIList = categoryDetailUI.productUIList
                    diffResult.dispatchUpdatesTo(linearProductAdapter)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        onProductClickSubject.subscribeBy { productId ->
            findNavController().navigate(PromotionDetailsFragmentDirections
                .actionToProductDetailFragment(productId))
        }.addTo(compositeDisposable)

        onChangeProductQuantitySubject.subscribeBy { pair ->
            viewModel.changeCart(pair)
        }.addTo(compositeDisposable)
    }

}