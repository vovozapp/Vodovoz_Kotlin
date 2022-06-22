package com.vodovoz.app.ui.components.fragment.brandSlider

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentSliderBrandBinding
import com.vodovoz.app.ui.components.base.HorizontalMarginItemDecoration
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.adapter.brandSliderAdapter.BrandSliderAdapter
import com.vodovoz.app.ui.components.adapter.brandSliderAdapter.BrandSliderDiffUtilCallback
import com.vodovoz.app.ui.components.fragment.home.HomeFragmentDirections
import com.vodovoz.app.ui.components.fragment.productsWithoutFilter.ProductsWithoutFiltersFragment
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject


class BrandSliderFragment : Fragment() {

    private lateinit var binding: FragmentSliderBrandBinding
    private lateinit var viewModel: BrandSliderViewModel

    private val compositeDisposable = CompositeDisposable()
    private val onBrandClickSubject: PublishSubject<Long> = PublishSubject.create()
    private lateinit var brandSliderAdapter: BrandSliderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentSliderBrandBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initBrandsRecyclerView()
        initShowAllButton()
        initViewModel()
    }.root

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[BrandSliderViewModel::class.java]
    }

    private fun initBrandsRecyclerView() {
        binding.brandsRecycler.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )

        val space = resources.getDimension(R.dimen.primary_space).toInt()
        binding.brandsRecycler.addItemDecoration(HorizontalMarginItemDecoration(space))

        binding.brandsRecycler.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (binding.brandsRecycler.width != 0) {
                        binding.brandsRecycler.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        brandSliderAdapter = BrandSliderAdapter(
                            onBrandClickSubject = onBrandClickSubject,
                            cardWidth = (binding.brandsRecycler.width - (space * 4))/3
                        )
                        binding.brandsRecycler.adapter = brandSliderAdapter
                        observeViewModel()
                    }
                }
            }
        )
    }

    private fun initShowAllButton() {
        binding.showAll.setOnClickListener {
            parentFragment?.findNavController()?.navigate(
                HomeFragmentDirections.actionHomeFragmentToAllBrandsFragment(listOf<Long>().toLongArray())
            )
        }
    }

    private fun observeViewModel() {
        viewModel.brandUIListLD.observe(viewLifecycleOwner) { brandUIDataList ->
            val diffUtil = BrandSliderDiffUtilCallback(
                oldList = brandSliderAdapter.brandUIList,
                newList = brandUIDataList
            )

            DiffUtil.calculateDiff(diffUtil).let { diffResult ->
                brandSliderAdapter.brandUIList = brandUIDataList
                diffResult.dispatchUpdatesTo(brandSliderAdapter)
            }
        }

        viewModel.sateHideLD.observe(viewLifecycleOwner) { stateHide ->
            when(stateHide) {
                true -> binding.root.visibility = View.VISIBLE
                false -> binding.root.visibility = View.GONE
            }
        }
    }

    override fun onStart() {
        super.onStart()
        onBrandClickSubject.subscribeBy { brandId ->
            Log.i(LogSettings.ID_LOG, "BRAND ID: $brandId")
            parentFragment?.findNavController()?.navigate(
                HomeFragmentDirections.actionHomeFragmentToProductsWithoutFiltersFragment(
                    ProductsWithoutFiltersFragment.DataSource.Brand(brandId)
                )
            )
        }.addTo(compositeDisposable)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

}