package com.vodovoz.app.ui.components.fragment.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentAccountBinding
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.ViewStateBaseDialogFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.fragment.slider.order_slider.OrdersSliderConfig
import com.vodovoz.app.ui.components.fragment.slider.order_slider.OrdersSliderFragment
import com.vodovoz.app.ui.model.UserDataUI
import com.vodovoz.app.util.LogSettings

class ProfileFragment : ViewStateBaseDialogFragment() {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var viewModel: ProfileViewModel

    private val ordersSliderFragment: OrdersSliderFragment by lazy {
        OrdersSliderFragment.newInstance(OrdersSliderConfig(
            containTitleContainer = false
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
    ) = FragmentAccountBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initButtons()
        initOrdersSliderFragment()
        observeViewModel()
    }

    override fun update() {
        viewModel.updateData()
    }

    private fun initOrdersSliderFragment() {
        ordersSliderFragment.initCallbacks(
            iOnOrderClick = { orderId ->

            },
            iOnShowAllOrdersClick = {}
        )

        childFragmentManager.beginTransaction().replace(
            R.id.ordersSliderFragment,
            ordersSliderFragment
        ).commit()
    }

    private fun initButtons() {
        with(binding) {
            logout.setOnClickListener { viewModel.logout() }
            header.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionToUserDataFragment())
            }
            loginOrRegister.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionToLoginFragment())
            }
            addresses.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionToSavedAddressesDialogFragment())
            }
            ordersHistory.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionToAllOrdersFragment())
            }
        }
    }

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

        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            Log.i(LogSettings.LOCAL_DATA, "${state::class.simpleName}")
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
            .placeholder(ContextCompat.getDrawable(requireContext(), R.drawable.default_avatar))
            .into(binding.avatar)

        binding.name.text = StringBuilder()
            .append(userDataUI.firstName)
            .append(" ")
            .append(userDataUI.secondName)
            .toString()
    }

}