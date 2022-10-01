package com.vodovoz.app.ui.fragment.slider.order_slider

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentSliderOrderBinding
import com.vodovoz.app.ui.adapter.OrderSliderAdapter
import com.vodovoz.app.ui.base.BaseHiddenFragment
import com.vodovoz.app.ui.diffUtils.OrderDiffUtilCallback
import com.vodovoz.app.ui.interfaces.IOnOrderClick
import com.vodovoz.app.ui.interfaces.IOnShowAllOrdersClick
import com.vodovoz.app.ui.model.OrderUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class OrdersSliderFragment : BaseHiddenFragment() {

    companion object {
        private const val CONFIG = "CONFIG"
        fun newInstance(
            ordersSliderConfig: OrdersSliderConfig
        ) = OrdersSliderFragment().apply {
            arguments = bundleOf(Pair(CONFIG, ordersSliderConfig))
        }
    }

    private lateinit var config: OrdersSliderConfig
    private lateinit var orderUIList: List<OrderUI>
    private lateinit var iOnOrderClick: IOnOrderClick
    private lateinit var iOnShowAllOrdersClick: IOnShowAllOrdersClick

    private lateinit var binding: FragmentSliderOrderBinding

    private val compositeDisposable = CompositeDisposable()
    private val onOrderClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val orderSliderAdapter = OrderSliderAdapter(onOrderClickSubject)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeSubjects()
        getArgs()
    }

    private fun getArgs() {
        config = requireArguments().getParcelable(CONFIG)!!
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
    }

    private fun initOrderPager() {
        val space = resources.getDimension(R.dimen.space_16).toInt()
        binding.vpOrders.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpOrders.adapter = orderSliderAdapter
        binding.vpOrders.addItemDecoration(
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

        when(config.containTitleContainer) {
            true -> binding.llTitleContainer.visibility = View.VISIBLE
            false -> binding.llTitleContainer.visibility = View.GONE
        }

        binding.tvShowAll.setOnClickListener { iOnShowAllOrdersClick.onShowAllOrdersClick() }
    }

    private fun subscribeSubjects() {
        onOrderClickSubject.subscribeBy { orderId ->
            iOnOrderClick.onOrderClick(orderId)
        }.addTo(compositeDisposable)
    }

    fun initCallbacks(
        iOnOrderClick: IOnOrderClick,
        iOnShowAllOrdersClick: IOnShowAllOrdersClick
    ) {
        this.iOnOrderClick = iOnOrderClick
        this.iOnShowAllOrdersClick = iOnShowAllOrdersClick
    }

    fun updateData(orderUIList: List<OrderUI>) {
        this.orderUIList = orderUIList

        val diffUtil = OrderDiffUtilCallback(
            oldList = orderSliderAdapter.orderUIList,
            newList = orderUIList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            orderSliderAdapter.orderUIList = orderUIList
            diffResult.dispatchUpdatesTo(orderSliderAdapter)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

}