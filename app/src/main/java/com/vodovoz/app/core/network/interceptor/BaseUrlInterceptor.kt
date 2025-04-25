package com.vodovoz.app.core.network.interceptor

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BaseUrlInterceptor @Inject constructor() : Interceptor {

    @Volatile
    private var scheme: String? = null

    @Volatile
    private var host: String? = null

    fun updateBaseUrl(url: String) {
        url.toHttpUrlOrNull()?.let { httpUrl ->
            scheme = httpUrl.scheme
            host = httpUrl.host
        }
    }

    fun clear(){
        scheme = null
        host   = null
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val currentScheme = scheme
        val currentHost = host

        if (currentScheme == null || currentHost == null) return chain.proceed(request)

        val newRequest = request.newBuilder()
            .url(
                request.url.newBuilder()
                    .scheme(currentScheme)
                    .host(currentHost)
                    .build()
            )
            .build()

        return chain.proceed(newRequest)
    }
}