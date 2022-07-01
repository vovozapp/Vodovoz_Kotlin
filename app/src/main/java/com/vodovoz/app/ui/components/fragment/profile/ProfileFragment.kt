package com.vodovoz.app.ui.components.fragment.profile

import android.os.Bundle
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
import com.vodovoz.app.ui.components.base.ViewStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.model.UserDataUI

class ProfileFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var viewModel: ProfileViewModel

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
        observeViewModel()
    }

    override fun update() {
        viewModel.updateData()
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
                }
            }
        }

        viewModel.userDataUILD.observe(viewLifecycleOwner) { userDataUI ->
            fillUserData(userDataUI)
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
            .placeholder(ContextCompat.getDrawable(requireContext(), R.drawable.default_avatar))
            .into(binding.avatar)

        binding.name.text = StringBuilder()
            .append(userDataUI.firstName)
            .append(" ")
            .append(userDataUI.secondName)
            .toString()
    }

}