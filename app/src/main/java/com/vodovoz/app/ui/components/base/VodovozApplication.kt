package com.vodovoz.app.ui.components.base

import android.app.Application
import com.vodovoz.app.BuildConfig
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.config.ApiConfig
import com.vodovoz.app.data.local.LocalData
import com.vodovoz.app.data.parser.common.*
import com.vodovoz.app.data.parser.response.*
import com.vodovoz.app.data.parser.response.country.CountrySliderResponseJsonParser
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

    //CommonJsonParser
    private lateinit var categoryDetailJsonParser: CategoryDetailJsonParser
    private lateinit var commentJsonParser: CommentJsonParser
    private lateinit var historyJsonParser: HistoryJsonParser

    private fun initCommonJsonParser() {
        categoryDetailJsonParser = CategoryDetailJsonParser()
        commentJsonParser = CommentJsonParser()
        historyJsonParser = HistoryJsonParser()
    }

    //FeaturesJsonParser
    private lateinit var bannerResponseJsonParser: BannerResponseJsonParser
    private lateinit var brandResponseJsonParser: BrandResponseJsonParser
    private lateinit var catalogResponseJsonParser: CatalogResponseJsonParser
    private lateinit var categoryDetailResponseJsonParser: CategoryDetailResponseJsonParser
    private lateinit var categoryPopularResponseJsonParser: CategoryPopularResponseJsonParser
    private lateinit var commentResponseJsonParser: CommentResponseJsonParser
    private lateinit var countrySliderResponseJsonParser: CountrySliderResponseJsonParser
    private lateinit var discountCategoryResponseJsonParser: DiscountCategoryResponseParser
    private lateinit var doubleCategoryResponseJsonParser: DoubleCategoryResponseJsonParser
    private lateinit var historyResponseJsonParser: HistoryResponseJsonParser
    private lateinit var noveltiesCategoryResponseParser: NoveltiesCategoryResponseParser
    private lateinit var promotionSliderResponseJsonParser: PromotionSliderResponseJsonParser
    private lateinit var categoryHeaderResponseJsonParser: CategoryHeaderResponseJsonParser

    private fun initFeaturesJsonParser() {
        bannerResponseJsonParser = BannerResponseJsonParser()
        categoryPopularResponseJsonParser = CategoryPopularResponseJsonParser()
        commentResponseJsonParser = CommentResponseJsonParser(commentJsonParser)
        historyResponseJsonParser = HistoryResponseJsonParser(historyJsonParser)
        categoryDetailResponseJsonParser = CategoryDetailResponseJsonParser(categoryDetailJsonParser)
        categoryHeaderResponseJsonParser = CategoryHeaderResponseJsonParser()
        catalogResponseJsonParser = CatalogResponseJsonParser()
        doubleCategoryResponseJsonParser = DoubleCategoryResponseJsonParser()
        discountCategoryResponseJsonParser = DiscountCategoryResponseParser()
        noveltiesCategoryResponseParser = NoveltiesCategoryResponseParser()
        brandResponseJsonParser = BrandResponseJsonParser()
    }

    //Repository
    private lateinit var api: Api
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var dataRepository: DataRepository
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate() {
        super.onCreate()

        initCommonJsonParser()
        initFeaturesJsonParser()

        api = buildRetrofitClient()

        remoteDataSource = RemoteData(
            api,
            bannerResponseJsonParser = bannerResponseJsonParser,
            catalogResponseJsonParser = catalogResponseJsonParser,
            doubleCategoryResponseJsonParser = doubleCategoryResponseJsonParser,
            discountCategoryResponseParser = discountCategoryResponseJsonParser,
            noveltiesCategoryResponseParser = noveltiesCategoryResponseParser,
            commentResponseJsonParser = commentResponseJsonParser,
            historyResponseJsonParser = historyResponseJsonParser,
            categoryPopularResponseJsonParser = categoryPopularResponseJsonParser,
            brandResponseJsonParser = brandResponseJsonParser,
            categoryHeaderResponseJsonParser = categoryHeaderResponseJsonParser
        )

        dataRepository = DataRepository(
            remoteData = remoteDataSource,
            localData = LocalData(applicationContext),
            categoryDetailResponseJsonParser = categoryDetailResponseJsonParser
        )

        viewModelFactory = ViewModelFactory(
            dataRepository = dataRepository
        )
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