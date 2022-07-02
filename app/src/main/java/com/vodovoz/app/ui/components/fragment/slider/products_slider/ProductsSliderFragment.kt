package com.vodovoz.app.ui.components.fragment.slider.products_slider

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmadhamwi.tabsync.TabbedListMediator
import com.google.android.material.tabs.TabLayout
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentSliderProductBinding
import com.vodovoz.app.databinding.ViewCustomTabBinding
import com.vodovoz.app.ui.components.adapter.CategoriesAdapter
import com.vodovoz.app.ui.components.base.BaseHiddenFragment
import com.vodovoz.app.ui.components.interfaces.IOnChangeProductQuantity
import com.vodovoz.app.ui.components.interfaces.IOnProductClick
import com.vodovoz.app.ui.components.interfaces.IOnShowAllProductsClick
import com.vodovoz.app.ui.extensions.ViewExtensions.onRenderFinished
import com.vodovoz.app.ui.model.CategoryDetailUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class ProductsSliderFragment : BaseHiddenFragment() {

    companion object {
        private const val CONFIG = "CONFIG"
        fun newInstance(
            productsSliderConfig: ProductsSliderConfig
        ) = ProductsSliderFragment().apply {
            arguments = bundleOf(Pair(CONFIG, productsSliderConfig))
        }
    }

    private lateinit var categoryDetailUIList: List<CategoryDetailUI>
    private lateinit var config: ProductsSliderConfig
    private lateinit var iOnProductClick: IOnProductClick
    private lateinit var iOnChangeProductQuantity: IOnChangeProductQuantity
    private lateinit var iOnShowAllProductsClick: IOnShowAllProductsClick

    private lateinit var binding: FragmentSliderProductBinding
    private val space: Int by lazy { resources.getDimension(R.dimen.primary_space).toInt() }

    private val compositeDisposable = CompositeDisposable()
    private val onProductClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val onChangeProductQuantitySubject: PublishSubject<Pair<Long, Int>> = PublishSubject.create()

    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var tabbedListMediator: TabbedListMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    private fun getArgs() {
        config = requireArguments().getParcelable(CONFIG)!!
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
        initCategoryRecycler()
        initTabbedMediator()
        initTabLayout()
        subscribeSubjects()
    }

    private fun initCategoryRecycler() {
        binding.categoryRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.categoryRecycler.onRenderFinished { width, _ ->
            categoriesAdapter = CategoriesAdapter(
                onChangeProductQuantitySubject = onChangeProductQuantitySubject,
                onProductClickSubject = onProductClickSubject,
                cardWidth = (width - (space * 3))/2
            )
            binding.categoryRecycler.adapter = categoriesAdapter
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

    private fun subscribeSubjects() {
        onProductClickSubject.subscribeBy { productId ->
            iOnProductClick.onProductClick(productId)
        }.addTo(compositeDisposable)

        onChangeProductQuantitySubject.subscribeBy { pair ->
            iOnChangeProductQuantity.onChangeProductQuantity(pair.first, pair.second)
        }.addTo(compositeDisposable)
    }

    fun updateData(categoryDetailUIList: List<CategoryDetailUI>) {
        this.categoryDetailUIList = categoryDetailUIList
        updateCategoryTabs(categoryDetailUIList)
        updateCategoryRecycler(categoryDetailUIList)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateCategoryRecycler(categoryDetailUIList: List<CategoryDetailUI>) {
        categoriesAdapter.sliderCategoryUIList = categoryDetailUIList
        categoriesAdapter.notifyDataSetChanged()
    }

    private fun updateCategoryTabs(categoryDetailUIList: List<CategoryDetailUI>) {
        when(categoryDetailUIList.size) {
            1 -> {
                binding.categoryTabs.visibility = View.GONE
                binding.singleTitleContainer.visibility = View.VISIBLE
                binding.title.text = categoryDetailUIList.first().name
                when (config.containShowAllButton) {
                    true -> binding.showAll.visibility = View.VISIBLE
                    false -> binding.showAll.visibility = View.INVISIBLE
                }
            }
            else -> {
                binding.categoryTabs.visibility = View.VISIBLE
                binding.singleTitleContainer.visibility = View.GONE
                for (index in categoryDetailUIList.indices) {
                    binding.categoryTabs.newTab().apply {
                        val customTabBinding = ViewCustomTabBinding.inflate(
                            LayoutInflater.from(requireContext()), null, false
                        )

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

    fun initCallbacks(
        iOnProductClick: IOnProductClick,
        iOnChangeProductQuantity: IOnChangeProductQuantity,
        iOnShowAllProductsClick: IOnShowAllProductsClick
    ) {
        this.iOnProductClick = iOnProductClick
        this.iOnChangeProductQuantity = iOnChangeProductQuantity
        this.iOnShowAllProductsClick = iOnShowAllProductsClick
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}