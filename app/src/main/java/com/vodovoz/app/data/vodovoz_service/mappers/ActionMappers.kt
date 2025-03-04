package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.model.ACTION_DTO
import com.vodovoz.app.domain.general.model.DataAllAction
import com.vodovoz.app.domain.general.model.VodovozAction


fun ACTION_DTO.toAction(): VodovozAction? {
    val id = ID ?: return null
    val action = ACTION ?: return null

    return when (action.uppercase()) {
        "TOVAR" -> VodovozAction.Product(id.toLongOrNull() ?: return null)
        "TOVARY" -> VodovozAction.Products(id.toLongOrNull() ?: return null)
        "RAZDEL" -> VodovozAction.Category(id.toLongOrNull() ?: return null)
        "AKCIYA" -> VodovozAction.Promotion(id.toLongOrNull() ?: return null)
        "AKCII" -> VodovozAction.Promotions(id.toLongOrNull() ?: return null)
        "BRAND" -> VodovozAction.Brand(id.toLongOrNull() ?: return null)
        "URL" -> VodovozAction.Url(id)
        "URLKYKI" -> VodovozAction.UrlWithCookie(id)
        "DANNYEVSE" -> id.toDataAllAction()
        else -> VodovozAction.Unknown(action, id)
    }
}

fun String.toDataAllAction(): DataAllAction {
    return when (this) {
        "vseskidki" -> DataAllAction.AllDiscount
        "vsenovinki" -> DataAllAction.AllNewProducts
        "vseakcii" -> DataAllAction.AllPromotions
        "dostavka" -> DataAllAction.Delivery
        "profil" -> DataAllAction.Profile
        "trekervodi" -> DataAllAction.WaterTracker
        "pokypkasertificat" -> DataAllAction.BuyCertificate
        else -> DataAllAction.Unknown
    }
}


