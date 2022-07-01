package com.vodovoz.app.ui.components.fragment.cart

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentMainCartBinding
import com.vodovoz.app.ui.components.adapter.LinearProductsAdapter
import com.vodovoz.app.ui.components.adapter.NotAvailableCartItemsAdapter
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.ViewStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.diffUtils.ProductDiffUtilCallback
import com.vodovoz.app.ui.components.fragment.slider.product_slider.ProductSliderFragment
import com.vodovoz.app.ui.extensions.ScrollViewExtensions.setScrollElevation
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class CartFragment : ViewStateBaseFragment() {

    companion object {
        const val GIFT_ID = "GIFT_ID"
    }

    private lateinit var binding: FragmentMainCartBinding
    private lateinit var viewModel: CartViewModel

    private val compositeDisposable = CompositeDisposable()

    private val onProductClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val onChangeCartSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val onChangeProductQuantitySubject: PublishSubject<ProductUI> = PublishSubject.create()
    private val onSwapClickSubject: PublishSubject<Long> = PublishSubject.create()

    private val availableCartItemsAdapter = LinearProductsAdapter(
        onProductClickSubject = onProductClickSubject,
        onChangeProductQuantitySubject = onChangeProductQuantitySubject,
    )

    private val notAvailableCartItemsAdapter = NotAvailableCartItemsAdapter(
        onProductClickSubject = onProductClickSubject,
        onSwapClickSubject = onSwapClickSubject
    )

    val space: Int by lazy { resources.getDimension(R.dimen.primary_space).toInt() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[CartViewModel::class.java]
        viewModel.updateData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_cart_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.clearCart -> clearCart()
            R.id.orderHistory -> {}
        }
        return false
    }

    private fun clearCart() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Очистить корзину?")
            .setNegativeButton(resources.getString(R.string.cancel)) { _, _ -> }
            .setPositiveButton(resources.getString(R.string.confirm)) { _, _ ->
                viewModel.clearCart()
            }.show()
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentMainCartBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initActionBar()
        observeViewModel()
        observeResultLiveData()
        initAvailableProductRecycler()
        initNotAvailableProductRecycler()
        initButtons()
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<ProductUI>(GIFT_ID)?.observe(viewLifecycleOwner) { gift ->
                gift.cartQuantity++
                viewModel.changeCart(gift)
            }
    }

    private fun initActionBar() {
        (requireActivity() as AppCompatActivity).let { appCompatActivity ->
            appCompatActivity.setSupportActionBar(binding.toolbar)
            appCompatActivity.supportActionBar?.setDisplayShowHomeEnabled(true)
            appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initButtons() {
        binding.goToCatalog.setOnClickListener {
            findNavController().navigate(R.id.catalogFragment)
        }
        binding.showGifts.setOnClickListener {
            findNavController().navigate(CartFragmentDirections.actionToGiftsBottomFragment(
                viewModel.getGiftList().toTypedArray()
            ))
        }
    }

    private fun initAvailableProductRecycler() {
        binding.availableProductRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.availableProductRecycler.adapter = availableCartItemsAdapter
        binding.cartScrollContainer.setScrollElevation(binding.appBar)
        binding.availableProductRecycler.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.isReturnBottlesContainer.setOnClickListener {
            binding.isReturnBottles.isChecked = !binding.isReturnBottles.isChecked
        }
        binding.availableProductRecycler.addItemDecoration(
            object : RecyclerView.ItemDecoration() {override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                with(outRect) {
                    top = space
                    bottom = space
                    right = space
                }
            }}
        )
    }

    private fun initNotAvailableProductRecycler() {
        binding.notAvailableProductRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.notAvailableProductRecycler.adapter = notAvailableCartItemsAdapter
        binding.notAvailableProductRecycler.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.notAvailableProductRecycler.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    with(outRect) {
                        top = space
                        bottom = space
                        right = space
                    }
                }
            }
        )
    }

    override fun update() {
        when(viewModel.isTryToClearCart) {
            false -> viewModel.updateData()
            true -> viewModel.clearCart()
        }
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Loading -> onStateLoading()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Hide -> onStateHide()
                is ViewState.Success ->  onStateSuccess()
            }
        }

        viewModel.availableProductListLD.observe(viewLifecycleOwner) { productUIList ->
            when (productUIList.isEmpty()) {
                true -> {
                    binding.emptyCartContainer.visibility = View.VISIBLE
                    binding.cartContainer.visibility = View.GONE
                }
                false -> {
                    binding.emptyCartContainer.visibility = View.GONE
                    binding.cartContainer.visibility = View.VISIBLE
                    fillAvailableProductRecycler(productUIList)
                }
            }
        }

        viewModel.notAvailableProductListLD.observe(viewLifecycleOwner) { productUIList ->
            fillNotAvailableProductRecycler(productUIList)
        }

        viewModel.errorLD.observe(viewLifecycleOwner) { error ->
            Snackbar.make(binding.root, error.toString(), Snackbar.LENGTH_SHORT).show()
        }

        viewModel.giftMessageLD.observe(viewLifecycleOwner) { giftMessage ->
            when(giftMessage) {
                null -> binding.giftMessage.visibility = View.GONE
                else -> {
                    binding.giftMessage.visibility = View.VISIBLE
                    binding.giftMessage.text = giftMessage
                }
            }
        }

        viewModel.bestForYouCategoryDetailLD.observe(viewLifecycleOwner) { bestForYouCategoryDetailUI ->
            fillBestForYouProductSlider(bestForYouCategoryDetailUI)
        }

        viewModel.giftProductListLD.observe(viewLifecycleOwner) { giftList ->
            when(giftList.isEmpty()) {
                true -> binding.showGifts.visibility = View.GONE
                false -> binding.showGifts.visibility = View.VISIBLE
            }
        }
    }

    private fun fillAvailableProductRecycler(productUIList: List<ProductUI>) {
        val diffUtil = ProductDiffUtilCallback(
            oldList = availableCartItemsAdapter.productUIList,
            newList = productUIList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            availableCartItemsAdapter.productUIList = productUIList
            diffResult.dispatchUpdatesTo(availableCartItemsAdapter)
        }

        var isCanReturnBottles = false
        for (product in productUIList) {
            if (product.isCanReturnBottles) {
                isCanReturnBottles = true
                break
            }
        }

        when(isCanReturnBottles) {
            true -> binding.isReturnBottlesContainer.visibility = View.VISIBLE
            false -> binding.isReturnBottlesContainer.visibility = View.GONE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fillNotAvailableProductRecycler(productUIList: List<ProductUI>) {
        when(productUIList.isEmpty()) {
            true -> binding.notAvailableProductsContainer.visibility = View.GONE
            false -> {
                binding.notAvailableProductsContainer.visibility = View.VISIBLE
                notAvailableCartItemsAdapter.productUIList = productUIList
                notAvailableCartItemsAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun fillBestForYouProductSlider(categoryDetailUI: CategoryDetailUI) {
        childFragmentManager.beginTransaction()
            .replace(R.id.bestForYouProductSliderFragment, ProductSliderFragment.newInstance(
                dataSource = ProductSliderFragment.DataSource.Args(listOf(categoryDetailUI)),
                config = ProductSliderFragment.Config(true),
                onProductClickSubject = onProductClickSubject,
                onChangeCartSubject = onChangeCartSubject
            )).commit()
    }

    override fun onStart() {
        super.onStart()

        viewModel.isFirstUpdate = true
        viewModel.updateData()

        onProductClickSubject.subscribeBy { productId ->
            findNavController().navigate(CartFragmentDirections.actionToProductDetailFragment(productId))
        }.addTo(compositeDisposable)

        onChangeProductQuantitySubject.subscribeBy { product ->
            viewModel.changeCart(product)
        }.addTo(compositeDisposable)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

}