package com.vodovoz.app.ui.fragment.slider.brands_slider

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentSliderBrandBinding
import com.vodovoz.app.ui.adapter.BrandsSliderAdapter
import com.vodovoz.app.ui.base.BaseHiddenFragment
import com.vodovoz.app.ui.diffUtils.BrandDiffUtilCallback
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.extensions.ViewExtensions.onRenderFinished
import com.vodovoz.app.ui.interfaces.IOnBrandClick
import com.vodovoz.app.ui.interfaces.IOnShowAllBrandsClick
import com.vodovoz.app.ui.model.BrandUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject


class BrandsSliderFragment : BaseHiddenFragment() {

    private lateinit var brandUIList: List<BrandUI>
    private lateinit var iOnBrandClick: IOnBrandClick
    private lateinit var iOnShowAllBrandsClick: IOnShowAllBrandsClick

    private lateinit var binding: FragmentSliderBrandBinding

    private val compositeDisposable = CompositeDisposable()
    private val onAdapterReadySubject: BehaviorSubject<List<BrandUI>> = BehaviorSubject.create()
    private val onBrandClickSubject: PublishSubject<Long> = PublishSubject.create()
    private lateinit var brandsSliderAdapter: BrandsSliderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeSubjects()
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentSliderBrandBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initBrandsRecyclerView()
    }

    private fun initBrandsRecyclerView() {
        binding.rvBrands.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val space = resources.getDimension(R.dimen.space_16).toInt()
        brandsSliderAdapter = BrandsSliderAdapter(
            onBrandClickSubject = onBrandClickSubject,
            cardWidth = 0
        )
        binding.rvBrands.adapter = brandsSliderAdapter
        onAdapterReadySubject.subscribeBy { brandUIList ->
            this.brandUIList = brandUIList
            updateView(brandUIList)
        }.addTo(compositeDisposable)

        binding.rvBrands.addMarginDecoration { rect, view, parent, state ->
            if (parent.getChildAdapterPosition(view) == 0) rect.left = space
            if (parent.getChildAdapterPosition(view) == state.itemCount - 1) rect.right = space
            else rect.right = space/2
            rect.top = space / 2
            rect.bottom = space / 2
        }
        

    }

    private fun subscribeSubjects() {
        onBrandClickSubject.subscribeBy { brandId ->
            iOnBrandClick.onBrandClick(brandId)
        }.addTo(compositeDisposable)
    }

    fun initCallbacks(
        iOnBrandClick: IOnBrandClick,
        iOnShowAllBrandsClick: IOnShowAllBrandsClick
    ) {
        this.iOnBrandClick = iOnBrandClick
        this.iOnShowAllBrandsClick = iOnShowAllBrandsClick
    }

    fun updateData(brandUIList: List<BrandUI>) {
        onAdapterReadySubject.onNext(brandUIList)
    }

    private fun updateView(brandUIList: List<BrandUI>) {
        val diffUtil = BrandDiffUtilCallback(
            oldList = brandsSliderAdapter.brandUIList,
            newList = brandUIList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            brandsSliderAdapter.brandUIList = brandUIList
            diffResult.dispatchUpdatesTo(brandsSliderAdapter)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

}