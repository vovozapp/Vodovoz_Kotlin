package com.vodovoz.app.data

import com.vodovoz.app.BuildConfig
import com.vodovoz.app.common.product.rating.RatingResponse
import com.vodovoz.app.data.model.common.*
import com.vodovoz.app.data.model.features.AllPromotionsBundleEntity
import com.vodovoz.app.data.model.features.FavoriteProductsHeaderBundleEntity
import com.vodovoz.app.data.model.features.PastPurchasesHeaderBundleEntity
import com.vodovoz.app.data.parser.response.brand.AllBrandsResponseJsonParser.parseAllBrandsResponse
import com.vodovoz.app.data.parser.response.category.CategoryHeaderResponseJsonParser.parseCategoryHeaderResponse
import com.vodovoz.app.data.parser.response.comment.SendCommentAboutProductResponseJsonParser.parseSendCommentAboutProductResponse
import com.vodovoz.app.data.parser.response.favorite.FavoriteHeaderResponseJsonParser.parseFavoriteProductsHeaderBundleResponse
import com.vodovoz.app.data.parser.response.order.CancelOrderResponseJsonParser.parseCancelOrderResponse
import com.vodovoz.app.data.parser.response.order.OrderDetailsResponseJsonParser.parseOrderDetailsResponse
import com.vodovoz.app.data.parser.response.past_purchases.PastPurchasesHeaderResponseJsonParser.parsePastPurchasesHeaderResponse
import com.vodovoz.app.data.parser.response.popupNews.PopupNewsResponseJsonParser.parsePopupNewsResponse
import com.vodovoz.app.data.parser.response.pre_order.PreOrderFormDataResponseJsonParser.parsePreOrderFormDataResponse
import com.vodovoz.app.data.parser.response.pre_order.PreOrderProductResponseJsonParser.parsePreOrderProductResponse
import com.vodovoz.app.data.parser.response.promotion.AllPromotionsResponseJsonParser.parseAllPromotionsResponse
import com.vodovoz.app.data.parser.response.promotion.PromotionDetailResponseJsonParser.parsePromotionDetailResponse
import com.vodovoz.app.data.parser.response.promotion.PromotionsByBannerResponseJsonParser.parsePromotionsByBannerResponse
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

    //Основная информация об избранных продуктах
    suspend fun fetchFavoriteProductsSorted(
        userId: Long?,
        productIdListStr: String,
        categoryId: Long?,
        sort: String?,
        orientation: String?,
        page: Int?,
        isAvailable: Boolean?,
    ): ResponseBody {
        return api.fetchFavoriteResponse(
            userId = userId,
            productIdList = productIdListStr,
            categoryId = categoryId,
            sort = sort,
            orientation = orientation,
            action = when (isAvailable) {
                true -> "nalichie"
                false -> "netnalichi"
                else -> null
            },
            page = page
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
    suspend fun fetchProductsByBrandResponse(
        blockId: Int = 12,
        productId: Long,
        brandId: Long,
        page: Int
    ): ResponseBody {
        return api.fetchProductsByBrandResponse(blockId, productId, brandId, page)
    }

    //Продукты могут понравиться
    suspend fun fetchMaybeLikeProductsResponse(
        page: Int
    ): ResponseBody {
        return api.fetchNovelties(action = "details", page = page)
    }

    //Главная информация о категории
    suspend fun fetchCategoryHeader(
        categoryId: Long
    ): ResponseBody {
        return api.fetchCategoryResponse(
            blockId = 1,
            categoryId = categoryId
        )
    }

    //Постраничная загрузка продуктов для выбранной категории
    suspend fun fetchProductsByCategory(
        categoryId: Long,
        sort: String,
        orientation: String,
        filter: String,
        filterValue: String,
        priceFrom: Int,
        priceTo: Int,
        page: Int?
    ): ResponseBody {
        return api.fetchCategoryResponse(
            blockId = 1,
            categoryId = categoryId,
            page = page,
            sort = sort,
            orientation = orientation,
            filter = filter,
            filterValue = filterValue,
            priceFrom = priceFrom,
            priceTo = priceTo
        )
    }

    /**
     * Поиск
     */

    suspend fun fetchSearchDefaultData() : ResponseBody {
        return api.fetchSearchResponse(action = "glav")
    }

    suspend fun fetchProductsByQueryHeader(query: String) : ResponseBody {
        return api.fetchSearchResponse(
            action = "search",
            query = query,
            limit = 10,
            page = 1
        )
    }

    suspend fun fetchProductsByQuery(
        query: String,
        categoryId: Long?,
        sort: String,
        orientation: String,
        page: Int?
    ) : ResponseBody {
        return api.fetchSearchResponse(
            action = "search",
            limit = 10,
            query = query,
            categoryId = categoryId,
            sort = sort,
            orientation = orientation,
            page = page
        )
    }

    suspend fun fetchMatchesQueries(query: String) : ResponseBody {
        return api.fetchMatchesQueries(
            action = "glav",
            query = query
        )
    }

    /**
     * Продукты без фильтра
     */

    suspend fun fetchBrandHeader(
        brandId: Long
    ) : ResponseBody {
        return api.fetchBrandResponse(
            action = "detail",
            brandId = brandId.toString()
        )
    }

    suspend fun fetchProductsByBrand(
        brandId: Long?,
        code: String?,
        categoryId: Long?,
        sort: String?,
        orientation: String?,
        page: Int?
    ) : ResponseBody {
        return api.fetchBrandResponse(
            action = "detail",
            brandId = brandId.toString(),
            code = code,
            categoryId = categoryId,
            orientation = orientation,
            sort = sort,
            page = page
        )
    }

    suspend fun fetchCountryHeader(
        countryId: Long
    ) : ResponseBody {
        return api.fetchCountryResponse(
            action = "details",
            countryId = countryId
        )
    }

    suspend fun fetchProductsByCountry(
        countryId: Long,
        sort: String?,
        orientation: String?,
        categoryId: Long?,
        page: Int?,
    ) : ResponseBody {
        return api.fetchCountryResponse(
            action = "details",
            countryId = countryId,
            sort = sort,
            orientation = orientation,
            page = page,
            categoryId = categoryId
        )
    }

    suspend fun fetchDiscountHeader() : ResponseBody {
        return api.fetchNoveltiesResponse(
            action = "specpredlosh",
            page = 1
        )
    }

    suspend fun fetchProductsByDiscount(
        categoryId: Long?,
        sort: String?,
        orientation: String?,
        page: Int?
    ) : ResponseBody {
        return api.fetchNoveltiesResponse(
            action = "specpredlosh",
            categoryId = categoryId,
            sort = sort,
            orientation = orientation,
            page = page
        )
    }

    suspend fun fetchNoveltiesHeader() : ResponseBody {
        return api.fetchNoveltiesResponse(
            action = "novinki",
            page = 1
        )
    }

    suspend fun fetchProductsByNovelties(
        categoryId: Long?,
        sort: String?,
        orientation: String?,
        page: Int?
    ) : ResponseBody {
        return api.fetchNoveltiesResponse(
            action = "novinki",
            categoryId = categoryId,
            sort = sort,
            orientation = orientation,
            page = page
        )
    }

    suspend fun fetchDoubleSliderHeader(
        categoryId: Long
    ) : ResponseBody {
        return api.fetchDoubleSlider(
            action = "details",
            androidVersion = BuildConfig.VERSION_NAME,
            categoryId = categoryId
        )
    }

    suspend fun fetchProductsByDoubleSlider(
        categoryId: Long?,
        page: Int?,
        sort: String?,
        orientation: String?
    ) : ResponseBody {
        return api.fetchDoubleSlider(
            action = "details",
            androidVersion = BuildConfig.VERSION_NAME,
            categoryId = categoryId,
            sort = sort,
            orientation = orientation,
            page = page,
        )
    }

    /**
     * only products
     */

    //Продукты по баннеру
    suspend fun fetchProductsByBanner(categoryId: Long): ResponseBody = api.fetchMainSliderResponse(
        action = "detailtovar",
        categoryId = categoryId
    )

    /**
     * promo details
     */

    suspend fun fetchPromotionDetails(promotionId: Long): ResponseBody =
        api.fetchPromotionResponse(action = "detail", promotionId = promotionId)

    /**
     * all promotions
     */

    //Информация о всех акциях
    suspend fun fetchAllPromotions(
        filterId: Long
    ): ResponseBody = api.fetchPromotionResponse(
        action = "akcii",
        filterId = filterId
    )

    //Акции по баннеру
    suspend fun fetchPromotionsByBanner(categoryId: Long): ResponseBody = api.fetchMainSliderResponse(
        action = "detailaction",
        categoryId = categoryId
    )

    //Все бренды
    suspend fun fetchAllBrands(
        brandIdList: List<Long>
    ): ResponseBody =
        api.fetchBrandResponse(
            action = "brand",
            brandIdList = StringBuilder().apply {
                brandIdList.forEach { brandId ->
                    append(brandId)
                    append(",")
                }
            }.toString()
        )

    //Всплывающая новость
    suspend fun fetchPopupNews(userId: Long?): ResponseBody = api.fetchNewsResponse(
        action = "okno",
        userId = userId,
        platform = "android"
    )

    /**
     * pre order
     */

    suspend fun fetchPreOrderFormData(
        userId: Long?
    ): ResponseBody = api.fetchPreOrderResponse(
        action = "predzakaz",
        userId = userId
    )

    suspend fun preOrderProduct(
        userId: Long?,
        productId: Long?,
        name: String?,
        email: String?,
        phone: String?
    ): ResponseBody = api.fetchPreOrderResponse(
        action = "otpravka",
        userId = userId,
        productId = productId,
        email = email,
        phone = phone,
        name = name
    )

    /**
     * rate product
     */

    suspend fun rateProduct(
       productId: Long,
       ratingValue: Float
    ): RatingResponse {
        return api.rateProduct(
            productId = productId,
            ratingValue = ratingValue
        )
    }

    /**
     * all comments by products
     */

    //Все отзывы о продукте
    suspend fun fetchAllCommentsByProduct(
        productId: Long,
        page: Int?
    ) = api.fetchCommentsResponse(
        action = "detail",
        productId = productId,
        page = page
    )

    /**
     * send comment by product
     */

    //Отправить отзыв о продукте
    suspend fun sendCommentAboutProduct(
        productId: Long,
        rating: Int,
        comment: String,
        userId: Long
    ) = api.fetchCommentsResponse(
        blockId = 12,
        action = "add",
        productId = productId,
        rating = rating,
        comment = comment,
        userId = userId
    )

    /**
     * past purchases
     */

    suspend fun fetchPastPurchasesHeader(
        userId: Long?
    ) = api.fetchPastPurchasesResponse(
        action = "getLastFifty",
        userId = userId,
        page = 1
    )

    suspend fun fetchPastPurchasesProducts(
        userId: Long?,
        sort: String?,
        orientation: String?,
        categoryId: Long?,
        isAvailable: Boolean?,
        page: Int?,
    ) = api.fetchPastPurchasesResponse(
        action = "getLastFifty",
        userId = userId,
        sort = sort,
        orientation = orientation,
        categoryId = categoryId,
        page = page,
        isAvailable = when(isAvailable) {
            true -> "nalichie"
            false -> "netnalichi"
            else -> null
        }
    )

    /**
     * all orders
     */

    //Постраничная загрузка всех заказов
    suspend fun fetchAllOrders(
        userId: Long?,
        appVersion: String?,
        orderId: Long?,
        status: String?,
        page: Int?
    ) = api.fetchOrdersHistoryResponse(
        userId = userId,
        appVersion = appVersion,
        action = "spisok",
        orderId = orderId,
        status = status,
        page = page
    )

    /**
     * repeat order
     */

    //Детальная информация о заказе
    suspend fun fetchOrderDetailsResponse(
        userId: Long?,
        appVersion: String?,
        orderId: Long?
    ) = api.fetchOrdersResponse(
        action = "detail",
        userId = userId,
        appVersion = appVersion,
        orderId = orderId
    )

    //Отмена заказа
    suspend fun cancelOrder(
        orderId: Long?
    ) = api.cancelOrder(
        orderId = orderId
    )

}