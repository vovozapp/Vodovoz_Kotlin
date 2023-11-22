package com.vodovoz.app.core.network.interceptor

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ChangeUrlInterceptor @Inject constructor() : Interceptor {

    private var mScheme: String? = null
    private var mHost: String? = null

    fun setInterceptor(url: String?) {
        val httpUrl = url?.toHttpUrlOrNull()
        mScheme = httpUrl?.scheme
        mHost = httpUrl?.host
    }

    override fun intercept(chain: Interceptor.Chain): Response {

        var original: Request = chain.request()

        // If new Base URL is properly formatted than replace with old one
        if (mScheme != null && mHost != null) {
            val newUrl: HttpUrl = original.url.newBuilder()
                .scheme(mScheme!!)
                .host(mHost!!)
                .build()
            original = original.newBuilder()
                .url(newUrl)
                .build()
        }
        return chain.proceed(original)
    }
}