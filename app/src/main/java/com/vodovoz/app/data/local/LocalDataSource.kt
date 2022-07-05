package com.vodovoz.app.data.local

import com.vodovoz.app.data.model.common.LastLoginDataEntity

interface LocalDataSource {

    fun fetchLastLoginData(): LastLoginDataEntity
    fun updateLastLoginData(lastLoginDataEntity: LastLoginDataEntity)

    fun fetchUserId(): Long?
    fun updateUserId(userId: Long)
    fun removeUserId()

    fun isAlreadyLogin(): Boolean

    fun changeProductQuantityInCart(productId: Long, quantity: Int)
    fun fetchCart(): HashMap<Long, Int>
    fun clearCart()

    fun isAvailableCookieSessionId(): Boolean
    fun fetchCookieSessionId(): String?
    fun updateCookieSessionId(cookieSessionId: String)
    fun removeCookieSessionId()

    fun changeFavoriteStatus(pairList: List<Pair<Long, Boolean>>)
    fun fetchAllFavoriteProducts(): List<Long>
    fun fetchAllFavoriteProductsOfDefaultUser(): List<Long>

}