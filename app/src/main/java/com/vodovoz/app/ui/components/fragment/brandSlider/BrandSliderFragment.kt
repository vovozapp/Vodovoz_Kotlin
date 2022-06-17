package com.vodovoz.app.ui.components.fragment.brandSlider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentSliderBrandBinding
import com.vodovoz.app.ui.components.base.HorizontalMarginItemDecoration
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.adapter.brandSliderAdapter.BrandSliderAdapter
import com.vodovoz.app.ui.components.adapter.brandSliderAdapter.BrandSliderDiffUtilCallback


class BrandSliderFragment : Fragment() {

    private lateinit var binding: FragmentSliderBrandBinding
    private lateinit var viewModel: BrandSliderViewModel

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
                        brandSliderAdapter = BrandSliderAdapter((binding.brandsRecycler.width - (space * 4))/3)
                        binding.brandsRecycler.adapter = brandSliderAdapter
                        observeViewModel()
                    }
                }
            }
        )
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

}