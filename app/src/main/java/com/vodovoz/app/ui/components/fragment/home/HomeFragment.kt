package com.vodovoz.app.ui.components.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.vodovoz.app.R
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.databinding.FragmentMainHomeBinding
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.fragment.bannerSlider.BannerSliderConfig
import com.vodovoz.app.ui.components.fragment.bannerSlider.BannerSliderFragment
import com.vodovoz.app.ui.components.fragment.historySlider.HistorySliderFragment
import com.vodovoz.app.ui.components.fragment.orderSlider.OrderSliderFragment
import com.vodovoz.app.ui.components.fragment.productSlider.ProductSliderFragment
import com.vodovoz.app.ui.components.fragment.promotionSlider.PromotionSliderFragment
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentMainHomeBinding
    private lateinit var viewModel: HomeViewModel

    private val mainBannerSliderReadySubject: PublishSubject<Boolean> = PublishSubject.create()
    private val secondaryBannerSliderReadySubject: PublishSubject<Boolean> = PublishSubject.create()
    private val historySliderReadySubject: PublishSubject<Boolean> = PublishSubject.create()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMainHomeBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initViewModel()
        initView()
        observeViewModel()
        loadFragments()
    }.root

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[HomeViewModel::class.java]
    }

    private fun initView() {
        binding.update.setOnClickListener {
            viewModel.updateData()
        }

        binding.contentContainer.viewTreeObserver.addOnScrollChangedListener {
            binding.appBar.translationZ =
                if (binding.contentContainer.canScrollVertically(-1)) 16f
                else 0f
        }
    }

    private fun observeViewModel() {
        viewModel.fetchStateLD.observe(viewLifecycleOwner) { state ->
            with (binding) {
                when(state) {
                    is FetchState.Success -> {
                        loadContainer.visibility = View.GONE
                        loadErrorContainer.visibility = View.GONE
                        contentContainer.visibility = View.VISIBLE
                    }
                    is FetchState.Error -> {
                        errorMessage.text = state.errorMessage
                        loadContainer.visibility = View.GONE
                        contentContainer.visibility = View.GONE
                        loadErrorContainer.visibility = View.VISIBLE
                    }
                    is FetchState.Loading -> {
                        contentContainer.visibility = View.GONE
                        loadErrorContainer.visibility = View.GONE
                        loadContainer.visibility = View.VISIBLE
                    }
                }
            }
        }

        Observable.zip(
            mainBannerSliderReadySubject,
            secondaryBannerSliderReadySubject,
            historySliderReadySubject
        ) { _, _, _ ->
            Observable.just(true)
        }.subscribeBy {
            binding.loadContainer.visibility = View.GONE
            binding.loadErrorContainer.visibility = View.GONE
            binding.contentContainer.visibility = View.VISIBLE
        }
    }

    private fun loadFragments() {
        val primaryMargin = resources.getDimension(R.dimen.primary_margin).toInt()
        val primarySpace = resources.getDimension(R.dimen.primary_space).toInt()

        childFragmentManager.beginTransaction()
            .replace(R.id.discountFragment, ProductSliderFragment.newInstance(
                dataSource = ProductSliderFragment.DataSource.Request(sliderType = DataRepository.DISCOUNT_PRODUCT_TYPE),
                config = ProductSliderFragment.Config(false)
            )).commit()

        childFragmentManager.beginTransaction()
            .replace(R.id.noveltiesFragment, ProductSliderFragment.newInstance(
                dataSource = ProductSliderFragment.DataSource.Request(sliderType = DataRepository.NOVELTIES_PRODUCT_TYPE),
                config = ProductSliderFragment.Config(false)
            )).commit()

        childFragmentManager.beginTransaction()
            .replace(R.id.topProductSlider, ProductSliderFragment.newInstance(
                dataSource = ProductSliderFragment.DataSource.Request(sliderType = DataRepository.TOP_PRODUCT),
                config = ProductSliderFragment.Config(false)
            )).commit()

        childFragmentManager.beginTransaction()
            .replace(R.id.bottomProductSlider, ProductSliderFragment.newInstance(
                dataSource = ProductSliderFragment.DataSource.Request(sliderType = DataRepository.BOTTOM_PRODUCT),
                config = ProductSliderFragment.Config(false)
            )).commit()

        childFragmentManager.beginTransaction()
            .replace(R.id.viewedProductSliderFragment, ProductSliderFragment.newInstance(
                dataSource = ProductSliderFragment.DataSource.Request(sliderType = DataRepository.VIEWED_PRODUCT),
                config = ProductSliderFragment.Config(false)
            )).commit()

        childFragmentManager.beginTransaction()
            .replace(R.id.promotionSliderFragment, PromotionSliderFragment.newInstance(
                dataSource = PromotionSliderFragment.DataSource.Request("Акции")),
            ).commit()

        childFragmentManager.beginTransaction()
            .replace(R.id.orderSliderFragment, OrderSliderFragment())
            .commit()

        childFragmentManager.beginTransaction()
            .replace(R.id.mainBannersFragment, BannerSliderFragment.newInstance(
                bannerSliderConfig = BannerSliderConfig(
                    bannerType = DataRepository.MAIN_BANNER_TYPE,
                    marginTop = primaryMargin,
                    marginBottom = primaryMargin,
                    marginLeft = primarySpace,
                    marginRight = primarySpace,
                    ratio = 0.41
                ),
                readyViewSubject = mainBannerSliderReadySubject
            )).commit()

        childFragmentManager.beginTransaction()
            .replace(R.id.secondaryBannersFragment, BannerSliderFragment.newInstance(
                BannerSliderConfig(
                    bannerType = DataRepository.SECONDARY_BANNER_TYPE,
                    marginTop = primaryMargin,
                    marginBottom = primaryMargin,
                    marginLeft = primarySpace,
                    marginRight = primarySpace,
                    ratio = 0.48
                ),
                readyViewSubject = secondaryBannerSliderReadySubject
            )).commit()

        childFragmentManager.beginTransaction()
            .replace(R.id.historiesFragment, HistorySliderFragment.newInstance(
                historySliderReadySubject
            )).commit()
    }

}