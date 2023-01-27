package com.vodovoz.app.ui.fragment.slider.popular_slider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentSliderPopularBinding
import com.vodovoz.app.ui.adapter.PopularCategoriesSliderAdapter
import com.vodovoz.app.ui.base.BaseHiddenFragment
import com.vodovoz.app.ui.base.HorizontalMarginItemDecoration
import com.vodovoz.app.ui.diffUtils.PopularDiffUtilCallback
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.interfaces.IOnPopularCategoryClick
import com.vodovoz.app.ui.model.CategoryUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class PopularCategoriesSliderFragment : BaseHiddenFragment() {

    private lateinit var categoryUIList: List<CategoryUI>
    private lateinit var iOnPopularCategoryClick: IOnPopularCategoryClick

    private lateinit var binding: FragmentSliderPopularBinding

    private val compositeDisposable = CompositeDisposable()
    private val onPopularCategoryClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val popularCategoriesSliderAdapter = PopularCategoriesSliderAdapter(onPopularCategoryClickSubject)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeSubjects()
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
        initPopularRecyclerView()
    }

    private fun initPopularRecyclerView() {
        binding.rvPopularCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val space = resources.getDimension(R.dimen.space_16).toInt()
        binding.rvPopularCategories.addMarginDecoration { rect, view, parent, state ->
            if (parent.getChildAdapterPosition(view) == 0) rect.left = space
            if (parent.getChildAdapterPosition(view) == state.itemCount - 1) rect.right = space
            else rect.right = space/2
            rect.top = space / 2
            rect.bottom = space / 2
        }
        binding.rvPopularCategories.adapter = popularCategoriesSliderAdapter
    }

    private fun subscribeSubjects() {
        onPopularCategoryClickSubject.subscribeBy { categoryId ->
            iOnPopularCategoryClick.onPopularCategoryClick(categoryId)
        }.addTo(compositeDisposable)
    }

    fun initCallbacks(iOnPopularCategoryClick: IOnPopularCategoryClick) {
        this.iOnPopularCategoryClick = iOnPopularCategoryClick
    }

    fun updateData(categoryUIList: List<CategoryUI>) {
        this.categoryUIList = categoryUIList

        val diffUtil = PopularDiffUtilCallback(
            oldList = popularCategoriesSliderAdapter.categoryPopularUIList,
            newList = categoryUIList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            popularCategoriesSliderAdapter.categoryPopularUIList = categoryUIList
            diffResult.dispatchUpdatesTo(popularCategoriesSliderAdapter)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

}