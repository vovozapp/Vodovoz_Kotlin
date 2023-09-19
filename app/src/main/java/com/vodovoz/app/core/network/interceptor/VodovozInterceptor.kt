package com.vodovoz.app.core.network.interceptor

import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.util.LogSettings
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

class VodovozInterceptor @Inject constructor(
    private val localDataSource: LocalDataSource
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        Timber.tag(LogSettings.NETWORK_LOG).d(chain.request().toString())

        val builder = chain.request().newBuilder()
        localDataSource.fetchCookieSessionId()?.let { cookieSessionId ->
            builder.addHeader("Cookie", cookieSessionId)
        }

        val originalResponse = try {
            chain.proceed(builder.build())
        } catch (t: Throwable) {
            chain.proceed(chain.request())
        }

        if (!localDataSource.isAvailableCookieSessionId()) {
            localDataSource.updateCookieSessionId(
                originalResponse.headers.values("Set-Cookie").first()
            )
        }

        return originalResponse
    }
}