package com.vodovoz.app.ui.fragment.home_bottom_info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.core.network.ApiConfig
import com.vodovoz.app.databinding.FragmentSectionAdditionalInfoBinding
import com.vodovoz.app.feature.home.HomeFragmentDirections

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
        binding.aboutApp.setOnClickListener {
            requireParentFragment().findNavController().navigate(HomeFragmentDirections.actionToAboutAppDialogFragment())
        }
        binding.aboutDelivery.setOnClickListener {
            requireParentFragment().findNavController().navigate(HomeFragmentDirections.actionToWebViewFragment(
                ApiConfig.ABOUT_DELIVERY_URL,
                "О доставке"
            ))
        }
        binding.aboutPay.setOnClickListener {
            requireParentFragment().findNavController().navigate(HomeFragmentDirections.actionToWebViewFragment(
                ApiConfig.ABOUT_PAY_URL,
                "Об оплате"
            ))
        }
        binding.aboutShop.setOnClickListener {
            requireParentFragment().findNavController().navigate(HomeFragmentDirections.actionToWebViewFragment(
                ApiConfig.ABOUT_SHOP_URL,
                "О магазине"
            ))
        }
        binding.services.setOnClickListener {
            requireParentFragment().findNavController().navigate(HomeFragmentDirections.actionToAboutServicesDialogFragment())
        }
        binding.contacts.setOnClickListener { findNavController().navigate(HomeFragmentDirections.actionToContactsFragment()) }
        binding.howToOrder.setOnClickListener {
            requireParentFragment().findNavController().navigate(HomeFragmentDirections.actionToHowToOrderFragment())
        }
    }

}