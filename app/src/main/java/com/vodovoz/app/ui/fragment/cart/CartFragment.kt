package com.vodovoz.app.ui.fragment.cart

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import com.vodovoz.app.ui.adapter.LinearProductsAdapter
import com.vodovoz.app.ui.adapter.NotAvailableCartItemsAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.diffUtils.ProductDiffUtilCallback
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderConfig
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderFragment
import com.vodovoz.app.ui.extensions.ScrollViewExtensions.setScrollElevation
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import java.lang.StringBuilder

class CartFragment : ViewStateBaseFragment() {

    companion object {
        const val GIFT_ID = "GIFT_ID"
    }

    private lateinit var binding: FragmentMainCartBinding
    private lateinit var viewModel: CartViewModel

    private val compositeDisposable = CompositeDisposable()

    private val onProductClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val onChangeProductQuantitySubject: PublishSubject<Pair<Long, Int>> = PublishSubject.create()
    private val onSwapClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val onFavoriteClickSubject: PublishSubject<Pair<Long, Boolean>> = PublishSubject.create()

    private val bestForYouProductsSliderFragment: ProductsSliderFragment by lazy {
        ProductsSliderFragment.newInstance(ProductsSliderConfig(
            containShowAllButton = false
        )) }

    private val availableCartItemsAdapter = LinearProductsAdapter(
        onProductClickSubject = onProductClickSubject,
        onChangeProductQuantitySubject = onChangeProductQuantitySubject,
        onFavoriteClickSubject = onFavoriteClickSubject,
    )

    private val notAvailableCartItemsAdapter = NotAvailableCartItemsAdapter(
        onProductClickSubject = onProductClickSubject,
        onSwapClickSubject = onSwapClickSubject
    )

    val space: Int by lazy { resources.getDimension(R.dimen.space_16).toInt() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        initViewModel()
        subscribeSubjects()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[CartViewModel::class.java]
    }

    private fun subscribeSubjects() {
        onFavoriteClickSubject.subscribeBy { pair ->
            Toast.makeText(requireContext(), "${pair.first} : ${pair.second}", Toast.LENGTH_LONG).show()
            viewModel.changeFavoriteStatus(pair.first, pair.second)
        }.addTo(compositeDisposable)
        onProductClickSubject.subscribeBy { productId ->
            findNavController().navigate(CartFragmentDirections.actionToProductDetailFragment(productId))
        }.addTo(compositeDisposable)
        onChangeProductQuantitySubject.subscribeBy { pair ->
            viewModel.changeCart(pair.first, pair.second)
        }.addTo(compositeDisposable)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_cart_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.clearCart -> clearCart()
            R.id.orderHistory -> {
                findNavController().navigate(CartFragmentDirections.actionToAllOrdersFragment())
            }
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
        initBestForYouProductsSlider()
        initButtons()
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<ProductUI>(GIFT_ID)?.observe(viewLifecycleOwner) { gift ->
                gift.cartQuantity++
                viewModel.changeCart(gift.id, gift.cartQuantity)
            }
    }

    private fun initActionBar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.incAppBar.tvTitle.text = requireContext().getString(R.string.cart_title)
    }

    private fun initBestForYouProductsSlider() {
        childFragmentManager.beginTransaction().replace(
            R.id.bestForYouProductSliderFragment,
            bestForYouProductsSliderFragment
        ).commit()

        bestForYouProductsSliderFragment.initCallbacks(
            iOnProductClick = { productId -> onProductClickSubject.onNext(productId)},
            iOnChangeProductQuantity = { pair -> onChangeProductQuantitySubject.onNext(pair) },
            iOnFavoriteClick = { pair -> onFavoriteClickSubject.onNext(pair) },
            iOnShowAllProductsClick = {}
        )
    }

    private fun initButtons() {
        binding.goToCatalog.setOnClickListener {
            findNavController().navigate(R.id.catalogFragment)
        }
        binding.showGifts.setOnClickListener {
            when (viewModel.isAlreadyLogin()) {
                true -> {
                    findNavController().navigate(CartFragmentDirections.actionToGiftsBottomFragment(
                        viewModel.getGiftList().toTypedArray()
                    ))
                }
                false -> findNavController().navigate(CartFragmentDirections.actionToProfileFragment())
            }
        }
        binding.regOrder.setOnClickListener {
            findNavController().navigate(CartFragmentDirections.actionToOrderingFragment())
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
            Log.i(LogSettings.LOCAL_DATA, "SIZE CART ${productUIList.size}")
            when (productUIList.isEmpty()) {
                true -> {
                    binding.emptyCartContainer.visibility = View.VISIBLE
                    binding.cartContainer.visibility = View.INVISIBLE
                }
                false -> {
                    binding.emptyCartContainer.visibility = View.INVISIBLE
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
            bestForYouProductsSliderFragment.updateData(listOf(bestForYouCategoryDetailUI))
        }

        viewModel.giftProductListLD.observe(viewLifecycleOwner) { giftList ->
            when(giftList.isEmpty()) {
                true -> binding.showGifts.visibility = View.GONE
                false -> binding.showGifts.visibility = View.VISIBLE
            }
        }

        viewModel.fullPriceLD.observe(viewLifecycleOwner) { binding.tvFullPrice.setPriceText(it) }
        viewModel.discountPriceLD.observe(viewLifecycleOwner) { binding.tvDiscountPrice.setPriceText(it) }
        viewModel.totalPriceLD.observe(viewLifecycleOwner) {
            binding.tvTotalPrice.setPriceText(it)
            binding.regOrder.text = StringBuilder().append("Оформить заказ на ").append(it).append("Р").toString()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fillAvailableProductRecycler(productUIList: List<ProductUI>) {
//        val diffUtil = ProductDiffUtilCallback(
//            oldList = availableCartItemsAdapter.productUIList,
//            newList = productUIList
//        )
//
//        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
//            availableCartItemsAdapter.productUIList = productUIList
//            diffResult.dispatchUpdatesTo(availableCartItemsAdapter)
//        }

        var isCanReturnBottles = false
        for (product in productUIList) {
            if (product.isCanReturnBottles) {
                isCanReturnBottles = true
                break
            }
        }

        availableCartItemsAdapter.productUIList = productUIList
        availableCartItemsAdapter.notifyDataSetChanged()
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


    override fun onStart() {
        super.onStart()
        viewModel.isFirstUpdate = true
        viewModel.updateData()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}