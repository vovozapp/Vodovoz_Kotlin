package com.vodovoz.app.feature.bottom.howtoorder

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentHowToOrderFlowBinding
import com.vodovoz.app.feature.bottom.howtoorder.adapter.HowToOrderFlowAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HowToOrderFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_how_to_order_flow

    private val binding: FragmentHowToOrderFlowBinding by viewBinding {
        FragmentHowToOrderFlowBinding.bind(
            contentView
        )
    }
    private val viewModel: HowToOrderFlowViewModel by viewModels()

    private val howToOrderAdapter = HowToOrderFlowAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar(resources.getString(R.string.how_order_title))
        observeList()
    }

    private fun observeList() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeHowToOrderSteps()
                .collect {
                    binding.vpHowOrder.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                    howToOrderAdapter.submitList(it)
                    binding.vpHowOrder.adapter = howToOrderAdapter
                    TabLayoutMediator(binding.tlIndicators, binding.vpHowOrder) { _, _ -> }.attach()
                }
        }
    }

}

/*
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
}*/
