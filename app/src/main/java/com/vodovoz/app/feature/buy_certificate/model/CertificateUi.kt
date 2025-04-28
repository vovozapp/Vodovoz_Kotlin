package com.vodovoz.app.feature.buy_certificate.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.model.certificate.CertificateModel

@Immutable
data class CertificateUi(
    val id: Int,
    val name: String,
    val image: String,
){
    companion object{
        val Empty = CertificateUi(-1,"","")
    }
}

fun List<CertificateModel>.mapToUi(): List<CertificateUi> {
    return map { it.toUi() }
}

fun CertificateModel.toUi(): CertificateUi {
    return CertificateUi(
        id, name, image
    )
}
