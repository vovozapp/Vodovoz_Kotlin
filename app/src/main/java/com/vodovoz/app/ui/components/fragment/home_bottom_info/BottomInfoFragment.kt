package com.vodovoz.app.ui.components.fragment.home_bottom_info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vodovoz.app.databinding.FragmentSectionAdditionalInfoBinding

class BottomInfoFragment : Fragment() {

    private lateinit var binding: FragmentSectionAdditionalInfoBinding

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
        initView()
    }.root

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