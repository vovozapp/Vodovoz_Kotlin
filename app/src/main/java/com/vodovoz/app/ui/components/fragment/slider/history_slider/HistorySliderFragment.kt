package com.vodovoz.app.ui.components.fragment.slider.history_slider

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentSliderHistoryBinding
import com.vodovoz.app.ui.components.adapter.HistoriesSliderAdapter
import com.vodovoz.app.ui.components.base.BaseHiddenFragment
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.diffUtils.HistoryDiffUtilCallback
import com.vodovoz.app.ui.components.fragment.home.HomeFragment
import com.vodovoz.app.ui.components.fragment.home.HomeFragmentDirections
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject


class HistorySliderFragment : BaseHiddenFragment() {

    companion object {
        fun newInstance(
            viewStateSubject: PublishSubject<ViewState>? = null,
            onUpdateSubject: PublishSubject<Boolean>? = null
        ) = HistorySliderFragment().apply {
            this.viewStateSubject = viewStateSubject
            this.onUpdateSubject = onUpdateSubject
        }
    }

    private val compositeDisposable = CompositeDisposable()

    private var viewStateSubject: PublishSubject<ViewState>? = null
    private var onUpdateSubject: PublishSubject<Boolean>? = null
    private var onHistoryClickSubject: PublishSubject<Long> = PublishSubject.create()

    private lateinit var binding: FragmentSliderHistoryBinding
    private lateinit var viewModel: HistorySliderViewModel

    private lateinit var historiesSliderAdapter: HistoriesSliderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[HistorySliderViewModel::class.java]
        viewModel.updateData()
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentSliderHistoryBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initHistoriesRecyclerView()
    }

    private fun initHistoriesRecyclerView() {
        binding.historiesRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val space = resources.getDimension(R.dimen.primary_space).toInt()

        binding.historiesRecycler.addItemDecoration(
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

        binding.historiesRecycler.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (binding.historiesRecycler.width != 0) {
                        binding.historiesRecycler.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        historiesSliderAdapter = HistoriesSliderAdapter(
                            onHistoryClickSubject = onHistoryClickSubject,
                            cardWidth = (binding.historiesRecycler.width - (space * 4))/3
                        )
                        binding.historiesRecycler.adapter = historiesSliderAdapter
                        observeViewModel()
                    }
                }
            }
        )
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

        viewModel.historyUIListLD.observe(viewLifecycleOwner) { historyUIList ->
            val diffUtil = HistoryDiffUtilCallback(
                oldList = historiesSliderAdapter.historyUIList,
                newList = historyUIList
            )

            DiffUtil.calculateDiff(diffUtil).let { diffResult ->
                historiesSliderAdapter.historyUIList = historyUIList
                diffResult.dispatchUpdatesTo(historiesSliderAdapter)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        onHistoryClickSubject.subscribeBy { historyId ->
            requireParentFragment().findNavController().navigate(
                HomeFragmentDirections.actionToFullScreenHistorySliderFragment(
                    historyId,
                    viewModel.historyUIList.toTypedArray()
                )
            )
        }.addTo(compositeDisposable)

        onUpdateSubject?.subscribeBy {
            viewModel.updateData()
        }?.addTo(compositeDisposable)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

}