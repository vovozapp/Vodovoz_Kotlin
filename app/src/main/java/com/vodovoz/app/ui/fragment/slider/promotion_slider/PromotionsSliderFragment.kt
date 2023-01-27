package com.vodovoz.app.ui.fragment.slider.promotion_slider

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentSliderPromotionBinding
import com.vodovoz.app.ui.adapter.PromotionsSliderAdapter
import com.vodovoz.app.ui.base.BaseHiddenFragment
import com.vodovoz.app.ui.fragment.home.HomeFragmentDirections
import com.vodovoz.app.ui.interfaces.*
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
    private lateinit var iOnFavoriteClick: IOnFavoriteClick
    private lateinit var iOnChangeProductQuantity: IOnChangeProductQuantity
    private lateinit var onNotifyWhenBeAvailable: (Long, String, String) -> Unit
    private lateinit var onNotAvailableMore: () -> Unit

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
        onFavoriteClickSubject = onFavoriteClickSubject,
        onNotifyWhenBeAvailable = { id, name, picture -> onNotifyWhenBeAvailable(id, name, picture) },
        onNotAvailableMore = { onNotAvailableMore() }
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
        binding.tvShowAll.setOnClickListener {
            iOnShowAllPromotionsClick.onShowAllPromotionsClick()
        }
    }

    private fun initPromotionPager() {
        binding.vpPromotions.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpPromotions.adapter = promotionsSliderAdapter
    }

    private fun subscribeSubjects() {
        onPromotionClickSubject.subscribeBy { promotionId ->
            iOnPromotionClick.onPromotionClick(promotionId)
        }.addTo(compositeDisposable)
        onPromotionProductClickSubject.subscribeBy { productId ->
            iOnProductClick.onProductClick(productId)
        }.addTo(compositeDisposable)
        onChangeProductQuantitySubject.subscribeBy {
            iOnChangeProductQuantity.onChangeProductQuantity(it)
        }.addTo(compositeDisposable)
        onFavoriteClickSubject.subscribeBy {
            iOnFavoriteClick.onFavoriteClick(it)
        }.addTo(compositeDisposable)
    }

    fun initCallbacks(
        iOnPromotionClick: IOnPromotionClick,
        iOnProductClick: IOnProductClick,
        iOnShowAllPromotionsClick: IOnShowAllPromotionsClick,
        iOnChangeProductQuantity: IOnChangeProductQuantity,
        iOnFavoriteClick: IOnFavoriteClick,
        onNotifyWhenBeAvailable: (Long, String, String) -> Unit,
        onNotAvailableMore: () -> Unit
    ) {
        this.iOnPromotionClick = iOnPromotionClick
        this.iOnProductClick = iOnProductClick
        this.iOnShowAllPromotionsClick = iOnShowAllPromotionsClick
        this.iOnFavoriteClick = iOnFavoriteClick
        this.iOnChangeProductQuantity = iOnChangeProductQuantity
        this.onNotifyWhenBeAvailable = onNotifyWhenBeAvailable
        this.onNotAvailableMore = onNotAvailableMore
    }

    fun updateData(promotionsSliderBundleUI: PromotionsSliderBundleUI) {
        updateHeader(
            promotionsSliderBundleUI.title,
            promotionsSliderBundleUI.containShowAllButton
        )
        updatePromotionsRecycler(promotionsSliderBundleUI.promotionUIList)
    }

    private fun updateHeader(title: String, containShowAllButton: Boolean) {
        binding.tvName.text = title
        when(containShowAllButton) {
            true -> binding.tvShowAll.visibility = View.VISIBLE
            false -> binding.tvShowAll.visibility = View.INVISIBLE
        }
    }

    private fun updatePromotionsRecycler(promotionUIList: List<PromotionUI>) {
        this.promotionUIList = promotionUIList
        promotionsSliderAdapter.promotionUIList = promotionUIList
        promotionsSliderAdapter.notifyDataSetChanged()
        if (promotionUIList.isNotEmpty()) {
            when (promotionUIList.first().productUIList.isEmpty()) {
                true -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.tvName.setTextAppearance(R.style.TextViewHeaderBlackBold)
                } else {
                    binding.tvName.setTextAppearance(null, R.style.TextViewHeaderBlackBold)
                }
                false -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.tvName.setTextAppearance(R.style.TextViewMediumBlackBold)
                } else {
                    binding.tvName.setTextAppearance(null, R.style.TextViewMediumBlackBold)
                }
            }
        }
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
        compositeDisposable.dispose()
    }

}