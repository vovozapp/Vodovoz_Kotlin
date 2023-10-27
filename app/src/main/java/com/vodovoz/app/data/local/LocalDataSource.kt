package com.vodovoz.app.data.local

interface LocalDataSource {

    //fun fetchLastLoginData(): LastLoginDataEntity
//    fun updateLastLoginData(lastLoginDataEntity: LastLoginDataEntity)

    fun fetchUserId(): Long?
    fun updateUserId(userId: Long)
    fun removeUserId()
    fun updateLastRequestCodeDate(time: Long)
    fun fetchLastRequestCodeDate(): Long
    fun updateLastRequestCodeTimeOut(time: Int)
    fun fetchLastRequestCodeTimeOut(): Int
    fun updateLastAuthPhone(phone: String)
    fun fetchLastAuthPhone(): String

    fun isAlreadyLogin(): Boolean

    fun changeProductQuantityInCart(productId: Long, quantity: Int)
    fun fetchCart(): HashMap<Long, Int>
    fun clearCart()

    fun isAvailableCookieSessionId(): Boolean
    fun fetchCookieSessionId(): String?
    fun updateCookieSessionId(cookieSessionId: String)
    fun removeCookieSessionId()
    fun isOldCookie(): Boolean

    fun changeFavoriteStatus(pairList: List<Pair<Long, Boolean>>)
    fun fetchAllFavoriteProducts(): List<Long>
    fun fetchAllFavoriteProductsOfDefaultUser(): List<Long>

    fun clearSearchHistory()
    fun addQueryToHistory(query: String)
    fun fetchSearchHistory(): List<String>

}