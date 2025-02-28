package com.vodovoz.app.data.vodovoz_service.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.vodovoz.app.core.network.interceptor.CookieHandlerInterceptor
import com.vodovoz.app.data.vodovoz_service.VodovozService
import com.vodovoz.app.data.vodovoz_service.repository.VodovozServiceRepositoryImpl
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class VodovozServiceModule {

    @Binds
    @Singleton
    abstract fun providesVodovozServiceRepository(vodovozServiceRepository: VodovozServiceRepositoryImpl): VodovozServiceRepository


    companion object {

        const val BASE_URL = "https://vodovoz.net/"
        const val URL = "https://vodovoz.net/newmobile_new/"

        @Provides
        @Singleton
        @Named("vodovoz")
        fun providesVodovozRetrofit(@Named("vodovoz") okHttpClient: OkHttpClient): Retrofit {
            return Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(
                    MoshiConverterFactory.create(
                        Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                    )
                )
                .client(okHttpClient)
                .build()
        }

        @Provides
        @Singleton
        fun providesVodovozService(@Named("vodovoz") retrofit: Retrofit): VodovozService {
            return retrofit.create(VodovozService::class.java)
        }

        @Provides
        @Singleton
        @Named("vodovoz")
        fun providesOkHttpClient(
            cookieHandlerInterceptor: CookieHandlerInterceptor,
        ): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(cookieHandlerInterceptor)
                .addInterceptor(HttpLoggingInterceptor())
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
        }


    }

}

fun String.toFullUrl(): String {
    return VodovozServiceModule.BASE_URL.removePrefix("/") + this
}