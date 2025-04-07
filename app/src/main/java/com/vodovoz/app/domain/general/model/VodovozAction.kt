package com.vodovoz.app.domain.general.model

sealed interface VodovozAction {
    data class Product(val id: Long) : VodovozAction
    data class Products(val blockId: Long, val bannerId: Long) : VodovozAction
    data class Category(val id: Long) : VodovozAction
    data class Promotion(val id: Long) : VodovozAction
    data class Promotions(val blockId: Long, val bannerId: Long) : VodovozAction
    data class Brand(val id: Long) : VodovozAction
    data class Url(val url: String) : VodovozAction
    data class UrlWithCookie(val url: String) : VodovozAction

    data class Unknown(val action: String, val id: Any?) : VodovozAction

}

enum class DataAllAction : VodovozAction {
    AllDiscount, AllNewProducts, AllPromotions ,Delivery, Profile, WaterTracker, BuyCertificate, Unknown;
}


sealed class ButtonAction {

    data class Id(val id: Int) : ButtonAction()
    data class Action(val value: DataAllAction) : ButtonAction()

}
