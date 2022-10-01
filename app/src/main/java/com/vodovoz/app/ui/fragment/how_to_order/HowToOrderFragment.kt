package com.vodovoz.app.ui.fragment.how_to_order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentHowToOrderBinding
import com.vodovoz.app.ui.adapter.HowToOrderStepsAdapter
import com.vodovoz.app.ui.model.HowToOrderStepUI

class HowToOrderFragment : Fragment() {

    private lateinit var binding: FragmentHowToOrderBinding

    private val howToOrderStepUIList = listOf(
        HowToOrderStepUI(
            stepTitle = "Зарегистрируйтесь",
            stepDetails = "Пройдите регистрацию, воспользуйтесь своим логином и паролем или авторизуйтесь при помощи соцсетей",
            stepImageResId = R.drawable.png_circle_profile
        ),
        HowToOrderStepUI(
            stepTitle = "Найдите товары и добавьте в корзину",
            stepDetails = "Выбирайте, что нужно и складывайте товары в корзину",
            stepImageResId = R.drawable.png_cart
        ),
        HowToOrderStepUI(
            stepTitle = "Выберите адрес и время доставки",
            stepDetails = "Укажите адрес доставки и выберите удобное для вас время",
            stepImageResId = R.drawable.png_
        ),
        HowToOrderStepUI(
            stepTitle = "Оформите заказ",
            stepDetails = "Проверьте свой заказ и нажмите Оформить заказ",
            stepImageResId = R.drawable.png_order_accept
        )
    )
    private val howToOrderStepsAdapter = HowToOrderStepsAdapter(howToOrderStepUIList)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentHowToOrderBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initAppBar()
        initHowToOrderStepsPager()
    }.root

    private fun initAppBar() {
        binding.incAppBar.tvTitle.text = resources.getString(R.string.how_order_title)
        binding.incAppBar.imgBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initHowToOrderStepsPager() {
        binding.vpHowOrder.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpHowOrder.adapter = howToOrderStepsAdapter
        TabLayoutMediator(binding.tlIndicators, binding.vpHowOrder) { _, _ -> }.attach()
    }
}