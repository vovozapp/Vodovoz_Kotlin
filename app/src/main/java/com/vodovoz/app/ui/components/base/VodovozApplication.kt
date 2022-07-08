package com.vodovoz.app.ui.components.base

import android.app.Application
import android.util.Log
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.config.ApiConfig
import com.vodovoz.app.data.local.LocalData
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.remote.MapKitApi
import com.vodovoz.app.data.remote.VodovozApi
import com.vodovoz.app.data.remote.RemoteData
import com.vodovoz.app.data.remote.RemoteDataSource
import com.vodovoz.app.util.LogSettings
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class VodovozApplication : Application() {

    //Repository
    private lateinit var vodovozApi: VodovozApi
    private lateinit var mapKitApi: MapKitApi
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var localDataSource: LocalDataSource
    private lateinit var dataRepository: DataRepository
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate() {
        super.onCreate()

        vodovozApi = buildVodovozClient()
        mapKitApi = buildMapkitClient()

        remoteDataSource = RemoteData(vodovozApi, mapKitApi)
        localDataSource = LocalData(applicationContext)
        dataRepository = DataRepository(
            remoteDataSource = remoteDataSource,
            localData = localDataSource
        )
        viewModelFactory = ViewModelFactory(dataRepository = dataRepository)
    }

    private fun buildVodovozClient(): VodovozApi {
        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.addInterceptor { chain ->
            Log.i(LogSettings.REQ_RES_LOG, chain.request().toString())
            val builder = chain.request().newBuilder()

            localDataSource.fetchCookieSessionId()?.let { cookieSessionId ->
                builder.addHeader("Cookie", cookieSessionId)
            }

            val originalResponse = chain.proceed(builder.build())

            if (!localDataSource.isAvailableCookieSessionId()) {
                localDataSource.updateCookieSessionId(originalResponse.headers().values("Set-Cookie").first())
            }

            Log.i(LogSettings.REQ_RES_LOG, originalResponse.toString())
            originalResponse
        }

        return Retrofit.Builder()
            .baseUrl(ApiConfig.VODOVOZ_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(clientBuilder.build())
            .build()
            .create(VodovozApi::class.java)
    }

    private fun buildMapkitClient() = Retrofit.Builder()
        .baseUrl(ApiConfig.MAPKIT_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()
        .create(MapKitApi::class.java)

}