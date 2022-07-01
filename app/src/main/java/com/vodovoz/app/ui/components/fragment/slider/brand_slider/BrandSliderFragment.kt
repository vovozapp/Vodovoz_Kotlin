package com.vodovoz.app.ui.components.fragment.slider.brand_slider

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentSliderBrandBinding
import com.vodovoz.app.ui.components.adapter.BrandsSliderAdapter
import com.vodovoz.app.ui.components.base.BaseHiddenFragment
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.diffUtils.BrandSliderDiffUtilCallback
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject


class BrandSliderFragment : BaseHiddenFragment() {

    companion object {
        fun newInstance(
            onBrandClickSubject: PublishSubject<Long>,
            onShowAllBrandClickSubject: PublishSubject<Boolean>,
            viewStateSubject: PublishSubject<ViewState>? = null,
            onUpdateSubject: PublishSubject<Boolean>? = null
        ) = BrandSliderFragment().apply {
            this.viewStateSubject = viewStateSubject
            this.onUpdateSubject = onUpdateSubject
            this.onBrandClickSubject = onBrandClickSubject
            this.onShowAllBrandClickSubject = onShowAllBrandClickSubject
        }
    }

    private val compositeDisposable = CompositeDisposable()

    private var viewStateSubject: PublishSubject<ViewState>? = null
    private var onUpdateSubject: PublishSubject<Boolean>? = null
    private lateinit var onBrandClickSubject: PublishSubject<Long>
    private lateinit var onShowAllBrandClickSubject: PublishSubject<Boolean>

    private lateinit var binding: FragmentSliderBrandBinding
    private lateinit var viewModel: BrandSliderViewModel

    private lateinit var brandsSliderAdapter: BrandsSliderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[BrandSliderViewModel::class.java]
        viewModel.updateData()
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
        initShowAllButton()
    }

    private fun initBrandsRecyclerView() {
        binding.brandsRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val space = resources.getDimension(R.dimen.primary_space).toInt()
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

        binding.brandsRecycler.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (binding.brandsRecycler.width != 0) {
                        binding.brandsRecycler.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        brandsSliderAdapter = BrandsSliderAdapter(
                            onBrandClickSubject = onBrandClickSubject,
                            cardWidth = (binding.brandsRecycler.width - (space * 4))/3
                        )
                        binding.brandsRecycler.adapter = brandsSliderAdapter
                        observeViewModel()
                    }
                }
            }
        )
    }

    private fun initShowAllButton() {
        binding.showAll.setOnClickListener {
            onShowAllBrandClickSubject.onNext(true)
        }
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Hide -> {
                    viewStateSubject?.onNext(state)
                    hide()
                }
                else -> viewStateSubject?.onNext(state)
            }
        }

        viewModel.brandUIListLD.observe(viewLifecycleOwner) { brandUIDataList ->
            val diffUtil = BrandSliderDiffUtilCallback(
                oldList = brandsSliderAdapter.brandUIList,
                newList = brandUIDataList
            )

            DiffUtil.calculateDiff(diffUtil).let { diffResult ->
                brandsSliderAdapter.brandUIList = brandUIDataList
                diffResult.dispatchUpdatesTo(brandsSliderAdapter)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        onUpdateSubject?.subscribeBy {
            viewModel.updateData()
        }?.addTo(compositeDisposable)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

}