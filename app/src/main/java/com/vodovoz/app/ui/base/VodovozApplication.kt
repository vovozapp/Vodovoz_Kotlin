package com.vodovoz.app.ui.base

import android.app.Application
import android.util.Log
import com.vodovoz.app.common.notification.NotificationChannels
import com.vodovoz.app.core.network.ApiConfig
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.local.LocalData
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.remote.MapKitApi
import com.vodovoz.app.data.remote.RemoteData
import com.vodovoz.app.data.remote.RemoteDataSource
import com.vodovoz.app.data.remote.VodovozApi
import com.vodovoz.app.util.Keys
import com.vodovoz.app.util.LogSettings
import com.yandex.mapkit.MapKitFactory
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

@HiltAndroidApp
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
        //initYandexMetrica() //todo релиз
        MapKitFactory.setApiKey(Keys.MAPKIT_API_KEY)
        Timber.plant(Timber.DebugTree())
        NotificationChannels.create(this)

        vodovozApi = buildVodovozClient()
        mapKitApi = buildMapkitClient()

        remoteDataSource = RemoteData(vodovozApi, mapKitApi)
        localDataSource = LocalData(applicationContext)
        dataRepository = DataRepository(
            remoteDataSource = remoteDataSource,
            localDataSource = localDataSource
        )
        viewModelFactory = ViewModelFactory(dataRepository = dataRepository)
    }

    private fun initYandexMetrica() {
        val config: YandexMetricaConfig =
            YandexMetricaConfig.newConfigBuilder("6a2669ed-8014-4cd8-8427-3179e72f0635").build()
        YandexMetrica.activate(this, config)
        YandexMetrica.enableActivityAutoTracking(this)
    }

    private fun buildVodovozClient(): VodovozApi {
        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.addInterceptor { chain ->
            Log.d(LogSettings.NETWORK_LOG, chain.request().toString())
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
                localDataSource.updateCookieSessionId(originalResponse.headers.values("Set-Cookie").first())
            }
//
//            if (chain.request().toString().contains("tual")) {
//                Log.d(LogSettings.NETWORK_LOG, "Response: \n${JSONObject(originalResponse.body()!!.string()).toString(2)}")
//            }
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