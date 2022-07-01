package com.vodovoz.app.ui.components.fragment.slider.promotion_slider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.databinding.FragmentSliderPromotionBinding
import com.vodovoz.app.ui.components.adapter.PromotionsSliderAdapter
import com.vodovoz.app.ui.components.base.BaseHiddenFragment
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.diffUtils.PromotionDiffUtilCallback
import com.vodovoz.app.ui.model.PromotionUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class PromotionSliderFragment : BaseHiddenFragment() {

    companion object {
        fun newInstance(
            dataSource: DataSource,
            onProductClickSubject: PublishSubject<Long>,
            onPromotionClickSubject: PublishSubject<Long>,
            onReadyViewSubject: PublishSubject<Boolean>? = null,
            viewStateSubject: PublishSubject<ViewState>? = null,
            onUpdateSubject: PublishSubject<Boolean>? = null,
            onChangeCartSubject: PublishSubject<Boolean>? = null,
            onShowAllPromotionSubject: PublishSubject<Boolean>? = null
        ) = PromotionSliderFragment().apply {
            this.dataSource = dataSource
            this.onPromotionClickSubject = onPromotionClickSubject
            this.onReadyViewSubject = onReadyViewSubject
            this.viewStateSubject = viewStateSubject
            this.onUpdateSubject = onUpdateSubject
            this.onChangeCartSubject = onChangeCartSubject
            this.onProductClickSubject = onProductClickSubject
            this.onShowAllPromotionSubject = onShowAllPromotionSubject
        }
    }

    private val compositeDisposable = CompositeDisposable()

    private lateinit var dataSource: DataSource
    private lateinit var onProductClickSubject: PublishSubject<Long>
    private lateinit var onPromotionClickSubject: PublishSubject<Long>
    private var onShowAllPromotionSubject: PublishSubject<Boolean>? = null
    private var onReadyViewSubject: PublishSubject<Boolean>? = null
    private var viewStateSubject: PublishSubject<ViewState>? = null
    private var onUpdateSubject: PublishSubject<Boolean>? = null
    private var onChangeCartSubject: PublishSubject<Boolean>? = null

    private lateinit var binding: FragmentSliderPromotionBinding
    private lateinit var viewModel: PromotionSliderViewModel

    private val promotionsSliderAdapter: PromotionsSliderAdapter by lazy {
        PromotionsSliderAdapter(
            onPromotionClickSubject = onPromotionClickSubject,
            onProductClickSubject = onProductClickSubject
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[PromotionSliderViewModel::class.java]
        viewModel.updateArgs(dataSource)
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentSliderPromotionBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initShowAllPromotionsButton()
        initPromotionPager()
        initTitle()
        observeViewModel()
    }

    private fun initShowAllPromotionsButton() {
        binding.showAll.setOnClickListener {
            onShowAllPromotionSubject?.onNext(true)
        }
    }

    private fun initPromotionPager() {
        binding.promotionRager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.promotionRager.adapter = promotionsSliderAdapter
    }

    private fun initTitle() {
        binding.title.text = dataSource.title
        when(dataSource) {
            is DataSource.Request -> {
                binding.showAll.visibility = View.VISIBLE
            }
            is DataSource.Args -> binding.showAll.visibility = View.GONE
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

        viewModel.promotionListLD.observe(viewLifecycleOwner) { promotionList ->
            val diffUtil = PromotionDiffUtilCallback(
                oldList = promotionsSliderAdapter.promotionUIList,
                newList = promotionList
            )

            DiffUtil.calculateDiff(diffUtil).let { diffResult ->
                promotionsSliderAdapter.promotionUIList = promotionList
                diffResult.dispatchUpdatesTo(promotionsSliderAdapter)
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

    sealed class DataSource(val title: String) {
        class Request(title: String) : DataSource(title)
        class Args(
            title: String,
            val promotionUIList: List<PromotionUI>
        ) : DataSource(title)
    }

}