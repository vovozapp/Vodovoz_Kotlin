package com.vodovoz.app.ui.components.fragment.account

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
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.components.base.FetchStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.model.UserDataUI

class AccountFragment : FetchStateBaseFragment() {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var viewModel: AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[AccountViewModel::class.java]
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentAccountBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
    }.root

    override fun update() {
        viewModel.updateData()
    }

    override fun initView() {
        onStateSuccess()
        viewModel.isAlreadyLogin()
        initLoginOrRegisterButton()
        initButtons()
        observeViewModel()
    }

    private fun initLoginOrRegisterButton() {

    }

    private fun initButtons() {
        with(binding) {
            logout.setOnClickListener { viewModel.logout() }
            header.setOnClickListener {
                findNavController().navigate(AccountFragmentDirections.actionAccountFragmentToUserDataFragment())
            }
            loginOrRegister.setOnClickListener {
                findNavController().navigate(AccountFragmentDirections.actionAccountFragmentToLoginFragment())
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

        viewModel.fetchStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is FetchState.Loading -> onStateLoading()
                is FetchState.Error -> onStateError(state.errorMessage)
                is FetchState.Hide -> onStateHide()
                is FetchState.Success -> {
                    fillUserData(state.data!!)
                    onStateSuccess()
                }
            }
        }
    }

    private fun fillUserData(data: UserDataUI) {
        Glide.with(requireContext())
            .load(data.avatar)
            .placeholder(ContextCompat.getDrawable(requireContext(), R.drawable.default_avatar))
            .into(binding.avatar)

        binding.name.text = StringBuilder()
            .append(data.firstName)
            .append(" ")
            .append(data.secondName)
            .toString()
    }

}