package com.vodovoz.app.ui.fragment.all_promotions

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentAllPromotionsBinding
import com.vodovoz.app.ui.adapter.AllPromotionsAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.diffUtils.PromotionDiffUtilCallback
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.setScrollElevation
import com.vodovoz.app.ui.model.ListOfPromotionFilterUi
import com.vodovoz.app.ui.model.PromotionUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import java.io.Serializable

class AllPromotionsFragment : ViewStateBaseFragment() {

    companion object {
        const val PROMOTION_FILTER = "PROMOTION_FILTER"
    }

    private lateinit var binding: FragmentAllPromotionsBinding
    private lateinit var viewModel: AllPromotionsViewModel

    private val compositeDisposable = CompositeDisposable()

    private val onPromotionClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val allPromotionsAdapter = AllPromotionsAdapter(onPromotionClickSubject)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        getArgs()
    }

    private fun getArgs() {
        viewModel.updateArgs(AllPromotionsFragmentArgs.fromBundle(requireArguments()).dataSource)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[AllPromotionsViewModel::class.java]
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentAllPromotionsBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initPromotionsRecycler()
        initAppBar()
        observeViewModel()
        observeResultLiveData()
    }

    private fun initPromotionsRecycler() {
        val space = resources.getDimension(R.dimen.space_16).toInt()
        binding.rvPromotions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPromotions.adapter = allPromotionsAdapter
        binding.rvPromotions.setScrollElevation(binding.incAppBar.apAppBar)
        binding.rvPromotions.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    with(outRect) {
                        if (parent.getChildAdapterPosition(view) == 0) top = space/2
                        bottom = space/2
                        left = space/2
                        right = space/2
                    }
                }
            }
        )
    }

    private fun initAppBar() {
        binding.incAppBar.imgBack.setOnClickListener { findNavController().popBackStack() }
        binding.incAppBar.tvDropDownTitle.setOnClickListener {
            findNavController().navigate(AllPromotionsFragmentDirections.actionToPromotionFiltersBottomFragment(
                viewModel.selectedFilter.id,
                viewModel.promotionFilterUIList as ListOfPromotionFilterUi
            ))
        }
    }

    override fun update() {
        viewModel.updateData()
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(PROMOTION_FILTER)?.observe(viewLifecycleOwner) { filterId ->
                viewModel.updateSelectedFilter(filterId)
            }
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Loading -> onStateLoading()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Hide -> onStateHide()
                is ViewState.Success -> onStateSuccess()
            }
        }

        viewModel.promotionBundleUILD.observe(viewLifecycleOwner) { promotionBundleUI ->
            fillPromotionRecycler(promotionBundleUI.promotionUIList)
        }

        viewModel.selectedFilterLD.observe(viewLifecycleOwner) { filter ->
            binding.incAppBar.tvDropDownTitle.text = filter.name
        }
    }

    private fun fillPromotionRecycler(promotionUIList: List<PromotionUI>) {
        val diffUtil = PromotionDiffUtilCallback(
            oldList = allPromotionsAdapter.promotionUIList,
            newList = promotionUIList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            allPromotionsAdapter.promotionUIList = promotionUIList
            diffResult.dispatchUpdatesTo(allPromotionsAdapter)
        }
    }

    override fun onStart() {
        super.onStart()

        onPromotionClickSubject.subscribeBy { promotionId ->
            findNavController().navigate(
                AllPromotionsFragmentDirections.actionToPromotionDetailFragment(promotionId)
            )
        }.addTo(compositeDisposable)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.dispose()
    }

    sealed class DataSource : Serializable {
        class ByBanner(val categoryId: Long) : DataSource()
        class All() : DataSource()
    }

}