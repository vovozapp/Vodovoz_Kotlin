package com.vodovoz.app.ui.components.fragment.slider.popular_slider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentSliderPopularBinding
import com.vodovoz.app.ui.components.adapter.PopularCategoriesSliderAdapter
import com.vodovoz.app.ui.components.base.*
import com.vodovoz.app.ui.components.diffUtils.PopularDiffUtilCallback
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class PopularSliderFragment : BaseHiddenFragment() {

    companion object {
        fun newInstance(
            onPopularCategoryClickSubject: PublishSubject<Long>,
            viewStateSubject: PublishSubject<ViewState>? = null,
            onUpdateSubject: PublishSubject<Boolean>? = null
        ) = PopularSliderFragment().apply {
            this.viewStateSubject = viewStateSubject
            this.onUpdateSubject = onUpdateSubject
            this.onPopularCategoryClickSubject = onPopularCategoryClickSubject
        }
    }

    private val compositeDisposable = CompositeDisposable()

    private lateinit var onPopularCategoryClickSubject: PublishSubject<Long>
    private var viewStateSubject: PublishSubject<ViewState>? = null
    private var onUpdateSubject: PublishSubject<Boolean>? = null

    private lateinit var binding: FragmentSliderPopularBinding
    private lateinit var viewModel: PopularSliderViewModel

    private val popularCategoriesSliderAdapter: PopularCategoriesSliderAdapter by lazy {
        PopularCategoriesSliderAdapter(onPopularCategoryClickSubject)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[PopularSliderViewModel::class.java]
        viewModel.updateData()
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentSliderPopularBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initViewModel()
        initPopularRecyclerView()
        observeViewModel()
    }

    private fun initPopularRecyclerView() {
        binding.popularRecycler.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )

        val space = resources.getDimension(R.dimen.primary_space).toInt()
        binding.popularRecycler.addItemDecoration(HorizontalMarginItemDecoration(space))
        binding.popularRecycler.adapter = popularCategoriesSliderAdapter
    }

    private fun observeViewModel() {
        viewModel.categoryUIListLD.observe(viewLifecycleOwner) { categoryUIList ->
            val diffUtil = PopularDiffUtilCallback(
                oldList = popularCategoriesSliderAdapter.categoryPopularUIList,
                newList = categoryUIList
            )

            DiffUtil.calculateDiff(diffUtil).let { diffResult ->
                popularCategoriesSliderAdapter.categoryPopularUIList = categoryUIList
                diffResult.dispatchUpdatesTo(popularCategoriesSliderAdapter)
            }
        }

        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Hide -> {
                    viewStateSubject?.onNext(state)
                    hide()
                }
                else -> viewStateSubject?.onNext(state)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        onUpdateSubject?.subscribeBy { categoryId ->
            viewModel.updateData()
        }?.addTo(compositeDisposable)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

}