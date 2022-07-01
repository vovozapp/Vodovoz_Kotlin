package com.vodovoz.app.ui.components.fragment.slider.product_slider

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmadhamwi.tabsync.TabbedListMediator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentSliderProductBinding
import com.vodovoz.app.databinding.ViewCustomTabBinding
import com.vodovoz.app.ui.components.adapter.CategoriesAdapter
import com.vodovoz.app.ui.components.base.BaseHiddenFragment
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.fragment.home.HomeFragmentDirections
import com.vodovoz.app.ui.components.fragment.paginated_products_catalog_without_filters.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.ui.components.fragment.paginated_products_catalog_without_filters.PaginatedProductsCatalogWithoutFiltersViewModel
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class ProductSliderFragment : BaseHiddenFragment() {

    companion object {
        fun newInstance(
            dataSource: DataSource,
            config: Config,
            onProductClickSubject: PublishSubject<Long>,
            viewStateSubject: PublishSubject<ViewState>? = null,
            onUpdateSubject: PublishSubject<Boolean>? = null,
            onChangeCartSubject: PublishSubject<Boolean>? = null,
            onShowAllClickSubject: PublishSubject<PaginatedProductsCatalogWithoutFiltersFragment.DataSource>? = null
        ) = ProductSliderFragment().apply {
            this.dataSource = dataSource
            this.config = config
            this.onShowAllClickSubject = onShowAllClickSubject
            this.viewStateSubject = viewStateSubject
            this.onUpdateSubject = onUpdateSubject
            this.onChangeCartSubject = onChangeCartSubject
            this.onProductClickSubject = onProductClickSubject
        }
    }

    private val compositeDisposable = CompositeDisposable()

    private lateinit var dataSource: DataSource
    private lateinit var config: Config
    private lateinit var onProductClickSubject: PublishSubject<Long>
    private var onShowAllClickSubject: PublishSubject<PaginatedProductsCatalogWithoutFiltersFragment.DataSource>? = null
    private var viewStateSubject: PublishSubject<ViewState>? = null
    private var onUpdateSubject: PublishSubject<Boolean>? = null
    private var onChangeCartSubject: PublishSubject<Boolean>? = null

    private lateinit var binding: FragmentSliderProductBinding
    private lateinit var viewModel: ProductSliderViewModel

    private val onChangeProductQuantitySubject: PublishSubject<ProductUI> = PublishSubject.create()
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var tabbedListMediator: TabbedListMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[ProductSliderViewModel::class.java]
        viewModel.updateArgs(dataSource)
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentSliderProductBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initBackground()
        subscribeUpdateSubject()
        initTabbedMediator()
        initCategoryRecycler()
        initTabLayout()
        initShowAllButton()
    }

    private fun subscribeUpdateSubject() {
        onUpdateSubject?.subscribeBy {
            viewModel.updateData()
        }?.addTo(compositeDisposable)
    }

    private fun initBackground() {
        if (!config.showBackground) {
            binding.topMargin.visibility = View.GONE
            binding.bottomMargin.visibility = View.GONE
        } else {
            binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.root.translationZ = resources.getDimension(R.dimen.root_elevation)
        }
    }

    private fun initCategoryRecycler() {
        binding.categoryRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val cardSpace = resources.getDimension(R.dimen.primary_space).toInt()
        binding.categoryRecycler.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (binding.categoryRecycler.width != 0) {
                        binding.categoryRecycler.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        categoriesAdapter = CategoriesAdapter(
                            onChangeProductQuantitySubject = onChangeProductQuantitySubject,
                            onProductClickSubject = onProductClickSubject,
                            cardWidth = (binding.categoryRecycler.width - (cardSpace * 3))/2
                        )
                        binding.categoryRecycler.adapter = categoriesAdapter
                        observeViewModel()
                    }
                }
            }
        )
    }

    private fun initShowAllButton() {
        binding.showAll.setOnClickListener {
            val dataSource = if (dataSource is DataSource.Request) {
                when((dataSource as DataSource.Request).sliderType) {
                    ProductSliderViewModel.TOP_PRODUCTS_SLIDER,
                    ProductSliderViewModel.BOTTOM_PRODUCTS_SLIDER ->
                        PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Slider(
                            categoryId = viewModel.categoryDetailUIList.first().id!!
                        )
                    ProductSliderViewModel.DISCOUNT_PRODUCTS_SLIDER ->
                        PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Discount()
                    ProductSliderViewModel.NOVELTIES_PRODUCTS_SLIDER ->
                        PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Novelties()
                    else -> null
                }
            } else null

            dataSource?.let {
                onShowAllClickSubject?.onNext(dataSource)
            }
        }
    }

    private fun initTabbedMediator() {
        tabbedListMediator = TabbedListMediator(
            binding.categoryRecycler,
            binding.categoryTabs,
            listOf()
        )
    }

    private fun initTabLayout() {
        binding.categoryTabs.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.view?.findViewById<TextView>(R.id.name)?.setTextColor(
                        ContextCompat.getColor(requireContext(),
                            R.color.bluePrimary)
                    )
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    tab?.view?.findViewById<TextView>(R.id.name)?.setTextColor(
                        ContextCompat.getColor(requireContext(),
                            R.color.blackTextLight)
                    )
                }
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            }
        )
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Hide -> {
                    viewStateSubject?.onNext(state)
                    hide()
                }
                else -> viewStateSubject?.onNext(state)
            }
        }

        viewModel.categoryDetailUIListLD.observe(viewLifecycleOwner) { categoryDetailUIList ->
            updateCategoryRecycler(categoryDetailUIList)
            updateCategoryTabs(categoryDetailUIList)
        }

        viewModel.errorMessageLD.observe(viewLifecycleOwner) { errorMessage ->
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateCategoryRecycler(sliderCategoryUIList: List<CategoryDetailUI>) {
        categoriesAdapter.sliderCategoryUIList = sliderCategoryUIList
        categoriesAdapter.notifyDataSetChanged()
    }

    private fun updateCategoryTabs(categoryDetailUIList: List<CategoryDetailUI>) {
        when(categoryDetailUIList.size) {
            1 -> {
                binding.categoryTabs.visibility = View.GONE
                binding.singleTitleContainer.visibility = View.VISIBLE
                binding.title.text = categoryDetailUIList.first().name
                when(dataSource) {
                    is DataSource.Args -> binding.showAll.visibility = View.GONE
                    is DataSource.Request -> binding.showAll.visibility = View.VISIBLE
                }
                if ((dataSource is DataSource.Request)) {
                    if ((dataSource as DataSource.Request).sliderType == ProductSliderViewModel.VIEWED_PRODUCTS_SLIDER) {
                        binding.showAll.visibility = View.GONE
                    }
                }
            }
            else -> {
                binding.categoryTabs.visibility = View.VISIBLE
                binding.singleTitleContainer.visibility = View.GONE
                for (index in categoryDetailUIList.indices) {
                    binding.categoryTabs.newTab().apply {
                        val customTabBinding = ViewCustomTabBinding.inflate(
                            LayoutInflater.from(requireContext()),
                            null,
                            false
                        )

                        val space = resources.getDimension(R.dimen.primary_space).toInt()
                        val marginEnd: Int
                        val marginStart: Int
                        val marginTop = space/4
                        val marginBottom = space/4

                        when (index) {
                            0 -> {
                                marginStart = space
                                marginEnd = space/2
                            }
                            categoryDetailUIList.indices.last -> {
                                marginEnd = space
                                marginStart = space/2
                            }
                            else -> {
                                marginEnd = space/2
                                marginStart = space/2
                            }
                        }

                        customTabBinding.card.layoutParams = RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            topMargin = marginTop
                            bottomMargin = marginBottom
                            leftMargin = marginStart
                            rightMargin = marginEnd
                        }

                        customTabBinding.name.text = categoryDetailUIList[index].name
                        customView = customTabBinding.root
                        binding.categoryTabs.addTab(this)
                    }
                }
                tabbedListMediator.updateMediatorWithNewIndices(categoryDetailUIList.indices.toList())
                if (!tabbedListMediator.isAttached()) tabbedListMediator.attach()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        onChangeProductQuantitySubject.subscribeBy { product ->
            viewModel.changeCart(product)
        }.addTo(compositeDisposable)

        onUpdateSubject?.subscribeBy {
            viewModel.updateData()
        }?.addTo(compositeDisposable)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

    sealed class DataSource {
        class Request(val sliderType: String) : DataSource()
        class Args(val categoryDetailUIList: List<CategoryDetailUI>) : DataSource()
    }

    class Config(val showBackground: Boolean = false)

}