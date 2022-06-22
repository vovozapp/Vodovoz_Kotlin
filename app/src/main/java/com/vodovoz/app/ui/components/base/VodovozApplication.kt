package com.vodovoz.app.ui.components.base

import android.app.Application
import com.vodovoz.app.BuildConfig
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.config.ApiConfig
import com.vodovoz.app.data.local.LocalData
import com.vodovoz.app.data.parser.common.*
import com.vodovoz.app.data.parser.response.*
import com.vodovoz.app.data.parser.response.country.CountrySliderResponseJsonParser
import com.vodovoz.app.data.parser.common.HistoryJsonParser
import com.vodovoz.app.data.parser.response.banner.AdvertisingBannersSliderResponseJsonParser
import com.vodovoz.app.data.parser.response.brand.BrandsSliderResponseJsonParser
import com.vodovoz.app.data.parser.response.catalog.CatalogResponseJsonParser
import com.vodovoz.app.data.parser.response.category.CategoryHeaderResponseJsonParser
import com.vodovoz.app.data.parser.response.paginatedProducts.ProductsByCategoryResponseJsonParser
import com.vodovoz.app.data.parser.response.comment.CommentsSliderResponseJsonParser
import com.vodovoz.app.data.parser.response.discount.DiscountSliderResponseParser
import com.vodovoz.app.data.parser.response.doubleSlider.DoubleSliderResponseJsonParser
import com.vodovoz.app.data.parser.response.history.HistoriesSliderResponseJsonParser
import com.vodovoz.app.data.parser.response.novelties.NoveltiesSliderResponseParser
import com.vodovoz.app.data.parser.response.popular.PopularSliderResponseJsonParser
import com.vodovoz.app.data.parser.response.promotion.PromotionSliderResponseJsonParser
import com.vodovoz.app.data.remote.Api
import com.vodovoz.app.data.remote.RemoteData
import com.vodovoz.app.data.remote.RemoteDataSource
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class VodovozApplication : Application() {

    //Repository
    private lateinit var api: Api
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var dataRepository: DataRepository
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate() {
        super.onCreate()

        api = buildRetrofitClient()
        remoteDataSource = RemoteData(api)
        dataRepository = DataRepository(
            remoteData = remoteDataSource,
            localData = LocalData(applicationContext)
        )
        viewModelFactory = ViewModelFactory(dataRepository = dataRepository)
    }

    private fun buildRetrofitClient() = Retrofit.Builder()
        .baseUrl(ApiConfig.URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                    }
                )
                .build()
        )
        .build()
        .create(Api::class.java)


}