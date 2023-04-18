package com.vodovoz.app.feature.bottom.howtoorder

import androidx.lifecycle.ViewModel
import com.vodovoz.app.R
import com.vodovoz.app.ui.model.HowToOrderStepUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HowToOrderFlowViewModel @Inject constructor() : ViewModel() {

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

    fun observeHowToOrderSteps() = MutableStateFlow(howToOrderStepUIList).asStateFlow()
}