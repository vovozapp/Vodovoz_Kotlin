package com.vodovoz.app.domain.general.model

sealed interface VodovozAction {
    data class Product(val id: Int) : VodovozAction
    data class Category(val id: Int) : VodovozAction
    data class Promotion(val id: Int) : VodovozAction
    data class Brand(val id: Int) : VodovozAction
    data class Url(val url: String) : VodovozAction
    data class UrlWithCookie(val url: String) : VodovozAction

    data class Unknown(val action: String, val id: Any?) : VodovozAction

}

enum class DataAll : VodovozAction {
    AllDiscount, AllNewProducts, AllPromotions, Delivery, Profile, WaterTracker, None
}


sealed class ButtonInfo {

    data class Id(val id: Int) : ButtonInfo()
    data class Action(val dataAll: DataAll) : ButtonInfo()

}
