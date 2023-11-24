package com.vodovoz.app.core.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.vodovoz.app.BuildConfig
import com.vodovoz.app.core.network.interceptor.ChangeUrlInterceptor
import com.vodovoz.app.core.network.interceptor.VodovozInterceptor
import com.vodovoz.app.data.MainApi
import com.vodovoz.app.feature.map.api.MapKitFlowApi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
//import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    @IntoSet
    abstract fun providerUrlInterceptor(
        changeUrlInterceptor: ChangeUrlInterceptor
    ): Interceptor

    @Binds
    @Singleton
    @IntoSet
    abstract fun providerHeaderInterceptor(
        vodovozInterceptor: VodovozInterceptor,
    ): Interceptor



    companion object {

        @Provides
        @Singleton
        fun providesOkHttpClient(
            interceptors: Set<@JvmSuppressWildcards Interceptor>,
        ): OkHttpClient {
            val okHttpClient = OkHttpClient.Builder()
            interceptors.forEach {
                if(BuildConfig.DEBUG) {
                    okHttpClient.addInterceptor(it)
                } else if(it !is HttpLoggingInterceptor) {
                    okHttpClient.addInterceptor(it)
                }
            }
            return okHttpClient
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
        }

        @Provides
        @Singleton
        @IntoSet
        fun provideLoggingInterceptor(): Interceptor {
            return HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)
        }

//        @Provides
//        @Named("default")
//        fun providesRetrofit(okHttpClient: OkHttpClient): Retrofit {
//            return Retrofit.Builder()
//                .baseUrl(ApiConfig.VODOVOZ_URL)
//                .addConverterFactory(MoshiConverterFactory.create())
//                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
//                .client(okHttpClient)
//                .build()
//        }

        @Provides
        @Singleton
        @Named("main")
        fun providesMainRetrofit(okHttpClient: OkHttpClient): Retrofit {
            return Retrofit.Builder()
                .baseUrl(ApiConfig.VODOVOZ_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .client(okHttpClient)
                .build()
        }

        @Provides
        @Singleton
        @Named("mapkit")
        fun providesMapKitRetrofit(okHttpClient: OkHttpClient): Retrofit {
            return Retrofit.Builder()
                .baseUrl(ApiConfig.MAPKIT_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .client(okHttpClient)
                .build()
        }

        @Provides
        @Singleton
        fun providesMoshi(): Moshi {
            return Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        }

//        @Provides
//        fun provideApi(@Named("default") retrofit: Retrofit): VodovozApi = retrofit.create()

        @Provides
        @Singleton
        fun provideMainApi(@Named("main") retrofit: Retrofit): MainApi = retrofit.create()

//        @Provides
//        fun provideMapKitApi(@Named("mapkit") retrofit: Retrofit): MapKitApi = retrofit.create()

        @Provides
        @Singleton
        fun provideMapKitFlowApi(@Named("mapkit") retrofit: Retrofit): MapKitFlowApi =
            retrofit.create()
    }
}