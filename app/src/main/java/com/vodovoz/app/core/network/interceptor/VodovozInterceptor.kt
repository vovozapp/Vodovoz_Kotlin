package com.vodovoz.app.core.network.interceptor

import com.vodovoz.app.common.cookie.CookieManager
import com.vodovoz.app.util.extensions.debugLog
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class VodovozInterceptor @Inject constructor(
    private val cookieManager: CookieManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {


        debugLog { "LogSettings.NETWORK_LOG: ${chain.request()}" }

        val builder = chain.request().newBuilder()
        cookieManager.fetchCookieSessionId()?.let { cookieSessionId ->
            debugLog { "Cookie added: $cookieSessionId" }
            builder.addHeader("Cookie", cookieSessionId)
        }

        val originalResponse = try {
            chain.proceed(builder.build())
        } catch (t: Throwable) {
            chain.proceed(chain.request())
        }

        val cookieAvail = cookieManager.isAvailableCookieSessionId()
        val cookieIsOld = cookieManager.isOldCookie()
        debugLog { "cookieAvail: $cookieAvail, cookieIsOld: $cookieIsOld" }
        if (!cookieAvail || cookieIsOld) {
            val setCookie = originalResponse.headers.values("Set-Cookie")
            if (setCookie.isNotEmpty()) {
                cookieManager.updateCookieSessionId(
                    originalResponse.headers.values("Set-Cookie")
                        .firstOrNull { it.contains("PHPSESSID") }
                )
            }
        }

        return originalResponse
    }
}