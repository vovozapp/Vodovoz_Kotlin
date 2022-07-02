package com.vodovoz.app.ui.components.fragment.slider.brands_slider

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
import com.vodovoz.app.ui.components.adapter.BrandsSliderAdapter
import com.vodovoz.app.ui.components.base.BaseHiddenFragment
import com.vodovoz.app.ui.components.diffUtils.BrandSliderDiffUtilCallback
import com.vodovoz.app.ui.components.interfaces.IOnBrandClick
import com.vodovoz.app.ui.components.interfaces.IOnShowAllBrandsClick
import com.vodovoz.app.ui.extensions.ViewExtensions.onRenderFinished
import com.vodovoz.app.ui.model.BrandUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject


class BrandsSliderFragment : BaseHiddenFragment() {

    private lateinit var brandUIList: List<BrandUI>
    private lateinit var iOnBrandClick: IOnBrandClick
    private lateinit var iOnShowAllBrandsClick: IOnShowAllBrandsClick

    private lateinit var binding: FragmentSliderBrandBinding

    private val compositeDisposable = CompositeDisposable()
    private val onBrandClickSubject: PublishSubject<Long> = PublishSubject.create()
    private lateinit var brandsSliderAdapter: BrandsSliderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        subscribeSubjects()
    }

    private fun initBrandsRecyclerView() {
        binding.brandsRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val space = resources.getDimension(R.dimen.primary_space).toInt()
        binding.brandsRecycler.onRenderFinished { width, _ ->
            brandsSliderAdapter = BrandsSliderAdapter(
                onBrandClickSubject = onBrandClickSubject,
                cardWidth = (width - (space * 4))/3
            )
            binding.brandsRecycler.adapter = brandsSliderAdapter
        }

        binding.brandsRecycler.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    with(outRect) {
                        if (parent.getChildAdapterPosition(view) == 0) left = space
                        top = space / 2
                        right = space
                        bottom = space / 2
                    }
                }
            }
        )

        binding.showAll.setOnClickListener {
            iOnShowAllBrandsClick.onShowAllBrandsClick()
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
        this.brandUIList = brandUIList

        val diffUtil = BrandSliderDiffUtilCallback(
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
        compositeDisposable.clear()
    }

}