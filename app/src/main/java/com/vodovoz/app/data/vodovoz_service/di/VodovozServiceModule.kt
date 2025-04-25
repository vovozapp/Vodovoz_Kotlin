package com.vodovoz.app.data.vodovoz_service.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.vodovoz.app.core.network.VodovozWebConfig
import com.vodovoz.app.core.network.interceptor.BaseUrlInterceptor
import com.vodovoz.app.core.network.interceptor.ChangeUrlInterceptor
import com.vodovoz.app.core.network.interceptor.CookieHandlerInterceptor
import com.vodovoz.app.data.vodovoz_service.VodovozService
import com.vodovoz.app.data.vodovoz_service.model.VodovozResponseDTO
import com.vodovoz.app.data.vodovoz_service.repository.VodovozServiceRepositoryImpl
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
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

    @Binds
    @Singleton
    abstract fun providerBaseUrlInterceptor(
        baseUrlInterceptor: BaseUrlInterceptor
    ): Interceptor


    companion object {

        const val BASE_URL = "https://vodovoz.net/"
        const val URL = "https://vodovoz.net/newmobile_new/"

        @Provides
        @Singleton
        @Named("vodovoz")
        fun providesVodovozRetrofit(@Named("vodovoz") okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
            return Retrofit.Builder()
                .baseUrl(VodovozWebConfig.VODOVOZ_URL + VodovozWebConfig.VODOVOZ_PATH)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
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
            baseUrlInterceptor: BaseUrlInterceptor
        ): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(cookieHandlerInterceptor)
                .addInterceptor(baseUrlInterceptor)
                .addInterceptor(HttpLoggingInterceptor())
                .connectTimeout(25, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .writeTimeout(25, TimeUnit.SECONDS)
                .build()
        }

    }

}

fun String.toFullUrl(): String {
    return VodovozServiceModule.BASE_URL.removePrefix("/") + this
}