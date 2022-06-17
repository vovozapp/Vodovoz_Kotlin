package com.vodovoz.app.ui.components.fragment.popularSlider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentSliderPopularBinding
import com.vodovoz.app.ui.components.base.HorizontalMarginItemDecoration
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.adapter.popularSliderAdapter.PopularSliderAdapter
import com.vodovoz.app.ui.components.diffUtils.PopularDiffUtilCallback
import com.vodovoz.app.ui.components.fragment.home.HomeFragmentDirections
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class PopularSliderFragment : Fragment() {

    private lateinit var binding: FragmentSliderPopularBinding
    private lateinit var viewModel: PopularSliderViewModel

    private val compositeDisposable = CompositeDisposable()

    private val onPopularClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val popularSliderAdapter = PopularSliderAdapter(onPopularClickSubject)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSliderPopularBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initViewModel()
        initPopularRecyclerView()
        observeViewModel()
    }.root

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[PopularSliderViewModel::class.java]
    }

    private fun initPopularRecyclerView() {
        binding.popularRecycler.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )

        val space = resources.getDimension(R.dimen.primary_space).toInt()
        binding.popularRecycler.addItemDecoration(HorizontalMarginItemDecoration(space))
        binding.popularRecycler.adapter = popularSliderAdapter
    }

    private fun observeViewModel() {
        viewModel.popularSectionIUDataListLD.observe(viewLifecycleOwner) { popularSectionUIDataList ->
            val diffUtil = PopularDiffUtilCallback(
                oldList = popularSliderAdapter.categoryPopularUIList,
                newList = popularSectionUIDataList
            )

            DiffUtil.calculateDiff(diffUtil).let { diffResult ->
                popularSliderAdapter.categoryPopularUIList = popularSectionUIDataList
                diffResult.dispatchUpdatesTo(popularSliderAdapter)
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
        onPopularClickSubject.subscribeBy { categoryId ->
            parentFragment?.findNavController()?.navigate(
                HomeFragmentDirections.actionHomeFragmentToProductsFragment(categoryId)
            )
        }.addTo(compositeDisposable)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

}