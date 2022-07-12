package com.vodovoz.app.ui.components.fragment.slider.promotion_slider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.databinding.FragmentSliderPromotionBinding
import com.vodovoz.app.ui.components.adapter.PromotionsSliderAdapter
import com.vodovoz.app.ui.components.base.BaseHiddenFragment
import com.vodovoz.app.ui.components.interfaces.IOnProductClick
import com.vodovoz.app.ui.components.interfaces.IOnPromotionClick
import com.vodovoz.app.ui.components.interfaces.IOnShowAllPromotionsClick
import com.vodovoz.app.ui.model.PromotionUI
import com.vodovoz.app.ui.model.custom.PromotionsSliderBundleUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class PromotionsSliderFragment : BaseHiddenFragment() {

    private lateinit var promotionUIList: List<PromotionUI>
    private lateinit var iOnPromotionClick: IOnPromotionClick
    private lateinit var iOnShowAllPromotionsClick: IOnShowAllPromotionsClick
    private lateinit var iOnProductClick: IOnProductClick

    private lateinit var binding: FragmentSliderPromotionBinding

    private val compositeDisposable = CompositeDisposable()
    private val onPromotionClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val onPromotionProductClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val onChangeProductQuantitySubject: PublishSubject<Pair<Long, Int>> = PublishSubject.create()
    private val onFavoriteClickSubject: PublishSubject<Pair<Long, Boolean>> = PublishSubject.create()

    private val promotionsSliderAdapter = PromotionsSliderAdapter(
        onPromotionClickSubject = onPromotionClickSubject,
        onProductClickSubject = onPromotionProductClickSubject,
        onChangeProductQuantitySubject = onChangeProductQuantitySubject,
        onFavoriteClickSubject = onFavoriteClickSubject
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeSubjects()
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
        initPromotionPager()
        initHeader()
    }

    private fun initHeader() {
        binding.showAll.setOnClickListener {
            iOnShowAllPromotionsClick.onShowAllPromotionsClick()
        }
    }

    private fun initPromotionPager() {
        binding.promotionRager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.promotionRager.adapter = promotionsSliderAdapter
    }

    private fun subscribeSubjects() {
        onPromotionClickSubject.subscribeBy { promotionId ->
            iOnPromotionClick.onPromotionClick(promotionId)
        }.addTo(compositeDisposable)

        onPromotionProductClickSubject.subscribeBy { productId ->
            iOnProductClick.onProductClick(productId)
        }.addTo(compositeDisposable)
    }

    fun initCallbacks(
        iOnPromotionClick: IOnPromotionClick,
        iOnProductClick: IOnProductClick,
        iOnShowAllPromotionsClick: IOnShowAllPromotionsClick
    ) {
        this.iOnPromotionClick = iOnPromotionClick
        this.iOnProductClick = iOnProductClick
        this.iOnShowAllPromotionsClick = iOnShowAllPromotionsClick
    }

    fun updateData(promotionsSliderBundleUI: PromotionsSliderBundleUI) {
        updateHeader(
            promotionsSliderBundleUI.title,
            promotionsSliderBundleUI.containShowAllButton
        )
        updatePromotionsRecycler(promotionsSliderBundleUI.promotionUIList)
    }

    private fun updateHeader(title: String, containShowAllButton: Boolean) {
        binding.title.text = title
        when(containShowAllButton) {
            true -> binding.showAll.visibility = View.VISIBLE
            false -> binding.showAll.visibility = View.INVISIBLE
        }
    }

    private fun updatePromotionsRecycler(promotionUIList: List<PromotionUI>) {
        this.promotionUIList = promotionUIList
        promotionsSliderAdapter.promotionUIList = promotionUIList
        promotionsSliderAdapter.notifyDataSetChanged()
//        val diffUtil = PromotionDiffUtilCallback(
//            oldList = promotionsSliderAdapter.promotionUIList,
//            newList = promotionUIList
//        )
//
//        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
//            promotionsSliderAdapter.promotionUIList
//            diffResult.dispatchUpdatesTo(promotionsSliderAdapter)
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}