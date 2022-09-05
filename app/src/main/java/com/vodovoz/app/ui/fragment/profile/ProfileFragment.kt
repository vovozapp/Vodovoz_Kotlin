package com.vodovoz.app.ui.fragment.profile

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.data.config.ApiConfig
import com.vodovoz.app.databinding.FragmentProfileBinding
import com.vodovoz.app.ui.adapter.GridProductsAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseDialogFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.fragment.slider.order_slider.OrdersSliderConfig
import com.vodovoz.app.ui.fragment.slider.order_slider.OrdersSliderFragment
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderConfig
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderFragment
import com.vodovoz.app.ui.model.UserDataUI
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class ProfileFragment : ViewStateBaseDialogFragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel

    private val gridProductsAdapter = GridProductsAdapter(
        onProductClick = {
            findNavController().navigate(ProfileFragmentDirections.actionToProductDetailFragment(it))
        },
        onChangeFavoriteStatus = { productId, status ->
            viewModel.changeFavoriteStatus(productId, status)
        },
        onChangeCartQuantity = { productId, quantity ->
            viewModel.changeCart(productId, quantity)
        },
        onNotAvailableMore = {},
        onNotifyWhenBeAvailable = {}
    )

    private val ordersSliderFragment: OrdersSliderFragment by lazy {
        OrdersSliderFragment.newInstance(OrdersSliderConfig(
            containTitleContainer = false
        ))
    }

    private val viewedProductsSliderFragment: ProductsSliderFragment by lazy {
        ProductsSliderFragment.newInstance(ProductsSliderConfig(
            containShowAllButton = false
        ))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[ProfileViewModel::class.java]
        viewModel.isAlreadyLogin()
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentProfileBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initButtons()
        initOrdersSliderFragment()
        initViewedProductsSlider()
        initBestForYouProductsRecycler()
        observeViewModel()
    }

    override fun update() {
        viewModel.updateData()
    }

    private fun initOrdersSliderFragment() {
        ordersSliderFragment.initCallbacks(
            iOnOrderClick = { orderId ->
                findNavController().navigate(ProfileFragmentDirections.actionToOrderDetailsFragment(orderId))
            },
            iOnShowAllOrdersClick = {}
        )

        childFragmentManager.beginTransaction().replace(
            R.id.ordersSliderFragment,
            ordersSliderFragment
        ).commit()
    }

    private fun initBestForYouProductsRecycler() {
        val space = resources.getDimension(R.dimen.space_16).toInt()
        binding.bestForYouProductsRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.bestForYouProductsRecycler.adapter = gridProductsAdapter
        binding.bestForYouProductsRecycler.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    with(outRect) {
                        if (parent.getChildAdapterPosition(view) % 2 == 0) {
                            left = space
                            right = space/2
                        } else {
                            left = space/2
                            right = space
                        }
                        top = space
                        bottom = space
                    }
                }
            }
        )
    }

    private fun initButtons() {
        with(binding) {
            logout.setOnClickListener { viewModel.logout() }
            header.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionToUserDataFragment())
            }
            btnLoginOrReg.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionToLoginFragment())
            }
            addresses.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionToSavedAddressesDialogFragment())
            }
            ordersHistory.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionToAllOrdersFragment())
            }
            discountCard.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionToDiscountCardFragment())
            }
            binding.deliveryPrice.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionToWebViewFragment(
                    ApiConfig.ABOUT_DELIVERY_URL,
                    "Стоимость доставки"
                ))
            }
            binding.tvRepair.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionToServiceDetailFragment(
                    listOf("sanitar", "remont").toTypedArray(),
                    "remont"
                ))
            }
            binding.tvSanitar.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionToServiceDetailFragment(
                    listOf("sanitar", "remont").toTypedArray(),
                    "sanitar"
                ))
            }
            binding.pastPurchases.setOnClickListener { findNavController().navigate(ProfileFragmentDirections.actionToPastPurchasesFragment()) }
            binding.questionnaires.setOnClickListener { findNavController().navigate(ProfileFragmentDirections.actionToQuestionnairesFragment()) }
            binding.aboutDelivery.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionToWebViewFragment(
                    ApiConfig.ABOUT_DELIVERY_URL,
                    "Стоимость доставки"
                ))
            }
            binding.aboutPay.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionToWebViewFragment(
                    ApiConfig.ABOUT_PAY_URL,
                    "Об оплате"
                ))
            }
            binding.aboutApp.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionToAboutAppDialogFragment())
            }
        }
    }

    private fun initViewedProductsSlider() {
        viewedProductsSliderFragment.initCallbacks(
            iOnProductClick = { productId -> findNavController().navigate(ProfileFragmentDirections.actionToProductDetailFragment(productId)) },
            iOnChangeProductQuantity = { pair -> viewModel.changeCart(pair.first, pair.second) },
            iOnFavoriteClick = { pair -> viewModel.changeFavoriteStatus(pair.first, pair.second) },
            iOnShowAllProductsClick = {}
        )

        childFragmentManager.beginTransaction().replace(
            R.id.viewedProductsSliderFragment,
            viewedProductsSliderFragment
        ).commit()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        viewModel.isAlreadyLoginLD.observe(viewLifecycleOwner) { isAlreadyLogin ->
            when(isAlreadyLogin) {
                true -> {
                    binding.profileContainer.visibility = View.VISIBLE
                    binding.noLoginContainer.visibility = View.GONE
                }
                false -> {
                    binding.profileContainer.visibility = View.GONE
                    binding.noLoginContainer.visibility = View.VISIBLE
                    onStateSuccess()
                }
            }
        }

        viewModel.userDataUILD.observe(viewLifecycleOwner) { userDataUI ->
            fillUserData(userDataUI)
        }

        viewModel.orderUIListLD.observe(viewLifecycleOwner) { orderUIList ->
            ordersSliderFragment.updateData(orderUIList)
        }

        viewModel.viewedProductsSliderLD.observe(viewLifecycleOwner) { categoryDetailUIList ->
            viewedProductsSliderFragment.updateData(categoryDetailUIList)
        }

        viewModel.bestForYouProductsListLD.observe(viewLifecycleOwner) { categoryDetailUI ->
            gridProductsAdapter.productUiList = categoryDetailUI.productUIList
            gridProductsAdapter.notifyDataSetChanged()

            binding.titleBestForYou.text = categoryDetailUI.name
        }

        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Loading -> onStateLoading()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Hide -> onStateHide()
                is ViewState.Success -> onStateSuccess()
            }
        }
    }


    private fun fillUserData(userDataUI: UserDataUI) {
        Glide.with(requireContext())
            .load(userDataUI.avatar)
            .placeholder(ContextCompat.getDrawable(requireContext(), R.drawable.png_default_avatar))
            .into(binding.avatar)

        binding.name.text = StringBuilder()
            .append(userDataUI.firstName)
            .append(" ")
            .append(userDataUI.secondName)
            .toString()
    }

    override fun onResume() {
        super.onResume()
        viewModel.isAlreadyLogin()
    }


}