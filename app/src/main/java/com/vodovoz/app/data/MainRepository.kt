package com.vodovoz.app.data

import com.vodovoz.app.BuildConfig
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.features.FavoriteProductsHeaderBundleEntity
import com.vodovoz.app.data.parser.response.favorite.FavoriteHeaderResponseJsonParser.parseFavoriteProductsHeaderBundleResponse
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.rx3.rxSingle
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
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

    //Добавление продукта в корзину
    suspend fun addProductToCart(productId: Long, quantity: Int): ResponseBody {
        return api.fetchAddProductResponse(
            action = "add",
            productId = productId,
            quantity = quantity
        )
    }

    //Изменение колличества товаров в корзине
    suspend fun changeProductsQuantityInCart(productId: Long, quantity: Int): ResponseBody {
        return api.fetchChangeProductsQuantityResponse(
            action = "guaty",
            productId = productId,
            quantity = quantity
        )
    }

    //Добавить в избранное для авторизованного пользователя
    suspend fun addToFavorite(
        productIdList: List<Long>,
        userId: Long
    ): ResponseBody {
        return api.fetchChangeFavoriteResponse(
            blockId = 12,
            action = "add",
            productIdList = StringBuilder().apply {
                productIdList.forEach { productId ->
                    append(productId).append(",")
                }
            }.toString(),
            userId = userId
        )
    }

    //Удалить из избранного для авторизованного пользователя
    suspend fun removeFromFavorite(
        productId: Long,
        userId: Long
    ): ResponseBody {
        return api.fetchChangeFavoriteResponse(
            blockId = 12,
            action = "del",
            productIdList = productId.toString(),
            userId = userId
        )
    }

    //Основная информация об избранных продуктах
    suspend fun fetchFavoriteProducts(
        userId: Long?,
        productIdListStr: String?
    ): ResponseBody {
        return api.fetchFavoriteResponse(
            userId = userId,
            productIdList = productIdListStr,
            action = "nalichie"
        )
    }

    //Каталог
    suspend fun fetchCatalogResponse(): ResponseBody {
        return api.fetchCatalogResponse()
    }

    /**
     * Корзина
     * **/

    //Корзина
    suspend fun fetchCartResponse(
        action: String? = null,
        userId: Long? = null,
        coupon: String? = null,
        amount: Int? = null
    ): ResponseBody {
        return api.fetchCartResponse(
            action = action,
            userId = userId,
            coupon = coupon,
            amount = amount
        )
    }

    //Добавление в корзину addProductToCart
    //Изменение колличества товаров в корзине changeProductsQuantityInCart

    //удаление из корзины
    suspend fun fetchDeleteProductResponse(
        action: String? = null,
        productId: Long? = null,
    ): ResponseBody {
        return api.fetchDeleteProductResponse(action = action, productId = productId)
    }

    //Очистить корзину
    suspend fun fetchClearCartResponse(
        action: String? = null,
    ): ResponseBody {
        return api.fetchClearCartResponse(action)
    }

    /**
     * Продукт
     */

    //Продукт
    suspend fun fetchProductResponse(
        blockId: Int = 1,
        productId: Long
    ): ResponseBody {
        return api.fetchProductResponse(blockId, productId)
    }

    //Продукты выбранного бренда
    @GET("/newmobile/details/brandtovar.php")
    suspend fun fetchProductsByBrandResponse(
        blockId: Int = 12,
        productId: Long,
        brandId: Long,
        page: Int
    ): ResponseBody {
        return api.fetchProductsByBrandResponse(blockId, productId, brandId, page)
    }

}