package com.vodovoz.app.feature.cart.bottles.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.BottleModel

@Immutable
data class BottleUi(
    val name: String,
    val id: Long,
    val articleText: String,
    val description: String,
    val cartQuantity: Int
)

fun BottleModel.toUi(): BottleUi{
    return BottleUi(name, id, articleText, description, cartQuantity)
}

fun List<BottleModel>.mapToUi(): List<BottleUi>{
    return map { it.toUi() }
}
