package com.vodovoz.app.feature.buy_certificate.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.model.certificate.BuyCertificateTabModel
import com.vodovoz.app.feature.preorder.model.FieldUi
import com.vodovoz.app.feature.preorder.model.mapToUi

@Immutable
data class BuyCertificateTabUi(
    val id: Int,
    val name: String,
    val fields: List<FieldUi>
){
    companion object{
        val Empty = BuyCertificateTabUi(-1, "", emptyList())
    }
}

fun List<BuyCertificateTabModel>.mapToUi(): List<BuyCertificateTabUi>{
    return map { it.toUi() }
}

fun BuyCertificateTabModel.toUi(): BuyCertificateTabUi{
    return BuyCertificateTabUi(
        id = id,
        name = name,
        fields = fields.mapToUi()
    )
}