package com.vodovoz.app.ui.components.fragment.fixProductsAmount

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentFixAmountProductsBinding
import com.vodovoz.app.ui.components.adapter.fixProductsAdapter.FixAmountProductsAdapter
import com.vodovoz.app.ui.components.adapter.pagingProductsAdapter.list.ListMarginDecoration
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.components.base.FetchStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.fragment.login.LoginViewModel
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.setScrollElevation
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import java.io.Serializable

class FixAmountProductsFragment : FetchStateBaseFragment() {

    private lateinit var binding: FragmentFixAmountProductsBinding
    private lateinit var viewModel: FixAmountProductsViewModel

    private val compositeDisposable = CompositeDisposable()
    private val onProductClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val fixAmountProductAdapter =  FixAmountProductsAdapter(onProductClickSubject = onProductClickSubject)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        getArgs()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[FixAmountProductsViewModel::class.java]
    }

    private fun getArgs() {
        viewModel.updateArgs(FixAmountProductsFragmentArgs.fromBundle(requireArguments()).dataSource)
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentFixAmountProductsBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
    }.root

    override fun initView() {
        initProductRecycler()
        initAppBar()
        observeViewModel()
    }

    private fun initAppBar() {
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initProductRecycler() {
        binding.productRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.productRecycler.adapter = fixAmountProductAdapter
        binding.productRecycler.setScrollElevation(binding.appBar)
        binding.productRecycler.addItemDecoration(ListMarginDecoration(resources.getDimension(R.dimen.primary_space).toInt()))
    }

    override fun update() {
        viewModel.updateData()
    }

    private fun observeViewModel() {
        viewModel.fetchStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is FetchState.Error -> onStateError(state.errorMessage)
                is FetchState.Loading -> onStateLoading()
                is FetchState.Hide -> onStateHide()
                is FetchState.Success -> {
                    onStateSuccess()
                    updateProductRecycler(state.data!!)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateProductRecycler(productUIList: List<ProductUI>) {
        fixAmountProductAdapter.productUIList = productUIList
        fixAmountProductAdapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        onProductClickSubject.subscribeBy { productId ->
            findNavController().navigate(
                FixAmountProductsFragmentDirections.actionFixAmountProductsFragmentToProductDetailFragment(productId)
            )
        }.addTo(compositeDisposable)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

    sealed class DataSource : Serializable {
        class BannerProducts(val categoryId: Long) : DataSource()
    }

}