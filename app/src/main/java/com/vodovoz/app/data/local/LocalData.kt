package com.vodovoz.app.data.local

import android.content.Context
import android.util.Log
import com.vodovoz.app.data.model.common.LastLoginDataEntity
import com.vodovoz.app.util.LogSettings

class LocalData(
    private val context: Context
) : LocalDataSource {

    companion object {
        //Account Settings
        private const val ACCOUNT_SETTINGS = "account_settings"
        private const val USER_ID = "USER_ID"
        private const val EMAIL = "EMAIL"
        private const val PASSWORD = "PASSWORD"

        //Cart Settings
        private const val CART_SETTINGS = "cart_settings"

        //Cookie Settings
        private const val COOKIE_SETTINGS = "COOKIE_SETTINGS"
        private const val COOKIE_SESSION_ID = "cookie_SESSION_id"

        //Favorite Settings
        private const val FAVORITE_SETTINGS = "favorite_settings"
        private const val DEFAULT_USER_FAVORITE_LIST = "DEFAULT_USER_FAVORITE_LIST"

        //Search Settings
        private const val SEARCH_SETTINGS = "search_settings"
        private const val SEARCH_HISTORY = "SEARCH_HISTORY"
    }

    private val accountSettings = context.getSharedPreferences(ACCOUNT_SETTINGS, Context.MODE_PRIVATE)
    private val cartSettings = context.getSharedPreferences(CART_SETTINGS, Context.MODE_PRIVATE)
    private val cookieSettings = context.getSharedPreferences(COOKIE_SETTINGS, Context.MODE_PRIVATE)
    private val favoriteSettings = context.getSharedPreferences(FAVORITE_SETTINGS, Context.MODE_PRIVATE)
    private val searchSettings = context.getSharedPreferences(SEARCH_SETTINGS, Context.MODE_PRIVATE)

    override fun isAvailableCookieSessionId(): Boolean {
        return cookieSettings.contains(COOKIE_SESSION_ID)
    }

    override fun fetchCookieSessionId() = cookieSettings.getString(COOKIE_SESSION_ID, null)

    override fun updateCookieSessionId(cookieSessionId: String) {
        cookieSettings.edit().apply {
            putString(COOKIE_SESSION_ID, cookieSessionId)
        }.apply()
    }

    override fun removeCookieSessionId() {
        cookieSettings.edit().remove(COOKIE_SESSION_ID).apply()
    }

    override fun changeFavoriteStatus(pairList: List<Pair<Long, Boolean>>) {
        val favoriteList = fetchAllFavoriteProducts().toMutableList()

        pairList.forEach { pair ->
            when(pair.second) {
                true -> favoriteList.add(pair.first)
                else -> favoriteList.remove(pair.first)
            }
        }

        when(isAlreadyLogin()) {
            true -> favoriteSettings.edit().putString(fetchUserId().toString(), buildFavoriteStr(favoriteList)).apply()
            false -> favoriteSettings.edit().putString(DEFAULT_USER_FAVORITE_LIST, buildFavoriteStr(favoriteList)).apply()
        }

    }

    override fun fetchAllFavoriteProducts() = when(isAlreadyLogin()) {
        false -> parseFavoriteStr(favoriteSettings.getString(DEFAULT_USER_FAVORITE_LIST, "")!!)
        true -> parseFavoriteStr(favoriteSettings.getString(fetchUserId().toString(), "")!!)
    }

    override fun fetchAllFavoriteProductsOfDefaultUser() = parseFavoriteStr(favoriteSettings.getString(DEFAULT_USER_FAVORITE_LIST, "")!!)

    override fun clearSearchHistory() {
        searchSettings.edit().remove(SEARCH_HISTORY).apply()
    }

    override fun addQueryToHistory(query: String) {
        if (query.isNotEmpty()) {
            val queryList = fetchSearchHistory().toMutableList()
            queryList.add(query)
            searchSettings.edit().putString(SEARCH_HISTORY, buildSearchHistoryStr(queryList)).apply()
        }
    }

    override fun fetchSearchHistory() = parseSearchHistoryStr(searchSettings.getString(SEARCH_HISTORY, "") ?: "")

    private fun parseSearchHistoryStr(searchHistoryStr: String): List<String> {
        val queryList = searchHistoryStr.split(",").toMutableList()
        return queryList.filter { it.isNotEmpty() }
    }

    private fun buildSearchHistoryStr(queryList: List<String>) = StringBuilder().apply {
        queryList.forEach { query ->
            append(query).append(",")
        }
    }.toString()

    private fun parseFavoriteStr(favoriteStr: String): List<Long> {
        Log.i(LogSettings.ID_LOG, favoriteStr)
        val favoriteList = mutableListOf<Long>()
        favoriteStr.split(",").forEach { id ->
            if (id.isNotEmpty()) {
                favoriteList.add(id.toLong())
            }
        }
        return favoriteList.toList()
    }

    private fun buildFavoriteStr(favoriteList: List<Long>): String {
        val favoriteStr = StringBuilder()
        favoriteList.forEach { productId ->
            favoriteStr.append(productId).append(",")
        }
        return favoriteStr.toString()
    }

    override fun fetchLastLoginData(): LastLoginDataEntity {
        val email = when(accountSettings.contains(EMAIL)) {
            true -> accountSettings.getString(EMAIL, null)
            false -> null
        }

        val password = when(accountSettings.contains(PASSWORD)) {
            true -> accountSettings.getString(PASSWORD, null)
            false -> null
        }

        return LastLoginDataEntity(
            email = email,
            password = password
        )
    }

    override fun updateLastLoginData(
        lastLoginDataEntity: LastLoginDataEntity
    ) {
        with(accountSettings.edit()) {
            lastLoginDataEntity.email?.let { putString(EMAIL, it).apply() }
            lastLoginDataEntity.password?.let { putString(PASSWORD, it).apply() }
        }
    }

    override fun fetchUserId() = when(accountSettings.contains(USER_ID)) {
        true -> accountSettings.getLong(USER_ID, 0)
        false -> null
    }

    override fun updateUserId(userId: Long) {
        accountSettings.edit().putLong(USER_ID, userId).apply()
    }

    override fun removeUserId() {
        accountSettings.edit().remove(USER_ID).apply()
    }

    override fun isAlreadyLogin() = fetchUserId() != null

    override fun changeProductQuantityInCart(productId: Long, quantity: Int) {
        val cookieSessionId = fetchCookieSessionId()
        val cart = fetchCart()
        cart[productId] = quantity
        cartSettings.edit().putString(cookieSessionId, buildCartStr(cart)).apply()
        Log.i(LogSettings.DEVELOP_LOG, "Chance local cart $productId : $quantity")
    }

    override fun fetchCart(): HashMap<Long, Int> {
        val userId = fetchCookieSessionId()
        if (!cartSettings.contains(userId.toString())) return HashMap()
        val cartStr = cartSettings.getString(userId.toString(), null)!!
        Log.i(LogSettings.DEVELOP_LOG, "Fetch cart $cartStr")
        return parseCartStr(cartStr)
    }

    override fun clearCart() {
        cartSettings.edit().remove(fetchCookieSessionId().toString()).apply()
    }

    private fun buildCartStr(cart: HashMap<Long, Int>): String {
        val result = StringBuilder()
        cart.entries.forEach { entry ->
            if (entry.value != 0) {
                result.append(entry.key)
                result.append(":")
                result.append(entry.value)
                result.append(",")
            }
        }

        return result.toString()
    }

    private fun parseCartStr(cartStr: String): HashMap<Long, Int> {
        val productStrList = cartStr.split(",")
        val cart = HashMap<Long, Int>()
        productStrList.forEach { productStr ->
            if (productStr.isNotEmpty()) {
                val productSplit = productStr.split(":")
                cart[productSplit.first().toLong()] = productSplit.last().toInt()
            }
        }
        return cart
    }

}