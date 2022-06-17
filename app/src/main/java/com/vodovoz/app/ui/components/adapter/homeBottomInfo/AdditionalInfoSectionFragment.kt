package com.vodovoz.app.ui.components.adapter.homeBottomInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.vodovoz.app.databinding.FragmentSectionAdditionalInfoBinding
import com.vodovoz.app.ui.components.base.VodovozApplication

class AdditionalInfoSectionFragment : Fragment() {

    private lateinit var binding: FragmentSectionAdditionalInfoBinding
    private lateinit var viewModel: AdditionalInfoSectionViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSectionAdditionalInfoBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initViewModel()
        initView()
        observeViewModel()
    }.root

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[AdditionalInfoSectionViewModel::class.java]
    }

    private fun initView() {
        binding.aboutApp.setOnClickListener {}
        binding.aboutDelivery.setOnClickListener {}
        binding.aboutPay.setOnClickListener {}
        binding.aboutShop.setOnClickListener {}
        binding.services.setOnClickListener {}
        binding.contacts.setOnClickListener {}
        binding.howToOrder.setOnClickListener {}
    }

    private fun observeViewModel() {}

}