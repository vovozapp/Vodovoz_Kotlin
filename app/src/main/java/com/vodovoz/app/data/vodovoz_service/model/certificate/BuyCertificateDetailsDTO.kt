package com.vodovoz.app.data.vodovoz_service.model.certificate


import com.squareup.moshi.Json
import androidx.annotation.Keep
import com.vodovoz.app.data.vodovoz_service.model.KNOPKA_ORDER_DTO

@Keep
data class BuyCertificateDetailsDTO(
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "SERTIFICAT")
    val SERTIFICAT: SERTIFICAT_DTO?,
    @Json(name = "DANNYE")
    val DANNYE: List<BUY_CERTIFICATE_TAB_DTO>?,
    @Json(name = "OPLATA")
    val OPLATA: BUY_CERTIFICATE_OPLATA_DTO?,
    @Json(name = "FAQ")
    val FAQ: FAQ_DTO?,
    @Json(name = "KNOPKA")
    val KNOPKA: KNOPKA_ORDER_DTO?
)