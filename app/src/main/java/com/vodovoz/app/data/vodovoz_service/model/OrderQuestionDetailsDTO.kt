package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep
import com.vodovoz.app.data.vodovoz_service.model.auth.KNOPKA_AUTH_DTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.KNOPKA_ANALOG_DTO
import com.vodovoz.app.data.vodovoz_service.model.user_data.POLE_DTO

@Keep
data class OrderQuestionDetailsDTO(
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "INFORMIROVANIE")
    val INFORMIROVANIE: String?,
    @Json(name = "LISTADATA")
    val LISTADATA: List<POLE_DTO>?,
    @Json(name = "KNOPKA")
    val KNOPKA: KNOPKA_ORDER_DTO?
)

@Keep
data class KNOPKA_ORDER_DTO(
    @Json(name = "BACKGROUND")
    val BACKGROUND: String?,
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "COLOR")
    val COLOR: String?,
    val ID: String?,
    @Json(name = "TEXT")
    val TEXT: String?,
)