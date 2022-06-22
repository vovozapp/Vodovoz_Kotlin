package com.vodovoz.app.ui.components.fragment.bannerSlider

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.data.model.common.BannerActionEntity
import com.vodovoz.app.databinding.FragmentSliderBannerBinding
import com.vodovoz.app.ui.components.adapter.bannerSliderAdapter.BannerSliderAdapter
import com.vodovoz.app.ui.components.adapter.bannerSliderAdapter.BannerSliderMarginDecoration
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.diffUtils.BannerDiffUtilCallback
import com.vodovoz.app.ui.components.fragment.allPromotions.AllPromotionsFragment
import com.vodovoz.app.ui.components.fragment.fixProductsAmount.FixAmountProductsFragment
import com.vodovoz.app.ui.components.fragment.home.HomeFragmentDirections
import com.vodovoz.app.ui.components.fragment.productsWithoutFilter.ProductsWithoutFiltersFragment
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject


class BannerSliderFragment() : Fragment() {

    companion object {

        private const val CONFIG = "CONFIG"

        fun newInstance(
            bannerSliderConfig: BannerSliderConfig,
            readyViewSubject: PublishSubject<Boolean>,
        ) = BannerSliderFragment().apply {
            this.readyViewSubject = readyViewSubject
            arguments = Bundle().also {
                it.putSerializable(CONFIG, bannerSliderConfig)
            }
        }
    }

    private lateinit var readyViewSubject: PublishSubject<Boolean>

    private lateinit var binding: FragmentSliderBannerBinding
    private lateinit var viewModel: BannerSliderViewModel

    private val compositeDisposable = CompositeDisposable()
    private val onBannerClickSubject: PublishSubject<BannerActionEntity> = PublishSubject.create()
    private lateinit var bannerSliderAdapter: BannerSliderAdapter
    private lateinit var bannerSliderConfig: BannerSliderConfig

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentSliderBannerBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initViewModel()
        getArgs()
        initMainBannerRecyclerView()
    }.root

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[BannerSliderViewModel::class.java]
    }

    private fun getArgs() {
        arguments?.let { args ->
            args.getSerializable(CONFIG)?.let { config ->
                bannerSliderConfig = config as BannerSliderConfig
                viewModel.setBannerType(bannerSliderConfig.bannerType)
            }
        }
    }

    private fun initMainBannerRecyclerView() {
        binding.mainBannersViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.mainBannersViewPager.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (binding.mainBannersViewPager.width != 0) {
                        binding.mainBannersViewPager.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        binding.mainBannersViewPager.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            (binding.mainBannersViewPager.width * bannerSliderConfig.ratio).toInt()
                        )
                        bannerSliderAdapter = BannerSliderAdapter(onBannerClickSubject)
                        binding.mainBannersViewPager.adapter = bannerSliderAdapter
                        observeViewModel()
                    }
                }
            }
        )

        binding.mainBannersViewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    viewModel.restartCountDownTimer(position)
                }
            }
        )

        binding.mainBannersViewPager.addItemDecoration(BannerSliderMarginDecoration(
            marginTop = bannerSliderConfig.marginTop,
            marginBottom = bannerSliderConfig.marginBottom,
            marginLeft = bannerSliderConfig.marginLeft,
            marginRight = bannerSliderConfig.marginRight
        ))
    }

    private fun observeViewModel() {
        viewModel.mainBannerListLD.observe(viewLifecycleOwner) { mainBannerUIDataList ->
            val diffUtil = BannerDiffUtilCallback(
                oldList = bannerSliderAdapter.bannerUIList,
                newList = mainBannerUIDataList
            )

            DiffUtil.calculateDiff(diffUtil).let { diffResult ->
                bannerSliderAdapter.bannerUIList = mainBannerUIDataList
                diffResult.dispatchUpdatesTo(bannerSliderAdapter)
            }

            readyViewSubject.onNext(true)
        }

        viewModel.sateHideLD.observe(viewLifecycleOwner) { stateHide ->
            when(stateHide) {
                true -> binding.root.visibility = View.VISIBLE
                false -> binding.root.visibility = View.GONE
            }
        }

        viewModel.currentBannerIndexLD.observe(viewLifecycleOwner) { index ->
            binding.mainBannersViewPager.setCurrentItem(index, true)
        }
    }

    override fun onStart() {
        super.onStart()
        onBannerClickSubject.subscribeBy { action ->
            val navDirect = when(action) {
                is BannerActionEntity.Brand ->
                    HomeFragmentDirections.actionHomeFragmentToProductsWithoutFiltersFragment(
                        ProductsWithoutFiltersFragment.DataSource.Brand(brandId = action.brandId)
                    )
                is BannerActionEntity.Brands -> {
                    HomeFragmentDirections.actionHomeFragmentToAllBrandsFragment(action.brandIdList.toLongArray())
                }
                is BannerActionEntity.Product ->
                    HomeFragmentDirections.actionHomeFragmentToProductsFragment(action.productId)
                is BannerActionEntity.Products ->
                    HomeFragmentDirections.actionHomeFragmentToFixAmountProductsFragment(
                        FixAmountProductsFragment.DataSource.BannerProducts(categoryId = action.categoryId)
                    )
                is BannerActionEntity.Promotion ->
                    HomeFragmentDirections.actionHomeFragmentToPromotionDetailFragment(action.promotionId)
                is BannerActionEntity.Promotions -> HomeFragmentDirections.actionHomeFragmentToAllPromotionsFragment(
                    AllPromotionsFragment.DataSource.ByBanner(action.categoryId)
                )
                is BannerActionEntity.AllPromotions -> HomeFragmentDirections.actionHomeFragmentToAllPromotionsFragment(
                    AllPromotionsFragment.DataSource.All()
                )
                is BannerActionEntity.Link -> {
                    val openLinkIntent = Intent(Intent.ACTION_VIEW, Uri.parse(action.url))
                    startActivity(openLinkIntent)
                    null
                }
                is BannerActionEntity.Category ->
                    HomeFragmentDirections.actionHomeFragmentToProductsFragment(action.categoryId)
                is BannerActionEntity.Discount -> HomeFragmentDirections.actionHomeFragmentToProductsWithoutFiltersFragment(
                    ProductsWithoutFiltersFragment.DataSource.Discount()
                )
                is BannerActionEntity.Novelties -> HomeFragmentDirections.actionHomeFragmentToProductsWithoutFiltersFragment(
                    ProductsWithoutFiltersFragment.DataSource.Novelties()
                )
            }
            navDirect?.let { parentFragment?.findNavController()?.navigate(navDirect) }
        }.addTo(compositeDisposable)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

}