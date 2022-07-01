package com.vodovoz.app.ui.components.fragment.products_catalog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentFixAmountProductsBinding
import com.vodovoz.app.ui.components.adapter.LinearProductsAdapter
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.ViewStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.decoration.ListMarginDecoration
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.setScrollElevation
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import java.io.Serializable

class ProductsCatalogFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentFixAmountProductsBinding
    private lateinit var viewModel: ProductsCatalogViewModel

    private val compositeDisposable = CompositeDisposable()

    private val onProductClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val onChangeProductQuantitySubject: PublishSubject<ProductUI> = PublishSubject.create()

    private val linearProductsAdapter =  LinearProductsAdapter(
        onProductClickSubject = onProductClickSubject,
        onChangeProductQuantitySubject = onChangeProductQuantitySubject
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        getArgs()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[ProductsCatalogViewModel::class.java]
    }

    private fun getArgs() {
        viewModel.updateArgs(ProductsCatalogFragmentArgs.fromBundle(requireArguments()).dataSource)
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentFixAmountProductsBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

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
        binding.productRecycler.adapter = linearProductsAdapter
        binding.productRecycler.setScrollElevation(binding.appBar)
        binding.productRecycler.addItemDecoration(ListMarginDecoration(resources.getDimension(R.dimen.primary_space).toInt()))
    }

    override fun update() {
        viewModel.updateData()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Loading -> onStateLoading()
                is ViewState.Hide -> onStateHide()
                is ViewState.Success -> onStateSuccess()
            }
        }

        viewModel.productUIListLD.observe(viewLifecycleOwner) { productUIList ->
            linearProductsAdapter.productUIList = productUIList
            linearProductsAdapter.notifyDataSetChanged()
        }
    }

    override fun onStart() {
        super.onStart()
        onProductClickSubject.subscribeBy { productId ->
            findNavController().navigate(
                ProductsCatalogFragmentDirections.actionToProductDetailFragment(productId)
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