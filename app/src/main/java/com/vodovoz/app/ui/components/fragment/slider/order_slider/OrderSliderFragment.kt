package com.vodovoz.app.ui.components.fragment.slider.order_slider

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentSliderOrderBinding
import com.vodovoz.app.ui.components.adapter.OrderSliderAdapter
import com.vodovoz.app.ui.components.base.BaseHiddenFragment
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.diffUtils.OrderDiffUtilCallback
import com.vodovoz.app.ui.model.OrderUI
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class OrderSliderFragment : BaseHiddenFragment() {

    companion object {
        fun newInstance(
            onOrderClickSubject: PublishSubject<Long>,
            onShowAllOrdersClickSubject: PublishSubject<Boolean>,
            viewStateSubject: PublishSubject<ViewState>? = null,
            onUpdateSubject: PublishSubject<Boolean>? = null
        ) = OrderSliderFragment().apply {
            this.viewStateSubject = viewStateSubject
            this.onUpdateSubject = onUpdateSubject
            this.onOrderClickSubject = onOrderClickSubject
            this.onShowAllOrdersClickSubject = onShowAllOrdersClickSubject
        }
    }

    private val compositeDisposable = CompositeDisposable()

    private lateinit var onShowAllOrdersClickSubject: PublishSubject<Boolean>
    private lateinit var onOrderClickSubject: PublishSubject<Long>
    private var viewStateSubject: PublishSubject<ViewState>? = null
    private var onUpdateSubject: PublishSubject<Boolean>? = null

    private lateinit var binding: FragmentSliderOrderBinding
    private lateinit var viewModel: OrderSliderViewModel

    private val orderSliderAdapter: OrderSliderAdapter by lazy { OrderSliderAdapter(onOrderClickSubject) }

    private var isInitRecyclerSize = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[OrderSliderViewModel::class.java]
        viewModel.updateData()
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentSliderOrderBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initOrderPager()
        observeViewModel()
    }

    private fun initOrderPager() {
        val space = resources.getDimension(R.dimen.primary_space).toInt()
        binding.orderPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.orderPager.adapter = orderSliderAdapter
        binding.orderPager.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    with(outRect) {
                        left = space
                        top = space / 2
                        right = space
                        bottom = space / 2
                    }
                }
            }
        )
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            Log.i(LogSettings.ID_LOG, "${state::class.simpleName}" )
            when(state) {
                is ViewState.Success -> if (isInitRecyclerSize) viewStateSubject?.onNext(state)
                is ViewState.Hide -> {
                    viewStateSubject?.onNext(state)
                    hide()
                }
                else -> viewStateSubject?.onNext(state)
            }
        }

        viewModel.orderUIListLD.observe(viewLifecycleOwner) { orderUIList ->
            fillOrderPager(orderUIList)
        }
    }

    private fun fillOrderPager(orderUIList: List<OrderUI>) {
        val diffUtil = OrderDiffUtilCallback(
            oldList = orderSliderAdapter.orderUIList,
            newList = orderUIList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            orderSliderAdapter.orderUIList = orderUIList
            diffResult.dispatchUpdatesTo(orderSliderAdapter)
        }

        binding.orderPager.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.orderPager.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    isInitRecyclerSize = true
                    viewStateSubject?.onNext(ViewState.Success())
                }
            }
        )
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