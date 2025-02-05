package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.model.HARAKTERISTIK_DTO
import com.vodovoz.app.domain.general.model.DataAll
import com.vodovoz.app.domain.general.model.VodovozAction


fun HARAKTERISTIK_DTO.toAction(): VodovozAction? {
    val id = ID ?: return null
    val action = ACTION ?: return null

    return when (action.uppercase()) {
        "TOVAR" -> VodovozAction.Product(id.toIntOrNull() ?: return null)
        "TOVARY" -> VodovozAction.Products(id.toIntOrNull() ?: return null)
        "RAZDEL" -> VodovozAction.Category(id.toIntOrNull() ?: return null)
        "AKCIYA" -> VodovozAction.Promotion(id.toIntOrNull() ?: return null)
        "AKCII" -> VodovozAction.Promotions(id.toIntOrNull() ?: return null)
        "BRAND" -> VodovozAction.Brand(id.toIntOrNull() ?: return null)
        "URL" -> VodovozAction.Url(id)
        "URLKYKI" -> VodovozAction.UrlWithCookie(id)
        "DANNYEVSE" -> id.toDataAll()
        else -> VodovozAction.Unknown(action, id)
    }
}

fun String.toDataAll(): DataAll {
    return when (this) {
        "vseskidki" -> DataAll.AllDiscount
        "vsenovinki" -> DataAll.AllNewProducts
        "vseakcii" -> DataAll.AllPromotions
        "dostavka" -> DataAll.Delivery
        "profil" -> DataAll.Profile
        "trekervodi" -> DataAll.WaterTracker
        else -> DataAll.None
    }
}


