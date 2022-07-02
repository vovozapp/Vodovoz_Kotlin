package com.vodovoz.app.ui.components.fragment.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentMainHomeBinding
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.ViewStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.fragment.home_bottom_info.BottomInfoFragment
import com.vodovoz.app.ui.components.fragment.paginated_products_catalog_without_filters.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.ui.components.fragment.popup_news.PopupNewsBottomFragment
import com.vodovoz.app.ui.extensions.BannerActionExtensions.invoke
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class HomeFragment : ViewStateBaseFragment() {

    companion object {
        private const val IS_FIRST_TIME_SHOW = "IS_FIRST_TIME_SHOW"
    }

    private lateinit var binding: FragmentMainHomeBinding
    private lateinit var viewModel: HomeViewModel

    private val compositeDisposable = CompositeDisposable()

    private val space: Int by lazy { resources.getDimension(R.dimen.primary_space).toInt() }
    private var isFirstUpdate = true
    private var isFirstTimeShow = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let { bundle ->
            Log.i(LogSettings.LOCAL_DATA, "NO NULL BUNDLE")
            if (bundle.containsKey(IS_FIRST_TIME_SHOW)) {
                Log.i(LogSettings.LOCAL_DATA, "EXIST")
                isFirstTimeShow = bundle.getBoolean(IS_FIRST_TIME_SHOW)
                Log.i(LogSettings.LOCAL_DATA, isFirstTimeShow.toString())
            }
        }
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[HomeViewModel::class.java]
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentMainHomeBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initSubjects()
        loadFragments()
        init()
        observeViewModel()
    }

    private fun init() {
//        binding.contentContainer.viewTreeObserver.addOnScrollChangedListener {
//            binding.appBar.translationZ =
//                if (binding.contentContainer.canScrollVertically(-1)) 16f
//                else 0f
//        }

        binding.refreshContainer.setOnRefreshListener {
            update()
        }
    }

    private fun observeViewModel() {
        viewModel.popupNewsUILD.observe(viewLifecycleOwner) { popupNewsUI ->
            if (isFirstTimeShow) {
                isFirstTimeShow = false
                val popupNewsDialog = PopupNewsBottomFragment.newInstance(
                    popupNewsUI = popupNewsUI,
                    iOnInvokeAction = { action -> action.invoke(findNavController(), requireActivity()) }
                )
                popupNewsDialog.show(childFragmentManager, popupNewsDialog::class.simpleName)
            }
        }
    }

    private fun initSubjects() {

    }


    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_FIRST_TIME_SHOW, isFirstTimeShow)
    }

    //Общие subjects
    private val onProductClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val onShowAllProductsSubject: PublishSubject<PaginatedProductsCatalogWithoutFiltersFragment.DataSource> = PublishSubject.create()

    init {
        onProductClickSubject.subscribeBy { productId ->
            findNavController().navigate(HomeFragmentDirections.actionToProductDetailFragment(productId))
        }.addTo(compositeDisposable)

        onShowAllProductsSubject.subscribeBy { dataSource ->
            findNavController().navigate(HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(dataSource))
        }.addTo(compositeDisposable)
    }

//    //Слайдер рекламных баннеров
//    private val viewStateAdvertisingBannerSliderSubject: PublishSubject<ViewState> = PublishSubject.create()
//    private val onUpdateAdvertisingBannerSliderSubject: PublishSubject<Boolean> = PublishSubject.create()
//    private val onAdvertisingBannerClickSubject: PublishSubject<ActionEntity> = PublishSubject.create()
//
//    init {
//        onAdvertisingBannerClickSubject.subscribeBy { bannerActionEntity ->
//            bannerActionEntity.invoke(
//                navController = findNavController(),
//                activity = requireActivity()
//            )
//        }.addTo(compositeDisposable)
//    }
//
//    private fun loadAdvertisingBannerSlider() {
//        childFragmentManager.beginTransaction().replace(R.id.mainBannersFragment, BannersSliderFragment.newInstance(
//            viewStateSubject = viewStateAdvertisingBannerSliderSubject,
//            onUpdateSubject = onUpdateAdvertisingBannerSliderSubject,
//            onBannerClickSubject = onAdvertisingBannerClickSubject,
//            bannerSliderConfig = BannersSliderFragment.BannerSliderConfig(
//                bannerType = BannerSliderViewModel.ADVERTISING_BANNERS_SLIDER,
//                marginTop = space/2,
//                marginBottom = space/2,
//                marginLeft = space,
//                marginRight = space,
//                ratio = 0.41
//            )
//        )).commit()
//    }

    private fun loadFragments() {

    }

    override fun update() {

    }

}