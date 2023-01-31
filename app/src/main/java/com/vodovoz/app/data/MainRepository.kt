package com.vodovoz.app.data

import com.vodovoz.app.BuildConfig
import okhttp3.ResponseBody
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val api: MainApi
) {

    //Слайдер рекламных баннеров на главной странице
    suspend fun fetchAdvertisingBannersSlider(): ResponseBody {
        return api.fetchAdvBanners(action = "slayder")
    }

    //Слайдер историй на главное странице
    suspend fun fetchHistoriesSlider(): ResponseBody {
        return api.fetchHistories(
            blockId = 12,
            action = "stories",
            platform = "android"
        )
    }

    //Слайдер популярных разделов на главной странице
    suspend fun fetchPopularSlider(): ResponseBody {
        return api.fetchPopulars(action = "popylrazdel")
    }

    //Слайдер баннеров категорий на главной странице
    suspend fun fetchCategoryBannersSlider(): ResponseBody {
        return api.fetchCategoryBanners(
            action = "slayder",
            androidVersion = BuildConfig.VERSION_NAME
        )
    }

    //Слайдер самых выгодных продуктов на главной странице
    suspend fun fetchDiscountsSlider(): ResponseBody {
        return api.fetchNovelties(action = "specpredlosh")
    }

    //Верхний слайдер на главной странице
    suspend fun fetchTopSlider(): ResponseBody {
        return api.fetchDoubleSlider(
            action = "topglav",
            arg = "new"
        )
    }

    //Информация о слайдере заказов на главной странице
    suspend fun fetchOrdersSlider(userId: Long): ResponseBody {
        return api.fetchOrderSlider(userId)
    }

    //Слайдер новинок на главной странице
    suspend fun fetchNoveltiesSlider(): ResponseBody {
        return api.fetchNovelties(
            action = "novinki"
        )
    }

    //Слайдер акций на главной странице
    suspend fun fetchPromotionsSlider(): ResponseBody {
        return api.fetchPromotions(
            action = "akcii",
            limit = 10,
            platform = "android"
        )
    }

    //Нижний слйдер на главнйо странице
    suspend fun fetchBottomSlider(): ResponseBody {
        return api.fetchDoubleSlider(
            action = "topglav",
            arg = "new"
        )
    }

    //Слайдер брендов на главной странице
    suspend fun fetchBrandsSlider(): ResponseBody {
        return api.fetchBrands(
            action = "brand",
            limit = 10
        )
    }

    //Слайдер стран на главной странице
    suspend fun fetchCountriesSlider(): ResponseBody {
        return api.fetchCountries(
            action = "glav"
        )
    }

    //Слайдер ранее просмотренных продуктов
    suspend fun fetchViewedProductsSlider(userId: Long?): ResponseBody {
        return api.fetchViewedProducts(
            action = "viewed",
            userId = userId
        )
    }

    //Слайдер комментариев на главной странице
    suspend fun fetchCommentsSlider(): ResponseBody {
        return api.fetchComments(
            action = "otzivy",
            limit = 10
        )
    }
}