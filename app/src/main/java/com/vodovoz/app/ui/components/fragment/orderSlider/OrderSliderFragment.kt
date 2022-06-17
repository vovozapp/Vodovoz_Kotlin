package com.vodovoz.app.ui.components.fragment.orderSlider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentSliderOrderBinding
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.components.base.FetchStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.adapter.orderSliderAdapter.OrderDiffUtilCallback
import com.vodovoz.app.ui.components.adapter.orderSliderAdapter.OrderSliderAdapter
import com.vodovoz.app.ui.components.adapter.orderSliderAdapter.OrderSliderMarginDecoration
import com.vodovoz.app.ui.model.OrderUI
import io.reactivex.rxjava3.subjects.PublishSubject

class OrderSliderFragment : FetchStateBaseFragment() {

    private lateinit var binding: FragmentSliderOrderBinding
    private lateinit var viewModel: OrderSliderViewModel

    private val onOrderClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val orderSliderAdapter = OrderSliderAdapter(onOrderClickSubject)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[OrderSliderViewModel::class.java]
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentSliderOrderBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
    }.root

    override fun initView() {
        initOrderPager()
        observeViewModel()
    }

    override fun update() { viewModel.updateData() }

    private fun initOrderPager() {
        binding.orderPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.orderPager.addItemDecoration(OrderSliderMarginDecoration(
            resources.getDimension(R.dimen.primary_space).toInt()
        ))
        binding.orderPager.adapter = orderSliderAdapter
    }

    private fun observeViewModel() {
        viewModel.fetchStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is FetchState.Error -> onStateError(state.errorMessage)
                is FetchState.Loading,
                is FetchState.Hide -> onStateHide()
                is FetchState.Success -> {
                    onStateSuccess()
                    fillOrderPager(state.data!!)
                }
            }
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
    }

}