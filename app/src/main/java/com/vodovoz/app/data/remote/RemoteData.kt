package com.vodovoz.app.data.remote

import android.util.Log
import com.vodovoz.app.BuildConfig
import com.vodovoz.app.data.model.common.*
import com.vodovoz.app.data.model.features.*
import com.vodovoz.app.data.parser.response.banner.AdvertisingBannersSliderResponseJsonParser.parseAdvertisingBannersSliderResponse
import com.vodovoz.app.data.parser.response.banner.CategoryBannersSliderResponseJsonParser.parseCategoryBannersSliderResponse
import com.vodovoz.app.data.parser.response.banner.ProductsByBannerResponseJsonParser.parseProductsByBannerResponse
import com.vodovoz.app.data.parser.response.brand.AllBrandsResponseJsonParser.parseAllBrandsResponse
import com.vodovoz.app.data.parser.response.brand.BrandHeaderResponseJsonParser.parseBrandHeaderResponse
import com.vodovoz.app.data.parser.response.brand.BrandsSliderResponseJsonParser.parseBrandsSliderResponse
import com.vodovoz.app.data.parser.response.cart.AddProductToCartResponseJsonParser.parseAddProductToCartResponse
import com.vodovoz.app.data.parser.response.cart.CartResponseJsonParser.parseCartResponse
import com.vodovoz.app.data.parser.response.cart.ChangeProductQuantityInCartResponseJsonParser.parseChangeProductQuantityInCartResponse
import com.vodovoz.app.data.parser.response.cart.ClearCartResponseJsonParser.parseClearCartResponse
import com.vodovoz.app.data.parser.response.cart.DeleteProductFromCartResponseJsonParser.parseDeleteProductFromCartResponse
import com.vodovoz.app.data.parser.response.catalog.CatalogResponseJsonParser.parseCatalogResponse
import com.vodovoz.app.data.parser.response.category.AllFiltersByCategoryResponseJsonParser.parseAllFiltersByCategoryResponse
import com.vodovoz.app.data.parser.response.category.CategoryHeaderResponseJsonParser.parseCategoryHeaderResponse
import com.vodovoz.app.data.parser.response.category.ConcreteFilterResponseJsonParser.parseConcreteFilterResponse
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
import com.vodovoz.app.data.parser.response.favorite.AddToFavoriteResponseJsonParser.parseAddToFavoriteResponse
import com.vodovoz.app.data.parser.response.favorite.FavoriteHeaderResponseJsonParser.parseFavoriteProductsHeaderBundleResponse
import com.vodovoz.app.data.parser.response.favorite.RemoveFromFavoriteResponseJsonParser.parseRemoveFromFavoriteResponse
import com.vodovoz.app.data.parser.response.history.HistoriesSliderResponseJsonParser.parseHistoriesSliderResponse
import com.vodovoz.app.data.parser.response.map.AddAddressResponseJsonParser.parseAddAddressResponse
import com.vodovoz.app.data.parser.response.map.AddressByGeocodeResponseJsonParser.parseAddressByGeocodeResponse
import com.vodovoz.app.data.parser.response.map.DeleteAddressResponseJsonParser.parseDeleteAddressResponse
import com.vodovoz.app.data.parser.response.map.DeliveryZonesBundleResponseJsonParser.parseDeliveryZonesBundleResponse
import com.vodovoz.app.data.parser.response.map.FetchAddressesSavedResponseJsonParser.parseFetchAddressesSavedResponse
import com.vodovoz.app.data.parser.response.map.UpdateAddressResponseJsonParser.parseUpdateAddressResponse
import com.vodovoz.app.data.parser.response.novelties.NoveltiesHeaderResponseJsonParser.parseNoveltiesHeaderResponse
import com.vodovoz.app.data.parser.response.novelties.NoveltiesSliderResponseParser.parseNoveltiesSliderResponse
import com.vodovoz.app.data.parser.response.order.OrderDetailsResponseJsonParser.parseOrderDetailsResponse
import com.vodovoz.app.data.parser.response.order.OrderSliderResponseJsonParser.parseOrderSliderResponse
import com.vodovoz.app.data.parser.response.ordering.PayMethodsResponseJsonParser.parsePayMethodsResponse
import com.vodovoz.app.data.parser.response.ordering.ShippingIntervalsResponseJsonParser.parseShippingIntervalsResponse
import com.vodovoz.app.data.parser.response.paginatedProducts.MaybeLikeProductsResponseJsonParser.parseMaybeLikeProductsResponse
import com.vodovoz.app.data.parser.response.paginatedProducts.SomeProductsByBrandResponseJsonParser.parseSomeProductsByBrandResponse
import com.vodovoz.app.data.parser.response.past_purchases.PastPurchasesHeaderResponseJsonParser.parsePastPurchasesHeaderResponse
import com.vodovoz.app.data.parser.response.popular.PopularSliderResponseJsonParser.parsePopularSliderResponse
import com.vodovoz.app.data.parser.response.popupNews.PopupNewsResponseJsonParser.parsePopupNewsResponse
import com.vodovoz.app.data.parser.response.product.ProductDetailsResponseJsonParser.parseProductDetailsResponse
import com.vodovoz.app.data.parser.response.promotion.AllPromotionsResponseJsonParser.parseAllPromotionsResponse
import com.vodovoz.app.data.parser.response.promotion.PromotionDetailResponseJsonParser.parsePromotionDetailResponse
import com.vodovoz.app.data.parser.response.promotion.PromotionSliderResponseJsonParser.parsePromotionSliderResponse
import com.vodovoz.app.data.parser.response.promotion.PromotionsByBannerResponseJsonParser.parsePromotionsByBannerResponse
import com.vodovoz.app.data.parser.response.questionnaires.QuestionnaireResponseJsonParser.parseQuestionnaireResponse
import com.vodovoz.app.data.parser.response.questionnaires.QuestionnaireTypesResponseJsonParser.parseQuestionnaireTypesResponse
import com.vodovoz.app.data.parser.response.search.DefaultSearchDataResponseJsonParser.parseDefaultSearchDataResponse
import com.vodovoz.app.data.parser.response.search.MatchesQueriesResponseJsonParser.parseMatchesQueriesResponse
import com.vodovoz.app.data.parser.response.search.ProductsByQueryHeaderResponseJsonParser.parseProductsByQueryHeaderResponse
import com.vodovoz.app.data.parser.response.service.AboutServicesResponseJsonParser.parseAboutServicesResponse
import com.vodovoz.app.data.parser.response.service.OrderServiceResponseJsonParser.parseOrderServiceResponse
import com.vodovoz.app.data.parser.response.service.ServiceByIdResponseJsonParser.parseServiceByIdResponse
import com.vodovoz.app.data.parser.response.service.ServiceOrderFormResponseJsonParser.parseServiceOrderFormResponse
import com.vodovoz.app.data.parser.response.shipping.FreeShippingDaysResponseJsonParser.parseFreeShippingDaysResponse
import com.vodovoz.app.data.parser.response.user.LoginResponseJsonParser.parseLoginResponse
import com.vodovoz.app.data.parser.response.user.PersonalProductsJsonParser.parsePersonalProductsResponse
import com.vodovoz.app.data.parser.response.user.RecoverPasswordJsonParser.parseRecoverPasswordResponse
import com.vodovoz.app.data.parser.response.user.RegisterResponseJsonParser.parseRegisterResponse
import com.vodovoz.app.data.parser.response.user.UpdateUserDataResponseJsonParser.parseUpdateUserDataResponse
import com.vodovoz.app.data.parser.response.user.UserDataResponseJsonParser.parseUserDataResponse
import com.vodovoz.app.data.parser.response.viewed.ViewedProductSliderResponseJsonParser.parseViewedProductsSliderResponse
import com.vodovoz.app.ui.model.FreeShippingDaysInfoBundleUI
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.rx3.rxSingle
import okhttp3.ResponseBody
import retrofit2.Response

class RemoteData(
    private val vodovozApi: VodovozApi,
    private val mapKitApi: MapKitApi
) : RemoteDataSource {

    //Общая информация о предоставляемых услугах
    override fun fetchAboutServices(): Single<ResponseEntity<AboutServicesBundleEntity>> = vodovozApi.fetchServicesResponse(
        action = "glav"
    ).flatMap { Single.just(it.parseAboutServicesResponse()) }

    override fun fetchServiceById(
        type: String?
    ): Single<ResponseEntity<ServiceEntity>> = vodovozApi.fetchServicesResponse(
        action = type
    ).flatMap { Single.just(it.parseServiceByIdResponse(type)) }

    //Отправить отзыв о продукте
    override fun sendCommentAboutProduct(
        productId: Long,
        rating: Int,
        comment: String,
        userId: Long
    ): Single<ResponseEntity<String>> = rxSingle {
        vodovozApi.fetchCommentsResponse(
            blockId = 12,
            action = "add",
            productId = productId,
            rating = rating,
            comment = comment,
            userId = userId
        )
    }.flatMap { Single.just(it.body()!!.parseSendCommentAboutProductResponse()) }

    //Все отзывы о продукте
    override suspend fun fetchAllCommentsByProduct(
        productId: Long,
        page: Int
    )= vodovozApi.fetchCommentsResponse(
        action = "detail",
        productId = productId,
        page = page
    )

    //Всплывающая новость
    override fun fetchPopupNews(userId: Long?): Single<ResponseEntity<PopupNewsEntity>> = vodovozApi.fetchNewsResponse(
        action = "okno",
        userId = userId,
        platform = "android"
    ).flatMap { Single.just(it.parsePopupNewsResponse()) }

    //Получить Cookie Session Id
    override fun fetchCookie(): Single<String> = vodovozApi.fetchCookie().flatMap {
        Single.just(it.headers().values("Set-Cookie").first())
    }

    //Очистить корзину
    override fun clearCart(): Single<ResponseEntity<Boolean>> = vodovozApi.fetchClearCartResponse(
        action = "delkorzina"
    ).flatMap { Single.just(it.parseClearCartResponse()) }

    //Добавить продукт в корзину
    override fun addProductToCart(
        productId: Long,
        quantity: Int
    ): Single<ResponseEntity<Boolean>> = vodovozApi.fetchAddProductResponse(
        action = "add",
        productId = productId,
        quantity = quantity
    ).flatMap {
        Single.just(it.parseAddProductToCartResponse())
    }

    //Удаление продукта из корзины
    override fun deleteProductFromCart(
        productId: Long
    ): Single<ResponseEntity<Boolean>> = vodovozApi.fetchDeleteProductResponse(
        action = "deletto",
        productId = productId,
    ).flatMap {
        Single.just(it.parseDeleteProductFromCartResponse())
    }

    //Изменение колличества товаров в корзине
    override fun changeProductsQuantityInCart(
        productId: Long,
        quantity: Int
    ): Single<ResponseEntity<Boolean>> = vodovozApi.fetchChangeProductsQuantityResponse(
        action = "guaty",
        productId = productId,
        quantity = quantity
    ).flatMap {
        Single.just(it.parseChangeProductQuantityInCartResponse())
    }

    //Содержимое корзины
    override fun fetchCart(): Single<ResponseEntity<CartBundleEntity>> = vodovozApi.fetchCartResponse(
        action = "getbasket"
    ).flatMap {
        Single.just(it.parseCartResponse())
    }

    //Информации о странах для слайдера на главной странице
    override fun fetchCountriesSlider(): Single<ResponseEntity<CountriesSliderBundleEntity>> = rxSingle {
        vodovozApi.fetchCountryResponse(action = "glav")
    }.flatMap { Single.just(it.body()!!.parseCountriesSliderResponse()) }

    //Главная информация о выбранной стране
    override fun fetchCountryHeader(
        countryId: Long
    ): Single<ResponseEntity<CategoryEntity>> = rxSingle {
        vodovozApi.fetchCountryResponse(
            action = "details",
            countryId = countryId
        )
    }.flatMap { Single.just(it.body()!!.parseCountryHeaderResponse()) }

    //Постраничная загрузка продуктов по для выбранной страны
    override suspend fun fetchProductsByCountry(
        countryId: Long,
        sort: String?,
        orientation: String?,
        categoryId: Long?,
        page: Int?,
    ) = vodovozApi.fetchCountryResponse(
        action = "details",
        countryId = countryId,
        sort = sort,
        orientation = orientation,
        page = page,
        categoryId = categoryId
    )

    //Информация о слайдере акций на главной странице
    override fun fetchPromotionsSlider(): Single<ResponseEntity<List<PromotionEntity>>> = vodovozApi.fetchPromotionResponse(
        action = "akcii",
        limit = 10,
        platform = "android"
    ).flatMap { Single.just(it.parsePromotionSliderResponse()) }

    //Подробная информация об акции
    override fun fetchPromotionDetails(
        promotionId: Long
    ): Single<ResponseEntity<PromotionDetailEntity>> = vodovozApi.fetchPromotionResponse(
        action = "detail",
        promotionId = promotionId
    ).flatMap { Single.just(it.parsePromotionDetailResponse()) }

    //Информация о всех акциях
    override fun fetchAllPromotions(
        filterId: Long
    ): Single<ResponseEntity<AllPromotionsBundleEntity>> = vodovozApi.fetchPromotionResponse(
        action = "akcii",
        filterId = filterId
    ).flatMap { Single.just(it.parseAllPromotionsResponse()) }

    //Информация о слайдере комментариев на главной странице
    override fun fetchCommentsSlider(): Single<ResponseEntity<List<CommentEntity>>> = vodovozApi.fetchCommentResponse(
        action = "otzivy",
        limit = 10
    ).flatMap { Single.just(it.parseCommentsSliderResponse()) }

    //Добавить отзыв о магазине
    override fun fetchSendCommentAboutShopResponse(
        userId: Long?,
        comment: String?,
        rating: Int?
    ): Single<ResponseEntity<String>> = vodovozApi.fetchCommentResponse(
        action = "add",
        userId = userId,
        comment = comment,
        rating = rating
    ).flatMap { Single.just(it.parseSendCommentAboutShopResponse()) }

    //Продукты по результатам поиска
    override suspend fun fetchProductsByQuery(
        query: String?,
        categoryId: Long?,
        sort: String?,
        orientation: String?,
        page: Int?
    ) = vodovozApi.fetchSearchResponse(
        action = "search",
        limit = 10,
        query = query,
        categoryId = categoryId,
        sort = sort,
        orientation = orientation,
        page = page
    )

    //Список подходящих запросов
    override fun fetchMatchesQueries(
        query: String?
    ): Single<ResponseEntity<List<String>>> = vodovozApi.fetchMatchesQueries(
        action = "glav",
        query = query
    ).flatMap { Single.just(it.parseMatchesQueriesResponse()) }

    override fun fetchProductsByQueryHeader(
        query: String?
    ): Single<ResponseEntity<CategoryEntity>> = rxSingle {
        vodovozApi.fetchSearchResponse(
            action = "search",
            query = query,
            limit = 10,
            page = 1
        )
    }.flatMap { Single.just(it.body()!!.parseProductsByQueryHeaderResponse()) }

    override fun fetchSearchDefaultData(): Single<ResponseEntity<DefaultSearchDataBundleEntity>> = rxSingle {
        vodovozApi.fetchSearchResponse(
            action = "glav"
        )
    }.flatMap { Single.just(it.body()!!.parseDefaultSearchDataResponse()) }

    //Информация о слайдере историй на главное странице
    override fun fetchHistoriesSlider(): Single<ResponseEntity<List<HistoryEntity>>> = vodovozApi.fetchHistoryResponse(
        blockId = 12,
        action = "stories",
        platform = "android"
    ).flatMap { Single.just(it.parseHistoriesSliderResponse()) }

    //Информация о слайдере популярных разделов на главное странице
    override fun fetchPopularSlider(): Single<ResponseEntity<List<CategoryEntity>>> = vodovozApi.fetchPopularResponse(
        action = "popylrazdel"
    ).flatMap { Single.just(it.parsePopularSliderResponse()) }

    override fun fetchFreeShippingDaysInfoResponse(): Single<ResponseEntity<FreeShippingDaysInfoBundleEntity>> =
        vodovozApi.fetchInfoAboutOrderingResponse().flatMap { Single.just(it.parseFreeShippingDaysResponse()) }

    override fun fetchShippingIntervals(
        addressId: Long?,
        userId: Long?,
        date: String?
    ): Single<ResponseEntity<List<ShippingIntervalEntity>>> = vodovozApi.fetchInfoAboutOrderingResponse(
        addressId = addressId,
        userId = userId,
        date = date
    ).flatMap { Single.just(it.parseShippingIntervalsResponse()) }

    //Информация о слайдере новинок на главной странице
    override fun fetchNoveltiesSlider(): Single<ResponseEntity<List<CategoryDetailEntity>>> = rxSingle {
        vodovozApi.fetchNoveltiesResponse(action = "novinki")
    }.flatMap { Single.just(it.body()!!.parseNoveltiesSliderResponse()) }

    override fun fetchNoveltiesHeader(): Single<ResponseEntity<CategoryEntity>> = rxSingle {
        vodovozApi.fetchNoveltiesResponse(
            action = "novinki",
            page = 1
        )
    }.flatMap { Single.just(it.body()!!.parseNoveltiesHeaderResponse()) }

    //Постраничная загрузка самых выгодных продуктов
    override suspend fun fetchProductsDiscount(
        categoryId: Long?,
        sort: String?,
        orientation: String?,
        page: Int?
    ) = vodovozApi.fetchNoveltiesResponse(
        action = "specpredlosh",
        categoryId = categoryId,
        sort = sort,
        orientation = orientation,
        page = page
    )

    //Постраничная загрузка новинок
    override suspend fun fetchProductsNovelties(
        categoryId: Long?,
        sort: String?,
        orientation: String?,
        page: Int?
    ) = vodovozApi.fetchNoveltiesResponse(
        action = "novinki",
        categoryId = categoryId,
        sort = sort,
        orientation = orientation,
        page = page
    )

    //Информация о слайдере самых выгодных продуктов на главной странице
    override fun fetchDiscountsSlider(): Single<ResponseEntity<List<CategoryDetailEntity>>> = rxSingle {
        vodovozApi.fetchNoveltiesResponse(action = "specpredlosh")
    }.flatMap { Single.just(it.body()!!.parseDiscountSliderResponse()) }

    //Информация о слайдере заказов на главной странице
    override fun fetchOrdersSlider(
        userId: Long
    ): Single<ResponseEntity<List<OrderEntity>>> = vodovozApi.fetchOrderSlider(
        userId
    ).flatMap { Single.just(it.parseOrderSliderResponse()) }

    //Продукты из категории "Может понравиться"
    override fun fetchMaybeLikeProducts(
        page: Int
    ): Single<ResponseEntity<PaginatedProductListEntity>> = rxSingle {
        vodovozApi.fetchNoveltiesResponse(
            action = "details",
            page = page
        )
    }.flatMap { Single.just(it.body()!!.parseMaybeLikeProductsResponse()) }

    //Слайдер ранее просмотренных продуктов
    override fun fetchViewedProductsSlider(
        userId: Long?
    ): Single<ResponseEntity<List<CategoryDetailEntity>>> = vodovozApi.fetchViewedProductSliderResponse(
        action = "viewed",
        userId = userId
    ).flatMap { Single.just(it.parseViewedProductsSliderResponse()) }

    //Слайдер рекламных баннеров
    override fun fetchAdvertisingBannersSlider(): Single<ResponseEntity<List<BannerEntity>>> = vodovozApi.fetchMainSliderResponse(
        action = "slayder"
    ).flatMap { Single.just(it.parseAdvertisingBannersSliderResponse()) }

    //Продукты по баннеру
    override fun fetchProductsByBanner(categoryId: Long): Single<ResponseEntity<List<ProductEntity>>> = vodovozApi.fetchMainSliderResponse(
        action = "detailtovar",
        categoryId = categoryId
    ).flatMap { Single.just(it.parseProductsByBannerResponse()) }

    //Акции по баннеру
    override fun fetchPromotionsByBanner(categoryId: Long): Single<ResponseEntity<AllPromotionsBundleEntity>> = vodovozApi.fetchMainSliderResponse(
        action = "detailaction",
        categoryId = categoryId
    ).flatMap {
        Single.just(it.parsePromotionsByBannerResponse())
    }

    //Слайдер баннеров категорий
    override fun fetchCategoryBannersSlider(): Single<ResponseEntity<List<BannerEntity>>> = vodovozApi.fetchMiniSliderResponse(
        action = "slayder",
        androidVersion = BuildConfig.VERSION_NAME
    ).flatMap { Single.just(it.parseCategoryBannersSliderResponse()) }

    //Слайдер брендов на главнйо странице
    override fun fetchBrandsSlider(): Single<ResponseEntity<List<BrandEntity>>> = rxSingle {
        vodovozApi.fetchBrandResponse(
            action = "brand",
            limit = 10
        )
    }.flatMap { Single.just(it.body()!!.parseBrandsSliderResponse()) }

    //Гланвная информация о выбранной стране
    override fun fetchBrandHeader(
        brandId: Long
    ): Single<ResponseEntity<CategoryEntity>> = rxSingle {
        vodovozApi.fetchBrandResponse(
            action = "detail",
            brandId = brandId.toString()
        )
    }.flatMap { Single.just(it.body()!!.parseBrandHeaderResponse()) }

    override fun fetchDiscountHeader(): Single<ResponseEntity<CategoryEntity>> = rxSingle {
        vodovozApi.fetchNoveltiesResponse(
            action = "specpredlosh",
            page = 1
        )
    }.flatMap { Single.just(it.body()!!.parseDiscountHeaderResponse()) }

    //Все бренды
    override fun fetchAllBrands(
        brandIdList: List<Long>
    ): Single<ResponseEntity<List<BrandEntity>>> = rxSingle {
        vodovozApi.fetchBrandResponse(
            action = "brand",
            brandIdList = StringBuilder().apply {
                brandIdList.forEach { brandId ->
                    append(brandId)
                    append(",")
                }
            }.toString()
        )
    }.flatMap { Single.just(it.body()!!.parseAllBrandsResponse()) }

    //Постраничная загрузка продуктов для выбранного бренда
    override suspend fun fetchProductsByBrand(
        brandId: Long?,
        code: String?,
        categoryId: Long?,
        sort: String?,
        orientation: String?,
        page: Int?
    ) = vodovozApi.fetchBrandResponse(
        action = "detail",
        brandId = brandId.toString(),
        code = code,
        categoryId = categoryId,
        orientation = orientation,
        sort = sort,
        page = page
    )

    //Постраничная загрузка продуктов для выбранной категории
    override suspend fun fetchProductsByCategory(
        categoryId: Long,
        sort: String,
        orientation: String,
        filter: String,
        filterValue: String,
        priceFrom: Int,
        priceTo: Int,
        page: Int
    ) = vodovozApi.fetchCategoryResponse(
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

    //Постраничная загрузка продуктов по слайдеру
    override suspend fun fetchProductsBySlider(
        categoryId: Long?,
        page: Int?,
        sort: String?,
        orientation: String?
    ) = vodovozApi.fetchDoubleSliderResponse(
        action = "details",
        androidVersion = BuildConfig.VERSION_NAME,
        categoryId = categoryId,
        sort = sort,
        orientation = orientation,
        page = page,
    )

    //Главная информация о слайдере
    override fun fetchSliderHeader(
        categoryId: Long
    ): Single<ResponseEntity<CategoryEntity>> = rxSingle {
        vodovozApi.fetchDoubleSliderResponse(
            action = "details",
            androidVersion = BuildConfig.VERSION_NAME,
            categoryId = categoryId
        )
    }.flatMap { Single.just(it.body()!!.parseSliderHeaderResponse()) }

    //Главная информация о категории
    override fun fetchCategoryHeader(
        categoryId: Long
    ): Single<ResponseEntity<CategoryEntity>> = rxSingle {
        vodovozApi.fetchCategoryResponse(
            blockId = 1,
            categoryId = categoryId
        )
    }.flatMap { Single.just(it.body()!!.parseCategoryHeaderResponse()) }

    //Каталог
    override fun fetchCatalog(): Single<ResponseEntity<List<CategoryEntity>>> =
        vodovozApi.fetchCatalogResponse().flatMap { Single.just(it.parseCatalogResponse()) }

    //Все филтры по продуктам для выбранной категории
    override fun fetchAllFiltersByCategory(
        categoryId: Long
    ): Single<ResponseEntity<FilterBundleEntity>> = vodovozApi.fetchFilterBundleResponse(
        categoryId = categoryId
    ).flatMap { Single.just(it.parseAllFiltersByCategoryResponse()) }

    //Подробная информация о продукте
    override fun fetchProductDetails(
        productId: Long
    ): Single<ResponseEntity<ProductDetailsBundleEntity>> = vodovozApi.fetchProductResponse(
        blockId = 1,
        productId = productId
    ).flatMap { Single.just(it.parseProductDetailsResponse()) }

    //Филтр для продукта по id
    override fun fetchProductFilterById(
        categoryId: Long,
        filterCode: String,
    ): Single<ResponseEntity<List<FilterValueEntity>>> = vodovozApi.fetchFilterResponse(
        action = "getAllValueOfProps",
        categoryId = categoryId,
        filterCode = filterCode
    ).flatMap { Single.just(it.parseConcreteFilterResponse()) }

    //Постраничная загрузка нескольких продуктов для выбранного бренда
    override fun fetchSomeProductsByBrand(
        productId: Long,
        brandId: Long,
        page: Int
    ): Single<ResponseEntity<PaginatedProductListEntity>> = vodovozApi.fetchBrandByProductResponse(
        blockId = 12,
        productId = productId,
        brandId = brandId,
        page = page
    ).flatMap { Single.just(it.parseSomeProductsByBrandResponse()) }

    //Верхний слайдер на главной странице
    override fun fetchTopSlider(): Single<ResponseEntity<List<CategoryDetailEntity>>> = rxSingle {
        vodovozApi.fetchDoubleSliderResponse(
            action = "topglav",
            arg = "new"
        )
    }.flatMap { Single.just(it.body()!!.parseTopSliderResponse()) }

    //Нижний слайдер на главной странице
    override fun fetchBottomSlider(): Single<ResponseEntity<List<CategoryDetailEntity>>> = rxSingle {
        vodovozApi.fetchDoubleSliderResponse(
            action = "topglav",
            arg = "new"
        )
    }.flatMap { Single.just(it.body()!!.parseBottomSliderResponse()) }

    //Регистрация нового пользователя
    override fun register(
        firstName: String,
        secondName: String,
        email: String,
        password: String,
        phone: String,
    ): Single<ResponseEntity<Long>> = vodovozApi.fetchRegisterResponse(
        firstName = firstName,
        secondName = secondName,
        email = email,
        password = password,
        phone = phone
    ).flatMap { Single.just(it.parseRegisterResponse()) }

    //Авторизация
    override fun login(
        email: String,
        password: String
    ): Single<ResponseEntity<Long>> = vodovozApi.fetchLoginResponse(
        email = email,
        password = password
    ).flatMap { Single.just(it.parseLoginResponse()) }

    //Информация о пользователе
    override fun fetchUserData(
        userId: Long
    ): Single<ResponseEntity<UserDataEntity>> = vodovozApi.fetchProfileResponse(
        action = "details",
        userId = userId
    ).flatMap { Single.just(it.parseUserDataResponse()) }

    override fun updateUserData(
        userId: Long,
        firstName: String?,
        secondName: String?,
        password: String?,
        phone: String?,
        sex: String?,
        birthday: String?,
        email: String?,
    ): Single<ResponseEntity<Boolean>> = vodovozApi.fetchProfileResponse(
        action = "edit",
        userId = userId,
        firstName = firstName,
        secondName = secondName,
        password = password,
        phone = phone,
        sex = sex,
        birthday = birthday,
        email = email
    ).flatMap { Single.just(it.parseUpdateUserDataResponse()) }

    override fun fetchPayMethods(
        addressId: Long?,
        userId: Long?
    ): Single<ResponseEntity<List<PayMethodEntity>>> = vodovozApi.fetchInfoAboutOrderingResponse(
        addressId = addressId,
        userId = userId
    ).flatMap { Single.just(it.parsePayMethodsResponse()) }

    //Добавить в избранное для авторизованного пользователя
    override fun addToFavorite(
        productIdList: List<Long>,
        userId: Long
    ): Single<ResponseEntity<String>> = vodovozApi.fetchChangeFavoriteResponse(
        blockId = 12,
        action = "add",
        productIdList = StringBuilder().apply {
            productIdList.forEach { productId ->
                append(productId).append(",")
            }
        }.toString(),
        userId = userId
    ).flatMap { Single.just(it.parseAddToFavoriteResponse()) }

    //Удалить из избранного для авторизованного пользователя
    override fun removeFromFavorite(
        productId: Long,
        userId: Long
    ): Single<ResponseEntity<String>> = vodovozApi.fetchChangeFavoriteResponse(
        blockId = 12,
        action = "del",
        productIdList = productId.toString(),
        userId = userId
    ).flatMap { Single.just(it.parseRemoveFromFavoriteResponse()) }

    //Основная информация об избранных продуктах
    override fun fetchFavoriteProductsHeaderBundleResponse(
        userId: Long?,
        productIdListStr: String?
    ): Single<ResponseEntity<FavoriteProductsHeaderBundleEntity>> = rxSingle {
        vodovozApi.fetchFavoriteResponse(
            userId = userId,
            productIdList = productIdListStr,
            action = "nalichie"
        )
    }.flatMap { Single.just(it.body()!!.parseFavoriteProductsHeaderBundleResponse()) }

    override suspend fun fetchFavoriteProductsResponse(
        userId: Long?,
        productIdListStr: String,
        categoryId: Long?,
        sort: String?,
        orientation: String?,
        page: Int?,
        isAvailable: Boolean?,
    ) = vodovozApi.fetchFavoriteResponse(
        userId = userId,
        productIdList = productIdListStr,
        categoryId = categoryId,
        sort = sort,
        orientation = orientation,
        action = when(isAvailable) {
            true -> "nalichie"
            false -> "netnalichi"
            else -> null
        },
        page = page
    )

    //Информация о зонах доставки
    override fun fetchDeliveryZonesResponse(): Single<ResponseEntity<DeliveryZonesBundleEntity>> = vodovozApi.fetchMapResponse(
        action = "tochkakarta"
    ).flatMap { Single.just(it.parseDeliveryZonesBundleResponse()) }

    //Адрес по координатам
    override fun fetchAddressByGeocodeResponse(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): Single<ResponseEntity<AddressEntity>> = mapKitApi.fetchAddressByGeocodeResponse(
        apiKey = "346ef353-b4b2-44b3-b597-210d62eeb66b",
        geocode = "$longitude,$latitude",
        format = "json"
    ).flatMap {
        Log.i(LogSettings.ID_LOG, it.toString())
        Single.just(it.body()!!.parseAddressByGeocodeResponse())
    }

    //Получить сохраненные адреса
    override fun fetchAddressesSaved(
        userId: Long?,
        type: Int?
    ): Single<ResponseEntity<List<AddressEntity>>> = vodovozApi.fetchAddressResponse(
        blockId = 102,
        action = "get",
        userid = userId,
        type = type
    ).flatMap {
        Single.just(it.body()!!.parseFetchAddressesSavedResponse())
    }

    //Добавить адрес в сохраненные
    override fun addAddress(
        locality: String?,
        street: String?,
        house: String?,
        entrance: String?,
        floor: String?,
        office: String?,
        comment: String?,
        type: Int?,
        userId: Long?
    ): Single<ResponseEntity<String>> = vodovozApi.fetchAddressResponse(
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
        action = "add"
    ).flatMap {
        Log.i(LogSettings.ID_LOG, it.toString())
        Single.just(it.body()!!.parseAddAddressResponse())
    }

    //Обновить адрес
    override fun updateAddress(
        locality: String?,
        street: String?,
        house: String?,
        entrance: String?,
        floor: String?,
        office: String?,
        comment: String?,
        type: Int?,
        addressId: Long?,
        userId: Long?
    ): Single<ResponseEntity<String>> = vodovozApi.fetchAddressResponse(
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
        action = "update"
    ).flatMap {
        Log.i(LogSettings.ID_LOG, it.toString())
        Single.just(it.body()!!.parseUpdateAddressResponse())
    }

    //Удалить адресс
    override fun deleteAddress(
        addressId: Long?,
        userId: Long?
    ): Single<ResponseEntity<String>> = vodovozApi.fetchAddressResponse(
        addressId = addressId,
        userid = userId,
        action = "del",
        blockId = 102
    ).flatMap {
        Log.i(LogSettings.ID_LOG, it.toString())
        Single.just(it.body()!!.parseDeleteAddressResponse()) }

    //Постраничная загрузка всех заказов
    override suspend fun fetchAllOrders(
        userId: Long?,
        appVersion: String?,
        orderId: Long?,
        status: String?,
        page: Int?
    ) = vodovozApi.fetchOrdersHistoryResponse(
        userId = userId,
        appVersion = appVersion,
        action = "spisok",
        orderId = orderId,
        status = status,
        page = page
    )

    //Детальная информация о заказе
    override fun fetchOrderDetailsResponse(
        userId: Long?,
        appVersion: String?,
        orderId: Long?
    ): Single<ResponseEntity<OrderDetailsEntity>> = vodovozApi.fetchOrdersResponse(
        action = "detail",
        userId = userId,
        appVersion = appVersion,
        orderId = orderId
    ).flatMap { Single.just(it.parseOrderDetailsResponse()) }


    override suspend fun fetchPastPurchasesProducts(
        userId: Long?,
        sort: String?,
        orientation: String?,
        categoryId: Long?,
        isAvailable: Boolean?,
        page: Int?,
    ): Response<ResponseBody> = vodovozApi.fetchPastPurchasesResponse(
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

    override fun fetchPastPurchasesHeader(
        userId: Long?
    ): Single<ResponseEntity<PastPurchasesHeaderBundleEntity>> = rxSingle {
        vodovozApi.fetchPastPurchasesResponse(
            action = "getLastFifty",
            userId = userId,
            page = 1
        )
    }.flatMap { Single.just(it.body()!!.parsePastPurchasesHeaderResponse()) }

    override fun recoverPassword(
        email: String?
    ): Single<ResponseEntity<Boolean>> = vodovozApi.recoverPassword(
        forgotPassword = "yes",
        email = email
    ).flatMap { Single.just(it.parseRecoverPasswordResponse()) }

    override fun fetchPersonalProducts(
        userId: Long?,
        page: Int?
    ): Single<ResponseEntity<CategoryDetailEntity>> = vodovozApi.fetchPersonalProducts(
        action = "tovarchik",
        userId = userId,
        page = page
    ).flatMap { Single.just(it.parsePersonalProductsResponse()) }

    override fun fetchActivateDiscountCardInfo(
        userId: Long?
    ): Single<ResponseEntity<ActivateDiscountCardBundleEntity>> = vodovozApi.fetchDiscountCardBaseRequest(
        action = "glav",
        userId = userId
    ).flatMap { Single.just(it.parseActivateDiscountCardInfoResponse()) }

    override fun activateDiscountCard(
        userId: Long?,
        value: String?,
    ): Single<ResponseEntity<String>> = vodovozApi.fetchDiscountCardBaseRequest(
        action = "edit",
        userId = userId,
        value = value
    ).flatMap { Single.just(it.parseActivateDiscountCardResponse()) }

    override fun fetchQuestionnairesTypes(): Single<ResponseEntity<List<QuestionnaireTypeEntity>>> =
        vodovozApi.fetchQuestionnairesResponse().flatMap { Single.just(it.parseQuestionnaireTypesResponse()) }

    override fun fetchQuestionnaire(
        userId: Long?,
        questionnaireType: String?
    ): Single<ResponseEntity<List<QuestionEntity>>> = vodovozApi.fetchQuestionnairesResponse(
        action = questionnaireType,
        userId = userId
    ).flatMap { Single.just(it.parseQuestionnaireResponse()) }

    override fun fetchContacts(
        appVersion: String?
    ): Single<ResponseEntity<ContactsBundleEntity>> = vodovozApi.fetchContactsResponse(
        action = "dannyesvyazi",
        appVersion = appVersion
    ).flatMap { Single.just(it.parseContactsBundleResponse()) }

    override fun sendMail(
        name: String?,
        phone: String?,
        email: String?,
        comment: String?
    ): Single<ResponseEntity<String>> = vodovozApi.sendMail(
        name = name,
        phone = phone,
        email = email,
        comment = comment
    ).flatMap { Single.just(it.parseSendMailResponse()) }

    override fun fetchFormForOrderService(
        type: String?,
        userId: Long?
    ): Single<ResponseEntity<List<ServiceOrderFormFieldEntity>>> = vodovozApi.fetchOrderServiceResponse(
        action = "glav",
        type = type,
        userId = userId
    ).flatMap { Single.just(it.parseServiceOrderFormResponse()) }

    override fun orderService(
        type: String?,
        userId: Long?,
        value: String?
    ): Single<ResponseEntity<String>> = vodovozApi.fetchOrderServiceResponse(
        action = "add",
        type = type,
        userId = userId,
        value = value
    ).flatMap { Single.just(it.parseOrderServiceResponse()) }

//
//    override fun updateUserPhoto(
//        userId: Long?,
//        photo: Any?
//    ) = vodovozApi.updateUserPhoto(
//        userId = userId,
//        photo = photo
//    ).flatMap { Single.just() }

}