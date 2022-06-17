package com.vodovoz.app.ui.components.fragment.promotionDetail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentPromotionDetailBinding
import com.vodovoz.app.ui.components.adapter.linearProductAdapter.LinearProductAdapter
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.components.base.FetchStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.adapter.pagingProductsAdapter.list.ListMarginDecoration
import com.vodovoz.app.ui.components.diffUtils.ProductDiffUtilCallback
import com.vodovoz.app.ui.components.fragment.productSlider.ProductSliderFragment
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.setScrollElevation
import com.vodovoz.app.ui.extensions.ScrollViewExtensions.setScrollElevation
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.PromotionDetailUI
import io.reactivex.rxjava3.subjects.PublishSubject

class PromotionDetailFragment : FetchStateBaseFragment() {

    private lateinit var binding: FragmentPromotionDetailBinding
    private lateinit var viewModel: PromotionDetailViewModel

    private val onProductClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val linearProductAdapter = LinearProductAdapter(onProductClickSubject)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        getArgs()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[PromotionDetailViewModel::class.java]
    }

    private fun getArgs() {
        viewModel.setArgs(PromotionDetailFragmentArgs.fromBundle(requireArguments()).promotionId)
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentPromotionDetailBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
    }.root

    override fun initView() {
        initActionBar()
        initPromotionProductRecycler()
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

    override fun update() {
        viewModel.updateData()
    }

    private fun observeViewModel() {
        viewModel.fetchStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is FetchState.Loading -> onStateLoading()
                is FetchState.Hide -> onStateHide()
                is FetchState.Error -> onStateError(state.errorMessage)
                is FetchState.Success -> {
                    onStateSuccess()
                    state.data?.let { fillView(it) }
                }
            }
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
        childFragmentManager.beginTransaction()
            .replace(R.id.forYouProductSliderFragment, ProductSliderFragment.newInstance(
                dataSource = ProductSliderFragment.DataSource.Args(listOf(categoryDetailUI)),
                config = ProductSliderFragment.Config(true)
            )).commit()
    }

    private fun fillPromotionProductRecycler(categoryDetailUI: CategoryDetailUI) {
        val diffUtil = ProductDiffUtilCallback(
            oldList = linearProductAdapter.productUiList,
            newList = categoryDetailUI.productUIList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            linearProductAdapter.productUiList = categoryDetailUI.productUIList
            diffResult.dispatchUpdatesTo(linearProductAdapter)
        }
    }

}