package com.vodovoz.app.data

import androidx.core.net.toUri
import com.vodovoz.app.BuildConfig
import com.vodovoz.app.common.product.rating.RatingResponse
import com.vodovoz.app.core.network.ApiConfig
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.UserAuthDataEntity
import com.vodovoz.app.data.parser.response.banner.AdvertisingBannersSliderResponseJsonParser.parseAdvertisingBannersSliderResponse
import com.vodovoz.app.data.parser.response.banner.CategoryBannersSliderResponseJsonParser.parseCategoryBannersSliderResponse
import com.vodovoz.app.data.parser.response.banner.ProductsByBannerResponseJsonParser.parseProductsByBannerResponse
import com.vodovoz.app.data.parser.response.brand.AllBrandsResponseJsonParser.parseAllBrandsResponse
import com.vodovoz.app.data.parser.response.brand.BrandHeaderResponseJsonParser.parseBrandHeaderResponse
import com.vodovoz.app.data.parser.response.brand.BrandsSliderResponseJsonParser.parseBrandsSliderResponse
import com.vodovoz.app.data.parser.response.cart.BottlesResponseJsonParser.parseBottlesResponse
import com.vodovoz.app.data.parser.response.cart.CartResponseJsonParser.parseCartResponse
import com.vodovoz.app.data.parser.response.cart.ClearCartResponseJsonParser.parseClearCartResponse
import com.vodovoz.app.data.parser.response.catalog.CatalogResponseJsonParser.parseCatalogResponse
import com.vodovoz.app.data.parser.response.category.AllFiltersByCategoryResponseJsonParser.parseAllFiltersByCategoryResponse
import com.vodovoz.app.data.parser.response.category.CategoryHeaderResponseJsonParser.parseCategoryHeaderResponse
import com.vodovoz.app.data.parser.response.category.ConcreteFilterResponseJsonParser.parseConcreteFilterResponse
import com.vodovoz.app.data.parser.response.comment.AllCommentsByProductResponseJsonParser.parseAllCommentsByProductResponse
import com.vodovoz.app.data.parser.response.comment.CommentsSliderResponseJsonParser.parseCommentsSliderResponse
import com.vodovoz.app.data.parser.response.comment.SendCommentAboutProductResponseJsonParser.parseSendCommentAboutProductResponse
import com.vodovoz.app.data.parser.response.comment.SendCommentAboutShopResponseJsonParser.parseSendCommentAboutShopResponse
import com.vodovoz.app.data.parser.response.contacts.ContactsBundleResponseJsonParser.parseContactsBundleResponse
import com.vodovoz.app.data.parser.response.contacts.SendMailResponseJsonParser.parseSendMailResponse
import com.vodovoz.app.data.parser.response.country.CountryHeaderResponseJsonParser.parseCountryHeaderResponse
import com.vodovoz.app.data.parser.response.country.CountrySliderResponseJsonParser.parseCountriesSliderResponse
import com.vodovoz.app.data.parser.response.discount.DiscountHeaderResponseJsonParser.parseDiscountHeaderResponse
import com.vodovoz.app.data.parser.response.discount.DiscountSliderResponseParser.parseDiscountSliderResponse
import com.vodovoz.app.data.parser.response.discount_card.ActivateDiscountCardInfoJsonParser.parseActivateDiscountCardInfoResponse
import com.vodovoz.app.data.parser.response.discount_card.ActivateDiscountCardJsonParser.parseActivateDiscountCardResponse
import com.vodovoz.app.data.parser.response.doubleSlider.DoubleSliderResponseJsonParser.parseBottomSliderResponse
import com.vodovoz.app.data.parser.response.doubleSlider.DoubleSliderResponseJsonParser.parseTopSliderResponse
import com.vodovoz.app.data.parser.response.doubleSlider.SliderHeaderResponseJsonParser.parseSliderHeaderResponse
import com.vodovoz.app.data.parser.response.favorite.FavoriteHeaderResponseJsonParser.parseFavoriteProductsHeaderBundleResponse
import com.vodovoz.app.data.parser.response.history.HistoriesSliderResponseJsonParser.parseHistoriesSliderResponse
import com.vodovoz.app.data.parser.response.map.AddAddressResponseJsonParser.parseAddAddressResponse
import com.vodovoz.app.data.parser.response.map.AddressByGeocodeResponseJsonParser.parseAddressByGeocodeResponse
import com.vodovoz.app.data.parser.response.map.DeleteAddressResponseJsonParser.parseDeleteAddressResponse
import com.vodovoz.app.data.parser.response.map.DeliveryZonesBundleResponseJsonParser.parseDeliveryZonesBundleResponse
import com.vodovoz.app.data.parser.response.map.FetchAddressesSavedResponseJsonParser.parseFetchAddressesSavedResponse
import com.vodovoz.app.data.parser.response.map.UpdateAddressResponseJsonParser.parseUpdateAddressResponse
import com.vodovoz.app.data.parser.response.novelties.NoveltiesHeaderResponseJsonParser.parseNoveltiesHeaderResponse
import com.vodovoz.app.data.parser.response.novelties.NoveltiesSliderResponseParser.parseNoveltiesSliderResponse
import com.vodovoz.app.data.parser.response.order.AllOrdersResponseJsonParser.parseAllOrdersSliderResponse
import com.vodovoz.app.data.parser.response.order.CancelOrderResponseJsonParser.parseCancelOrderResponse
import com.vodovoz.app.data.parser.response.order.OrderDetailsResponseJsonParser.parseOrderDetailsResponse
import com.vodovoz.app.data.parser.response.order.OrderSliderResponseJsonParser.parseOrderSliderResponse
import com.vodovoz.app.data.parser.response.order.RepeatOrderResponseJsonParser.parseRepeatOrderResponse
import com.vodovoz.app.data.parser.response.ordering.RegOrderResponseJsonParser.parseRegOrderResponse
import com.vodovoz.app.data.parser.response.paginatedProducts.FavoriteProductsResponseJsonParser.parseFavoriteProductsResponse
import com.vodovoz.app.data.parser.response.paginatedProducts.MaybeLikeProductsResponseJsonParser.parseMaybeLikeProductsResponse
import com.vodovoz.app.data.parser.response.paginatedProducts.ProductsByBrandResponseJsonParser.parseProductsByBrandResponse
import com.vodovoz.app.data.parser.response.paginatedProducts.ProductsByCategoryResponseJsonParser.parseProductsByCategoryResponse
import com.vodovoz.app.data.parser.response.paginatedProducts.ProductsByCountryResponseJsonParser.parseProductsByCountryResponse
import com.vodovoz.app.data.parser.response.paginatedProducts.ProductsByQueryResponseJsonParser.parseProductsByQueryResponse
import com.vodovoz.app.data.parser.response.paginatedProducts.ProductsBySliderResponseJsonParser.parseProductsBySliderResponse
import com.vodovoz.app.data.parser.response.paginatedProducts.ProductsDiscountResponseJsonParser.parseProductsDiscountResponse
import com.vodovoz.app.data.parser.response.paginatedProducts.ProductsNoveltiesResponseJsonParser.parseProductsNoveltiesResponse
import com.vodovoz.app.data.parser.response.paginatedProducts.SomeProductsByBrandResponseJsonParser.parseSomeProductsByBrandResponse
import com.vodovoz.app.data.parser.response.past_purchases.PastPurchasesHeaderResponseJsonParser.parsePastPurchasesHeaderResponse
import com.vodovoz.app.data.parser.response.popular.PopularSliderResponseJsonParser.parsePopularSliderResponse
import com.vodovoz.app.data.parser.response.popupNews.PopupNewsResponseJsonParser.parsePopupNewsResponse
import com.vodovoz.app.data.parser.response.pre_order.PreOrderFormDataResponseJsonParser.parsePreOrderFormDataResponse
import com.vodovoz.app.data.parser.response.pre_order.PreOrderProductResponseJsonParser.parsePreOrderProductResponse
import com.vodovoz.app.data.parser.response.product.ProductDetailsResponseJsonParser.parseProductDetailsResponse
import com.vodovoz.app.data.parser.response.promotion.AllPromotionsResponseJsonParser.parseAllPromotionsResponse
import com.vodovoz.app.data.parser.response.promotion.PromotionDetailResponseJsonParser.parsePromotionDetailResponseBundle
import com.vodovoz.app.data.parser.response.promotion.PromotionSliderResponseJsonParser.parsePromotionSliderResponse
import com.vodovoz.app.data.parser.response.promotion.PromotionsByBannerResponseJsonParser.parsePromotionsByBannerResponse
import com.vodovoz.app.data.parser.response.questionnaires.QuestionnaireResponseJsonParser.parseQuestionnaireResponse
import com.vodovoz.app.data.parser.response.search.DefaultSearchDataResponseJsonParser.parseDefaultSearchDataResponse
import com.vodovoz.app.data.parser.response.search.MatchesQueriesResponseJsonParser.parseMatchesQueriesResponse
import com.vodovoz.app.data.parser.response.search.ProductsByQueryHeaderResponseJsonParser.parseProductsByQueryHeaderResponse
import com.vodovoz.app.data.parser.response.service.OrderServiceResponseJsonParser.parseOrderServiceResponse
import com.vodovoz.app.data.parser.response.service.ServiceOrderFormResponseJsonParser.parseServiceOrderFormResponse
import com.vodovoz.app.data.parser.response.shipping.FreeShippingDaysResponseJsonParser.parseFreeShippingDaysResponse
import com.vodovoz.app.data.parser.response.shipping.ShippingInfoResponseJsonParser.parseShippingInfoResponse
import com.vodovoz.app.data.parser.response.user.AuthByPhoneJsonParser.parseAuthByPhoneResponse
import com.vodovoz.app.data.parser.response.user.LoginResponseJsonParser.parseLoginResponse
import com.vodovoz.app.data.parser.response.user.PersonalProductsJsonParser.parsePersonalProductsResponse
import com.vodovoz.app.data.parser.response.user.RecoverPasswordJsonParser.parseRecoverPasswordResponse
import com.vodovoz.app.data.parser.response.user.RegisterResponseJsonParser.parseRegisterResponse
import com.vodovoz.app.data.parser.response.user.RequestCodeResponseJsonParser.parseRequestCodeResponse
import com.vodovoz.app.data.parser.response.user.UpdateUserDataResponseJsonParser.parseUpdateUserDataResponse
import com.vodovoz.app.data.parser.response.user.UserDataResponseJsonParser.parseUserDataResponse
import com.vodovoz.app.data.parser.response.viewed.ViewedProductSliderResponseJsonParser.parseViewedProductsSliderResponse
import com.vodovoz.app.feature.bottom.services.detail.model.ServicesDetailParser.parseServiceDetail
import com.vodovoz.app.feature.map.api.MapKitFlowApi
import com.vodovoz.app.feature.profile.viewholders.models.*
import kotlinx.coroutines.coroutineScope
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Query
import java.io.File
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val api: MainApi,
    private val mapKitApi: MapKitFlowApi,
) {

    //Слайдер рекламных баннеров на главной странице
    suspend fun fetchAdvertisingBannersSlider() =
        api.fetchMainSliderResponse(action = "slayder", baner = null)
            .parseAdvertisingBannersSliderResponse()

    //Слайдер историй на главной странице

    suspend fun fetchHistoriesSlider() =
        api.fetchHistories(blockId = 12, action = "stories", platform = "android")
            .parseHistoriesSliderResponse()

    //Слайдер популярных разделов на главной странице
    suspend fun fetchPopularSlider() =
        api.fetchPopulars(action = "popylrazdel").parsePopularSliderResponse()

    //Слайдер баннеров категорий на главной странице
    suspend fun fetchCategoryBannersSlider() =
        api.fetchCategoryBanners(
            action = "slayder",
            androidVersion = "1.4.84"/*BuildConfig.VERSION_NAME*/
        ).parseCategoryBannersSliderResponse()

    //Слайдер самых выгодных продуктов на главной странице
    suspend fun fetchDiscountsSlider() =
        api.fetchNovelties(action = "specpredlosh").parseDiscountSliderResponse()

    //Верхний слайдер на главной странице
    suspend fun fetchTopSlider() =
        api.fetchDoubleSlider(action = "topglav", arg = "new").parseTopSliderResponse()

    //Информация о слайдере заказов на главной странице
    suspend fun fetchOrdersSliderProfile(userId: Long) =
        api.fetchOrderSlider(userId).parseOrderSliderResponse()

//    suspend fun fetchOrdersSlider(userId: Long) =
//        api.fetchOrderSlider(userId).parseOrderSliderResponse()

    //Слайдер новинок на главной странице
    suspend fun fetchNoveltiesSlider() =
        api.fetchNovelties(action = "novinki").parseNoveltiesSliderResponse()

    //Слайдер акций на главной странице
    suspend fun fetchPromotionsSlider() =
        api.fetchPromotionResponse(action = "akcii", limit = 10, platform = "android")
            .parsePromotionSliderResponse()

    //Нижний слайдер на главной странице
    suspend fun fetchBottomSlider() =
        api.fetchDoubleSlider(action = "topglav", arg = "new").parseBottomSliderResponse()

    //Слайдер брендов на главной странице
    suspend fun fetchBrandsSlider() =
        api.fetchBrandResponse(action = "brand", limit = 10).parseBrandsSliderResponse()

    //Слайдер стран на главной странице
    suspend fun fetchCountriesSlider() =
        api.fetchCountryResponse(action = "glav").parseCountriesSliderResponse()

    //Слайдер ранее просмотренных продуктов

//    suspend fun fetchViewedProductsSlider(userId: Long?): ResponseBody {
//        return api.fetchViewedProducts(
//            action = "viewed",
//            userId = userId
//        )
//    }

    suspend fun fetchViewedProductsSlider(
        userId: Long,
    ) = api.fetchViewedProducts(action = "viewed", userId = userId)
        .parseViewedProductsSliderResponse()

    //Слайдер комментариев на главной странице
    suspend fun fetchCommentsSlider() =
        api.fetchComments(action = "otzivy", limit = 10).parseCommentsSliderResponse()

    suspend fun sendCommentAboutShop(
        userId: Long?,
        comment: String?,
        rating: Int?,
    ) = api.fetchComments(
        action = "add",
        userId = userId,
        comment = comment,
        rating = rating
    ).parseSendCommentAboutShopResponse()

    //Добавление продукта в корзину
    suspend fun addProductToCart(productId: Long, quantity: Int): ResponseBody {
        return api.fetchAddProductResponse(
            action = "add",
            productId = productId,
            quantity = quantity
        )
    }

    //Изменение количества товаров в корзине
    suspend fun changeProductsQuantityInCart(productId: Long, quantity: Int): ResponseBody {
        return api.fetchChangeProductsQuantityResponse(
            action = "guaty",
            productId = productId,
            quantity = quantity
        )
    }

    //Добавить в избранное для авторизованного пользователя
//    suspend fun addToFavorite(
//        productIdList: List<Long>,
//        userId: Long,
//    ): ResponseBody {
//        return api.fetchChangeFavoriteResponse(
//            blockId = 12,
//            action = "add",
//            productIdList = StringBuilder().apply {
//                productIdList.forEach { productId ->
//                    append(productId).append(",")
//                }
//            }.toString(),
//            userId = userId
//        )
//    }

    //Удалить из избранного для авторизованного пользователя
//    suspend fun removeFromFavorite(
//        productId: Long,
//        userId: Long,
//    ): ResponseBody {
//        return api.fetchChangeFavoriteResponse(
//            blockId = 12,
//            action = "del",
//            productIdList = productId.toString(),
//            userId = userId
//        )
//    }

    //Основная информация об избранных продуктах
    suspend fun fetchFavoriteProducts(
        userId: Long?,
        productIdListStr: String?,
    ) = api.fetchFavoriteResponse(
        userId = userId,
        productIdList = productIdListStr,
        action = "nalichie"
    ).parseFavoriteProductsHeaderBundleResponse()

    //Основная информация об избранных продуктах
    suspend fun fetchFavoriteProductsSorted(
        userId: Long?,
        productIdListStr: String?,
        categoryId: Long?,
        sort: String?,
        orientation: String?,
        page: Int?,
        isAvailable: Boolean?,
    ) = api.fetchFavoriteResponse(
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
    ).parseFavoriteProductsResponse()

    //Каталог
    suspend fun fetchCatalogResponse() = api.fetchCatalogResponse().parseCatalogResponse()

    /**
     * Корзина
     * **/

    //Корзина
    suspend fun fetchCartResponse(
        action: String? = null,
        userId: Long? = null,
        coupon: String? = null,
        amount: Int? = null,
        appVersion: String? = null,
    ) = api.fetchCartResponse(
        action = action,
        userId = userId,
        coupon = coupon,
        amount = amount,
        appVersion = appVersion
    ).parseCartResponse()

//Добавление в корзину addProductToCart
//Изменение количества товаров в корзине changeProductsQuantityInCart

    //удаление из корзины
//    suspend fun fetchDeleteProductResponse(
//        action: String? = null,
//        productId: Long? = null,
//    ): ResponseBody {
//        return api.fetchDeleteProductResponse(action = action, productId = productId)
//    }

    //Очистить корзину
    suspend fun fetchClearCartResponse(
        action: String? = null,
    ) = api.fetchClearCartResponse(action).parseClearCartResponse()

    /**
     * Продукт
     */

    //Продукт
    suspend fun fetchProductResponse(
        blockId: Int = 1,
        productId: Long,
    ) = api.fetchProductResponse(blockId, productId).parseProductDetailsResponse()

    //Продукты выбранного бренда
    suspend fun fetchProductsByBrandResponse(
        blockId: Int = 12,
        productId: Long,
        brandId: Long,
        page: Int,
    ) = api.fetchProductsByBrandResponse(blockId, productId, brandId, page)
        .parseSomeProductsByBrandResponse()

    //Продукты могут понравиться
    suspend fun fetchMaybeLikeProductsResponse(
        page: Int,
    ) = api.fetchNovelties(action = "details", page = page).parseMaybeLikeProductsResponse()

    //Главная информация о категории
    suspend fun fetchCategoryHeader(
        categoryId: Long,
    ) = api.fetchCategoryResponse(
        blockId = 1,
        categoryId = categoryId
    ).parseCategoryHeaderResponse()

    //Постраничная загрузка продуктов для выбранной категории
    suspend fun fetchProductsByCategory(
        categoryId: Long,
        sort: String,
        orientation: String,
        filter: String,
        filterValue: String,
        priceFrom: Int,
        priceTo: Int,
        page: Int?,
        filterMap: HashMap<String, String>,
    ) = api.fetchCategoryResponse(
        blockId = 1,
        categoryId = categoryId,
        page = page,
        sort = sort,
        orientation = orientation,
        filter = filter,
        filterValue = filterValue,
        priceFrom = priceFrom,
        priceTo = priceTo,
        filterMap = filterMap
    ).parseProductsByCategoryResponse()

    /**
     * Поиск
     */

    suspend fun fetchSearchDefaultData() =
        api.fetchSearchResponse(action = "glav").parseDefaultSearchDataResponse()

    suspend fun fetchProductsByQueryHeader(query: String) = api.fetchSearchResponse(
        action = "search",
        query = query,
        limit = 10,
        page = 1
    ).parseProductsByQueryHeaderResponse()

    suspend fun fetchProductsByQuery(
        query: String,
        categoryId: Long?,
        sort: String,
        orientation: String,
        page: Int?,
    ) = api.fetchSearchResponse(
        action = "search",
        limit = 10,
        query = query,
        categoryId = categoryId,
        sort = sort,
        orientation = orientation,
        page = page
    ).parseProductsByQueryResponse()

    suspend fun fetchMatchesQueries(query: String) = api.fetchMatchesQueries(
        action = "glav",
        query = query
    ).parseMatchesQueriesResponse()

    /**
     * Продукты без фильтра
     */

    suspend fun fetchBrandHeader(
        brandId: Long,
    ) = api.fetchBrandResponse(
        action = "detail",
        brandId = brandId.toString()
    ).parseBrandHeaderResponse()

    suspend fun fetchProductsByBrand(
        brandId: Long?,
        code: String?,
        categoryId: Long?,
        sort: String?,
        orientation: String?,
        page: Int?,
    ) = api.fetchBrandResponse(
        action = "detail",
        brandId = brandId.toString(),
        code = code,
        categoryId = categoryId,
        orientation = orientation,
        sort = sort,
        page = page
    ).parseProductsByBrandResponse()

    suspend fun fetchCountryHeader(
        countryId: Long,
    ) = api.fetchCountryResponse(
        action = "details",
        countryId = countryId
    ).parseCountryHeaderResponse()

    suspend fun fetchProductsByCountry(
        countryId: Long,
        sort: String?,
        orientation: String?,
        categoryId: Long?,
        page: Int?,
    ) = api.fetchCountryResponse(
        action = "details",
        countryId = countryId,
        sort = sort,
        orientation = orientation,
        page = page,
        categoryId = categoryId
    ).parseProductsByCountryResponse()

    suspend fun fetchDiscountHeader() = api.fetchNovelties(
        action = "specpredlosh",
        page = 1
    ).parseDiscountHeaderResponse()

    suspend fun fetchProductsByDiscount(
        categoryId: Long?,
        sort: String?,
        orientation: String?,
        page: Int?,
    ) = api.fetchNovelties(
        action = "specpredlosh",
        categoryId = categoryId,
        sort = sort,
        orientation = orientation,
        page = page
    ).parseProductsDiscountResponse()

    suspend fun fetchNoveltiesHeader() = api.fetchNovelties(
        action = "novinki",
        page = 1
    ).parseNoveltiesHeaderResponse()

    suspend fun fetchProductsByNovelties(
        categoryId: Long?,
        sort: String?,
        orientation: String?,
        page: Int?,
    ) = api.fetchNovelties(
        action = "novinki",
        categoryId = categoryId,
        sort = sort,
        orientation = orientation,
        page = page
    ).parseProductsNoveltiesResponse()

    suspend fun fetchDoubleSliderHeader(
        categoryId: Long,
    ) = api.fetchDoubleSlider(
        action = "details",
        androidVersion = BuildConfig.VERSION_NAME,
        categoryId = categoryId
    ).parseSliderHeaderResponse()

    suspend fun fetchProductsByDoubleSlider(
        categoryId: Long?,
        page: Int?,
        sort: String?,
        orientation: String?,
    ) = api.fetchDoubleSlider(
        action = "details",
        androidVersion = BuildConfig.VERSION_NAME,
        categoryId = categoryId,
        sort = sort,
        orientation = orientation,
        page = page,
    ).parseProductsBySliderResponse()

    /**
     * only products
     */

    //Продукты  по баннеру
    suspend fun fetchProductsByBanner(categoryId: Long) = api.fetchMainSliderResponse(
        action = "detailtovar",
        categoryId = categoryId
    ).parseProductsByBannerResponse()

    /**
     * promo details
     */

    suspend fun fetchPromotionDetails(promotionId: Long) =
        api.fetchPromotionResponse(action = "detail", promotionId = promotionId)
            .parsePromotionDetailResponseBundle()

    /**
     * all promotions
     */

    //Информация о всех акциях
    suspend fun fetchAllPromotions(
        filterId: Long,
    ) = api.fetchPromotionResponse(
        action = "akcii",
        filterId = filterId
    ).parseAllPromotionsResponse()

    //Акции по баннеру
    suspend fun fetchPromotionsByBanner(categoryId: Long) =
        api.fetchMainSliderResponse(
            action = "detailaction",
            categoryId = categoryId
        ).parsePromotionsByBannerResponse()

    //Все бренды
    suspend fun fetchAllBrands(
        brandIdList: List<Long>,
    ) =
        api.fetchBrandResponse(
            action = "brand",
            brandIdList = StringBuilder().apply {
                brandIdList.forEach { brandId ->
                    append(brandId)
                    append(",")
                }
            }.toString()
        ).parseAllBrandsResponse()

    //Всплывающая новость
    suspend fun fetchPopupNews(userId: Long?) = api.fetchNewsResponse(
        action = "okno",
        userId = userId,
        platform = "android"
    ).parsePopupNewsResponse()


    /**
     * pre order
     */

    suspend fun fetchPreOrderFormData(
        userId: Long?,
    ) = api.fetchPreOrderResponse(
        action = "predzakaz",
        userId = userId
    ).parsePreOrderFormDataResponse()

    suspend fun preOrderProduct(
        userId: Long?,
        productId: Long?,
        name: String?,
        email: String?,
        phone: String?,
    ) = api.fetchPreOrderResponse(
        action = "otpravka",
        userId = userId,
        productId = productId,
        email = email,
        phone = phone,
        name = name
    ).parsePreOrderProductResponse()

    /**
     * rate product
     */

    suspend fun rateProduct(
        productId: Long,
        ratingValue: Float,
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
        page: Int?,
    ) = api.fetchCommentsResponse(
        action = "detail",
        productId = productId,
        page = page
    ).parseAllCommentsByProductResponse()

    /**
     * send comment by product
     */

    //Отправить отзыв о продукте
    suspend fun sendCommentAboutProduct(
        productId: Long,
        rating: Int,
        comment: String,
        userId: Long,
    ) = api.fetchCommentsResponse(
        blockId = 12,
        action = "add",
        productId = productId,
        rating = rating,
        comment = comment,
        userId = userId
    ).parseSendCommentAboutProductResponse()

    suspend fun dontCommentProduct(
        productId: Long,
        userId: Long,
    ) = api.fetchRateBottomData(
        action = "addblock",
        productId = productId,
        userId = userId
    )

    /**
     * past purchases
     */

    suspend fun fetchPastPurchasesHeader(
        userId: Long?,
    ) = api.fetchPastPurchasesResponse(
        action = "getLastFifty",
        userId = userId,
        page = 1
    ).parsePastPurchasesHeaderResponse()

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
        isAvailable = when (isAvailable) {
            true -> "nalichie"
            false -> "netnalichi"
            else -> null
        }
    ).parseFavoriteProductsResponse()

    /**
     * all orders
     */

    //Постраничная загрузка всех заказов
    suspend fun fetchAllOrders(
        userId: Long?,
        appVersion: String?,
        orderId: Long?,
        status: String?,
        page: Int?,
    ) = api.fetchOrdersHistoryResponse(
        userId = userId,
        appVersion = appVersion,
        action = "spisok",
        orderId = orderId,
        status = status,
        page = page
    ).parseAllOrdersSliderResponse()

    /**
     * repeat order
     */

    //Детальная информация о заказе
    suspend fun fetchOrderDetailsResponse(
        userId: Long?,
        appVersion: String?,
        orderId: Long?,
    ) = api.fetchOrdersResponse(
        action = "detail",
        userId = userId,
        appVersion = appVersion,
        orderId = orderId
    ).parseOrderDetailsResponse()

    suspend fun repeatOrder(
        orderId: Long?,
        userId: Long?,
    ) = api.repeatOrder(
        orderId = orderId,
        userId = userId
    ).parseRepeatOrderResponse()


    //Отмена заказа
    suspend fun cancelOrder(
        orderId: Long?,
    ) = api.cancelOrder(
        orderId = orderId
    ).parseCancelOrderResponse()

    /**
     * products filters
     */
    //Все фильтры по продуктам для выбранной категории
    suspend fun fetchAllFiltersByCategory(
        categoryId: Long,
    ) = api.fetchFilterBundleResponse(categoryId = categoryId).parseAllFiltersByCategoryResponse()

    suspend fun fetchProductFilterById(
        categoryId: Long,
        filterCode: String,
    ) = api.fetchFilterResponse(categoryId = categoryId, filterCode = filterCode)
        .parseConcreteFilterResponse()


    /**
     * map
     */

    //Информация о зонах доставки
    suspend fun fetchDeliveryZonesResponse() = api.fetchMapResponse(
        action = "tochkakarta"
    ).parseDeliveryZonesBundleResponse()

    //Адрес по координатам
    suspend fun fetchAddressByGeocodeResponse(
        latitude: Double,
        longitude: Double,
    ) = mapKitApi.fetchAddressByGeocodeResponse(
        apiKey = "346ef353-b4b2-44b3-b597-210d62eeb66b",
        geocode = "$longitude,$latitude",
        format = "json"
    ).parseAddressByGeocodeResponse()

    /**
     * profile
     */

    //Информация о пользователе

    suspend fun fetchUserData(userId: Long) = api.fetchProfileResponse(
        action = "details",
        userId = userId
    ).parseUserDataResponse()

    //Информация о пользователе
    suspend fun fetchProfileCategories(
        userId: Long,
        isTablet: Boolean,
    ) = api.fetchProfileCategoriesResponse(
        action = "glav",
        userId = userId,
        appVersion = BuildConfig.VERSION_NAME,
        isTablet = if (isTablet) "ipad" else "phone"
    )

    suspend fun fetchPersonalProducts(
        userId: Long,
        page: Int?,
    ) = api.fetchPersonalProducts(
        action = "tovarchik",
        userId = userId,
        page = page
    ).parsePersonalProductsResponse()

    suspend fun fetchQuestionnairesResponse(
        @Query("action") action: String? = null,
        @Query("userid") userId: Long? = null,
    ) = api.fetchQuestionnairesResponse(
        action = action,
        userId = userId
    ).parseQuestionnaireResponse()

    suspend fun fetchFormForOrderService(
        type: String?,
        userId: Long?,
    ) = api.fetchOrderServiceResponse(
        action = "glav",
        type = type,
        userId = userId
    ).parseServiceOrderFormResponse()

    suspend fun orderService(
        type: String?,
        userId: Long?,
        value: String?,
    ) = api.fetchOrderServiceResponse(
        action = "add",
        type = type,
        userId = userId,
        value = value
    ).parseOrderServiceResponse()

    /**
     * auth
     */

    //Авторизация
    suspend fun authByEmail(
        email: String,
        password: String,
    ) = api.fetchLoginResponse(
        email = email,
        password = password
    ).parseLoginResponse()

    suspend fun authByPhone(
        phone: String?,
        url: String,
        code: String?,
    ): ResponseEntity<UserAuthDataEntity> {

        val uri = ApiConfig.VODOVOZ_URL
            .toUri()
            .buildUpon()
            .encodedPath(url)
            .appendQueryParameter("action", "tochkakarta")
            .appendQueryParameter("telefon", phone)
            .appendQueryParameter("code", code)
            .build()
            .toString()

        return api.fetchAuthByPhoneResponse(uri).parseAuthByPhoneResponse()
    }

    suspend fun relogin(
        userId: Long,
        token: String,
    ) = api.fetchReloginResponse(
        userId = userId,
        token = token
    )

    suspend fun requestCode(
        phone: String?,
        url: String,
    ): ResponseEntity<Int> {

        val uri = ApiConfig.VODOVOZ_URL
            .toUri()
            .buildUpon()
            .encodedPath(url)
            .appendQueryParameter("action", "tochkakarta")
            .appendQueryParameter("telefon", phone)
            .build()
            .toString()

        return api.fetchAuthByPhoneResponse(uri).parseRequestCodeResponse()
    }

    /**
     * password recovery
     */

    suspend fun recoverPassword(
        email: String?,
    ) = api.recoverPassword(
        forgotPassword = "yes",
        email = email
    ).parseRecoverPasswordResponse()

    /**
     * site state
     */
    suspend fun fetchSiteState() = coroutineScope {
        api.fetchSiteState(action = "saitosnova")
    }

    /**
     * registration
     */

    //Регистрация нового пользователя
    suspend fun register(
        firstName: String,
        secondName: String,
        email: String,
        password: String,
        phone: String,
    ) = api.fetchRegisterResponse(
        firstName = firstName,
        secondName = secondName,
        email = email,
        password = password,
        phone = phone
    ).parseRegisterResponse()

    //Firebase Token
    suspend fun sendFirebaseToken(
        action: String = "token",
        userId: Long? = null,
        token: String,
    ) = coroutineScope {
        api.sendFirebaseToken(
            action = action, userId = userId, token = token
        )
    }

    /**
     * Services
     */

    //Общая информация о предоставляемых услугах
    suspend fun fetchAboutServices(action: String) = api.fetchServicesResponse(
        action = action
    )

    //Общая информация о предоставляемых услугах
    suspend fun fetchAboutServicesNew(action: String) = api.fetchServicesNewResponse(
        action = action
    )

    //Детальная информация о предоставляемых услугах
    suspend fun fetchServicesNewDetails(action: String, id: String) =
        api.fetchServicesNewDetailsResponse(
            action = action,
            id = id
        ).parseServiceDetail()

//    suspend fun fetchTestMapResponse(
//        address: String,
//        latitude: String,
//        longitude: String,
//        length: String,
//        date: String,
//    ): ResponseBody {
//        return api.sendTestMapRequest(
//            sourse = "API",
//            address = address,
//            latitude = latitude,
//            longitude = longitude,
//            length = length,
//            date = date
//        )
//    }

    /**
     * Contacts
     */

    suspend fun fetchContacts(appVersion: String) = api.fetchContactsResponse(
        action = "dannyesvyazi",
        appVersion = appVersion
    ).parseContactsBundleResponse()

    /**
     * Send Mail
     */

    suspend fun sendMail(
        name: String?,
        phone: String?,
        email: String?,
        comment: String?,
    ) = api.sendMail(
        name = name,
        phone = phone,
        email = email,
        comment = comment
    ).parseSendMailResponse()

    /**
     * Addresses
     */

    //Получить сохраненные адреса
    suspend fun fetchAddressesSaved(
        userId: Long?,
        type: Int?,
    ) = api.fetchAddressResponse(
        blockId = 102,
        action = "get",
        userid = userId,
        type = type
    ).parseFetchAddressesSavedResponse()

    //Удалить адресс
    suspend fun deleteAddress(
        addressId: Long?,
        userId: Long?,
    ) = api.fetchAddressResponse(
        addressId = addressId,
        userid = userId,
        action = "del",
        blockId = 102
    ).parseDeleteAddressResponse()

    //Добавить адрес в сохраненные
    suspend fun addAddress(
        locality: String?,
        street: String?,
        house: String?,
        entrance: String?,
        floor: String?,
        office: String?,
        comment: String?,
        type: Int?,
        userId: Long?,
        lat: String,
        longitude: String,
        length: String,
        fullAddress: String,
    ) = api.fetchAddressResponse(
        locality = locality,
        street = street,
        house = house,
        entrance = entrance,
        floor = floor,
        office = office,
        comment = comment,
        type = type,
        userid = userId,
        blockId = 102,
        action = "add",
        fullAddress = fullAddress,
        length = length,
        longAndLat = "$lat,$longitude"
    ).parseAddAddressResponse()

    //Обновить адрес
    suspend fun updateAddress(
        locality: String?,
        street: String?,
        house: String?,
        entrance: String?,
        floor: String?,
        office: String?,
        comment: String?,
        type: Int?,
        addressId: Long?,
        userId: Long?,
        lat: String,
        longitude: String,
        length: String,
        fullAddress: String,
    ) = api.fetchAddressResponse(
        locality = locality,
        street = street,
        house = house,
        entrance = entrance,
        floor = floor,
        office = office,
        comment = comment,
        type = type,
        addressId = addressId,
        userid = userId,
        blockId = 102,
        action = "update",
        fullAddress = fullAddress,
        length = length,
        longAndLat = "$lat,$longitude"
    ).parseUpdateAddressResponse()

    /**
     * Ordering
     */

    suspend fun fetchShippingInfo(
        userId: Long?,
        addressId: Long?,
        date: String?,
        appVersion: String?,
    ) = api.fetchInfoAboutOrderingResponse(
        userId = userId,
        addressId = addressId,
        date = date,
        appVersion = appVersion
    ).parseShippingInfoResponse()

    suspend fun fetchFreeShippingDaysInfoResponse(
        appVersion: String?,
    ) = api.fetchInfoAboutOrderingResponse(appVersion = appVersion).parseFreeShippingDaysResponse()

    suspend fun regOrder(
        orderType: Int?, //Тип заказа (1/2)
        device: String?, //Телефон Android:12 Версия: 1.4.83
        addressId: Long?, //адрес id - (150543)
        date: String?, //23.08.2022
        paymentId: Long?, //Pay method Id
        needOperatorCall: String?, // Y/N
        needShippingAlert: String?, //За 90 минут
        shippingAlertPhone: String?,
        comment: String?,
        totalPrice: Int?, //Итоговая сумма заказа
        shippingId: Long?, //
        shippingPrice: Int?, // цена доставки
        name: String?,
        phone: String?,
        email: String?,
        companyName: String?,
        userId: Long?,
        deposit: Int?, //?
        fastShippingPrice: Int?, // 500 р
        extraShippingPrice: Int?, // из delivery
        commonShippingPrice: Int?, //?
        coupon: String?, // передавать из корзины
        shippingIntervalId: Long?, //id Интервал доставки
        overMoney: Int?, //?
        parking: Int?, // числовое значение
        appVersion: String?,
        checkDeliveryValue: Int?,
    ) = api.fetchRegOrderResponse(
        orderType = orderType,
        device = device,
        addressId = addressId,
        date = date,
        paymentId = paymentId,
        needOperatorCall = needOperatorCall,
        needShippingAlert = needShippingAlert,
        shippingAlertPhone = shippingAlertPhone,
        comment = comment,
        totalPrice = totalPrice,
        shippingId = shippingId,
        shippingPrice = shippingPrice,
        name = name,
        phone = phone,
        email = email,
        companyName = companyName,
        userId = userId,
        deposit = deposit,
        fastShippingPrice = fastShippingPrice,
        extraShippingPrice = extraShippingPrice,
        commonShippingPrice = commonShippingPrice,
        coupon = coupon,
        shippingIntervalId = shippingIntervalId,
        overMoney = overMoney,
        parking = parking,
        appVersion = appVersion,
        checkDeliveryValue = checkDeliveryValue
    ).parseRegOrderResponse()

//    fun fetchShippingAlertEntityList() = ShippingAlertConfig.shippingAlertEntityList

    suspend fun updateUserData(
        userId: Long,
        firstName: String?,
        secondName: String?,
        password: String?,
        phone: String?,
        sex: String?,
        birthday: String?,
        email: String?,
    ) = api.fetchProfileResponse(
        action = "edit",
        userId = userId,
        firstName = firstName,
        secondName = secondName,
        password = password,
        phone = phone,
        sex = sex,
        birthday = birthday,
        email = email
    ).parseUpdateUserDataResponse()

    suspend fun logout() = api.logout()

    suspend fun addAvatar(id: Long, image: File): Response<Void> {
        val requestBody = image.asRequestBody(image.extension.toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("userpic", image.name, requestBody)
        return api.addAvatar(id = id, file = filePart)
    }

    suspend fun fetchActivateDiscountCardInfo(
        userId: Long?,
    ) = api.fetchDiscountCardBaseRequest(
        action = "glav",
        userId = userId
    ).parseActivateDiscountCardInfoResponse()

    suspend fun activateDiscountCard(
        userId: Long?,
        value: String?,
    ) = api.fetchDiscountCardBaseRequest(
        action = "edit",
        userId = userId,
        value = value
    ).parseActivateDiscountCardResponse()

    suspend fun fetchRateBottomData(
        userId: Long?,
    ) = api.fetchRateBottomData(
        action = "tovarglav",
        userId = userId
    )

    suspend fun addProductFromServiceDetails(
        idWithGift: String,
    ) = api.fetchAddProductResponse(
        "addtoqua",
        idWithGift = idWithGift
    )

    suspend fun fetchSearchDataByQrCode(searchText: String) = coroutineScope {
        api.fetchSearchDataByQrCode(
            blockId = 12,
            searchText = searchText
        )
    }

    suspend fun fetchNotificationSettingsData(uri: String) = coroutineScope {
        api.fetchNotificationSettingsData(uri)
    }

    suspend fun fetchBottles() = api.fetchBottles().parseBottlesResponse()
}