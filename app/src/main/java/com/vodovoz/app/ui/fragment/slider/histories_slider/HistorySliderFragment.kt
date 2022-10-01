package com.vodovoz.app.ui.fragment.slider.histories_slider

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentSliderHistoryBinding
import com.vodovoz.app.ui.adapter.HistoriesSliderAdapter
import com.vodovoz.app.ui.base.BaseHiddenFragment
import com.vodovoz.app.ui.diffUtils.HistoryDiffUtilCallback
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.extensions.ViewExtensions.onRenderFinished
import com.vodovoz.app.ui.interfaces.IOnHistoryClick
import com.vodovoz.app.ui.model.HistoryUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject


class HistorySliderFragment : BaseHiddenFragment() {

    private lateinit var historyUIList: List<HistoryUI>
    private lateinit var iOnHistoryClick: IOnHistoryClick

    private lateinit var binding: FragmentSliderHistoryBinding

    private val compositeDisposable = CompositeDisposable()
    private val onAdapterReadySubject: BehaviorSubject<List<HistoryUI>> = BehaviorSubject.create()
    private val onHistoryClickSubject: PublishSubject<Long> = PublishSubject.create()
    private lateinit var historiesSliderAdapter: HistoriesSliderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeSubjects()
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentSliderHistoryBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this }.root

    override fun initView() {
        initHistoriesRecyclerView()
    }

    private fun initHistoriesRecyclerView() {
        binding.rvHistories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val space = resources.getDimension(R.dimen.space_16).toInt()
        historiesSliderAdapter = HistoriesSliderAdapter(
            onHistoryClickSubject = onHistoryClickSubject,
            cardWidth = 0
        )
        binding.rvHistories.adapter = historiesSliderAdapter
        onAdapterReadySubject.subscribeBy { historyUIList ->
            this.historyUIList = historyUIList
            updateView(historyUIList)
        }.addTo(compositeDisposable)

        binding.rvHistories.addMarginDecoration { rect, view, parent, state ->
            if (parent.getChildAdapterPosition(view) == 0) rect.left = space
            if (parent.getChildAdapterPosition(view) == state.itemCount - 1) rect.right = space
            else rect.right = space/2
            rect.top = space / 2
            rect.bottom = space / 2
        }
    }

    private fun subscribeSubjects() {
        onHistoryClickSubject.subscribeBy { historyId ->
            iOnHistoryClick.onHistoryClick(historyId)
        }.addTo(compositeDisposable)
    }

    fun initCallbacks(iOnHistoryClick: IOnHistoryClick) {
        this.iOnHistoryClick = iOnHistoryClick
    }

    fun updateData(historyUIList: List<HistoryUI>) {
        onAdapterReadySubject.onNext(historyUIList)
    }

    private fun updateView(historyUIList: List<HistoryUI>) {
        val diffUtil = HistoryDiffUtilCallback(
            oldList = historiesSliderAdapter.historyUIList,
            newList = historyUIList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            historiesSliderAdapter.historyUIList = historyUIList
            diffResult.dispatchUpdatesTo(historiesSliderAdapter)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

}