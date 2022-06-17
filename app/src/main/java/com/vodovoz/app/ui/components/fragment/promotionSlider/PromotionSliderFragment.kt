package com.vodovoz.app.ui.components.fragment.promotionSlider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.databinding.FragmentSliderPromotionBinding
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.adapter.promotionSliderAdapter.PromotionSliderAdapter
import com.vodovoz.app.ui.components.diffUtils.PromotionDiffUtilCallback
import com.vodovoz.app.ui.components.fragment.home.HomeFragment
import com.vodovoz.app.ui.components.fragment.home.HomeFragmentDirections
import com.vodovoz.app.ui.model.PromotionUI
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class PromotionSliderFragment : Fragment() {

    companion object {
        fun newInstance(dataSource: DataSource) = PromotionSliderFragment().apply {
            this.dataSource = dataSource
        }
    }

    private lateinit var dataSource: DataSource

    private lateinit var binding: FragmentSliderPromotionBinding
    private lateinit var viewModel: PromotionSliderViewModel

    private val onPromotionClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val promotionSliderAdapter = PromotionSliderAdapter(onPromotionClickSubject)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSliderPromotionBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initViewModel()
        initShowAllPromotionsButton()
        initPromotionPager()
        initTitle()
        observeViewModel()
    }.root

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[PromotionSliderViewModel::class.java]

        viewModel.getDataByDataSource(dataSource)
    }

    private fun initShowAllPromotionsButton() {
        binding.showAll.setOnClickListener {
            parentFragment?.findNavController()?.navigate(HomeFragmentDirections.actionHomeFragmentToAllPromotionsFragment())
        }
    }

    private fun initPromotionPager() {
        binding.promotionRager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.promotionRager.adapter = promotionSliderAdapter
        onPromotionClickSubject.subscribeBy { promotionId ->
            parentFragment?.findNavController()?.navigate(HomeFragmentDirections.actionHomeFragmentToPromotionDetailFragment(promotionId))
        }
    }

    private fun initTitle() {
        binding.title.text = dataSource.title
        when(dataSource) {
            is DataSource.Request -> {
                binding.showAll.visibility = View.VISIBLE
            }
            is DataSource.Args -> binding.showAll.visibility = View.GONE
        }
    }

    private fun observeViewModel() {
        viewModel.promotionListLD.observe(viewLifecycleOwner) { promotionList ->
            val diffUtil = PromotionDiffUtilCallback(
                oldList = promotionSliderAdapter.promotionUIList,
                newList = promotionList
            )

            DiffUtil.calculateDiff(diffUtil).let { diffResult ->
                promotionSliderAdapter.promotionUIList = promotionList
                diffResult.dispatchUpdatesTo(promotionSliderAdapter)
            }
        }
    }

    sealed class DataSource(val title: String) {
        class Request(title: String) : DataSource(title)
        class Args(
            title: String,
            val promotionUIList: List<PromotionUI>
        ) : DataSource(title)
    }

}