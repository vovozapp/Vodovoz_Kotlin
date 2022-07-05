package com.vodovoz.app.ui.components.base

import android.app.Application
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.config.ApiConfig
import com.vodovoz.app.data.local.LocalData
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.remote.Api
import com.vodovoz.app.data.remote.RemoteData
import com.vodovoz.app.data.remote.RemoteDataSource
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class VodovozApplication : Application() {

    //Repository
    private lateinit var api: Api
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var localDataSource: LocalDataSource
    private lateinit var dataRepository: DataRepository
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate() {
        super.onCreate()

        api = buildRetrofitClient()
        remoteDataSource = RemoteData(api)
        localDataSource = LocalData(applicationContext)
        dataRepository = DataRepository(
            remoteDataSource = remoteDataSource,
            localData = localDataSource
        )
        viewModelFactory = ViewModelFactory(dataRepository = dataRepository)
    }

    private fun buildRetrofitClient(): Api {
        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.addInterceptor { chain ->
            val builder = chain.request().newBuilder()

            localDataSource.fetchCookieSessionId()?.let { cookieSessionId ->
                builder.addHeader("Cookie", cookieSessionId)
            }

            val originalResponse = chain.proceed(builder.build())

            if (!localDataSource.isAvailableCookieSessionId()) {
                localDataSource.updateCookieSessionId(originalResponse.headers().values("Set-Cookie").first())
            }

            originalResponse
        }

        return Retrofit.Builder()
            .baseUrl(ApiConfig.URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(clientBuilder.build())
            .build()
            .create(Api::class.java)
    }


}