package com.vodovoz.app.ui.components.fragment.productSlider

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmadhamwi.tabsync.TabbedListMediator
import com.google.android.material.tabs.TabLayout
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentSliderProductBinding
import com.vodovoz.app.databinding.ViewCustomTabBinding
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.fragment.productSlider.categoryDetailAdapter.ProductCategorySliderAdapter
import com.vodovoz.app.ui.model.CategoryDetailUI

class ProductSliderFragment : Fragment() {

    companion object {
        fun newInstance(
            dataSource: DataSource,
            config: Config
        ) = ProductSliderFragment().apply {
            this.dataSource = dataSource
            this.config = config
        }
    }

    private lateinit var dataSource: DataSource
    private lateinit var config: Config

    private lateinit var binding: FragmentSliderProductBinding
    private lateinit var viewModel: ProductSliderViewModel

    private lateinit var productCategorySliderAdapter: ProductCategorySliderAdapter
    private lateinit var tabbedListMediator: TabbedListMediator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSliderProductBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initViewModel()
        initBackground()
        initTabbedMediator()
        initCategoryRecycler()
        initTabLayout()
    }.root

    private fun initBackground() {
        if (!config.showBackground) {
            binding.topMargin.visibility = View.GONE
            binding.bottomMargin.visibility = View.GONE
        } else {
            binding.root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.root.translationZ = resources.getDimension(R.dimen.root_elveation)
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[ProductSliderViewModel::class.java]
        viewModel.getDataByDataSource(dataSource)
    }

    private fun initCategoryRecycler() {
        binding.categoryRecycler.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )

        val cardSpace = resources.getDimension(R.dimen.primary_space).toInt()
        binding.categoryRecycler.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (binding.categoryRecycler.width != 0) {
                        binding.categoryRecycler.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        productCategorySliderAdapter = ProductCategorySliderAdapter((binding.categoryRecycler.width - (cardSpace * 3))/2)
                        binding.categoryRecycler.adapter = productCategorySliderAdapter
                        observeViewModel()
                    }
                }
            }
        )
    }

    private fun initTabbedMediator() {
        tabbedListMediator = TabbedListMediator(
            binding.categoryRecycler,
            binding.categoryTabs,
            listOf()
        )
    }

    private fun observeViewModel() {
        viewModel.sliderCategoryUIListLD.observe(viewLifecycleOwner) { categoryUIDataList ->
            updateCategoryRecycler(categoryUIDataList)
            updateCategoryTabs(categoryUIDataList)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateCategoryRecycler(sliderCategoryUIList: List<CategoryDetailUI>) {
        productCategorySliderAdapter.sliderCategoryUIList = sliderCategoryUIList
        productCategorySliderAdapter.notifyDataSetChanged()
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
                        when (index) {
                            0 -> {
                                customTabBinding.card.layoutParams = RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                ).apply {
                                    val space = resources.getDimension(R.dimen.primary_space).toInt()
                                    marginStart = space
                                    marginEnd = space/2
                                    topMargin = space/4
                                    bottomMargin = space/4
                                }
                            }
                            categoryDetailUIList.size - 1 -> {
                                customTabBinding.card.layoutParams = RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                ).apply {
                                    val space = resources.getDimension(R.dimen.primary_space).toInt()
                                    marginEnd = space
                                    marginStart = space/2
                                    topMargin = space/4
                                    bottomMargin = space/4
                                }
                            }
                            else -> {
                                customTabBinding.card.layoutParams = RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                ).apply {
                                    val space = resources.getDimension(R.dimen.primary_space).toInt()
                                    marginEnd = space/2
                                    marginStart = space/2
                                    topMargin = space/4
                                    bottomMargin = space/4
                                }
                            }
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

    sealed class DataSource {
        class Request(val sliderType: String) : DataSource()
        class Args(val categoryDetailUIList: List<CategoryDetailUI>) : DataSource()
    }

    class Config(
        val showBackground: Boolean = false
    )

}