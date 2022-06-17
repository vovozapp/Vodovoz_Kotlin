package com.vodovoz.app.ui.components.fragment.userData

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentUserDataBinding
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.components.base.FetchStateBaseBottomFragment
import com.vodovoz.app.ui.components.base.FetchStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.fragment.register.RegisterViewModel
import com.vodovoz.app.ui.model.UserDataUI

class UserDataFragment : FetchStateBaseBottomFragment() {

    private lateinit var binding: FragmentUserDataBinding
    private lateinit var viewModel: UserDataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[UserDataViewModel::class.java]
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentUserDataBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
    }.root

    override fun initView() {
        observeViewModel()
        initAppBar()
    }

    private fun initAppBar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.let { noNullActionBar ->
            noNullActionBar.setDisplayHomeAsUpEnabled(true)
            noNullActionBar.setDisplayShowHomeEnabled(true)
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun update() {
        viewModel.updateData()
    }

    private fun observeViewModel() {
        viewModel.fetchStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is FetchState.Hide -> onStateHide()
                is FetchState.Error -> onStateError(state.errorMessage)
                is FetchState.Loading -> onStateLoading()
                is FetchState.Success -> {
                    onStateSuccess()
                    fillUserData(state.data!!)
                }
            }
        }
    }

    private fun fillUserData(userDataUI: UserDataUI) {
        with(binding) {
            Glide.with(requireContext())
                .load(userDataUI.avatar)
                .placeholder(ContextCompat.getDrawable(requireContext(), R.drawable.default_avatar))
                .into(avatar)

            firstName.text = userDataUI.firstName
            secondName.text = userDataUI.secondName
            email.text = userDataUI.email
            birthday.text = userDataUI.birthday
            phone.text = userDataUI.phone
        }
    }

}