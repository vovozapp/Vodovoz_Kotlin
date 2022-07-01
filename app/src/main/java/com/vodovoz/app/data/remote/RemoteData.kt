package com.vodovoz.app.data.remote

import com.vodovoz.app.BuildConfig
import com.vodovoz.app.data.model.common.*
import com.vodovoz.app.data.model.features.AllPromotionsBundleEntity
import com.vodovoz.app.data.model.features.CountryBundleEntity
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
import com.vodovoz.app.data.parser.response.country.CountryHeaderResponseJsonParser.parseCountryHeaderResponse
import com.vodovoz.app.data.parser.response.country.CountrySliderResponseJsonParser.parseCountriesSliderResponse
import com.vodovoz.app.data.parser.response.discount.DiscountHeaderResponseJsonParser.parseDiscountHeaderResponse
import com.vodovoz.app.data.parser.response.discount.DiscountSliderResponseParser.parseDiscountSliderResponse
import com.vodovoz.app.data.parser.response.doubleSlider.DoubleSliderResponseJsonParser.parseBottomSliderResponse
import com.vodovoz.app.data.parser.response.doubleSlider.DoubleSliderResponseJsonParser.parseTopSliderResponse
import com.vodovoz.app.data.parser.response.doubleSlider.SliderHeaderResponseJsonParser.parseSliderHeaderResponse
import com.vodovoz.app.data.parser.response.history.HistoriesSliderResponseJsonParser.parseHistoriesSliderResponse
import com.vodovoz.app.data.parser.response.novelties.NoveltiesHeaderResponseJsonParser.parseNoveltiesHeaderResponse
import com.vodovoz.app.data.parser.response.novelties.NoveltiesSliderResponseParser.parseNoveltiesSliderResponse
import com.vodovoz.app.data.parser.response.order.OrderSliderResponseJsonParser.parseOrderSliderResponse
import com.vodovoz.app.data.parser.response.paginatedProducts.MaybeLikeProductsResponseJsonParser.parseMaybeLikeProductsResponse
import com.vodovoz.app.data.parser.response.paginatedProducts.SomeProductsByBrandResponseJsonParser.parseSomeProductsByBrandResponse
import com.vodovoz.app.data.parser.response.popular.PopularSliderResponseJsonParser.parsePopularSliderResponse
import com.vodovoz.app.data.parser.response.product.ProductDetailsResponseJsonParser.parseProductDetailsResponse
import com.vodovoz.app.data.parser.response.promotion.AllPromotionsResponseJsonParser.parseAllPromotionsResponse
import com.vodovoz.app.data.parser.response.promotion.PromotionDetailResponseJsonParser.parsePromotionDetailResponse
import com.vodovoz.app.data.parser.response.promotion.PromotionSliderResponseJsonParser.parsePromotionSliderResponse
import com.vodovoz.app.data.parser.response.promotion.PromotionsByBannerResponseJsonParser.parsePromotionsByBannerResponse
import com.vodovoz.app.data.parser.response.user.LoginResponseJsonParser.parseLoginResponse
import com.vodovoz.app.data.parser.response.user.RegisterResponseJsonParser.parseRegisterResponse
import com.vodovoz.app.data.parser.response.user.UserDataResponseJsonParser.parseUserDataResponse
import com.vodovoz.app.data.parser.response.viewed.ViewedProductSliderResponseJsonParser.parseViewedProductsSliderResponse
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.rx3.rxSingle

class RemoteData(
    private val api: Api
) : RemoteDataSource {

    //Получить Cookie Session Id
    override fun fetchCookie(): Single<String> = api.fetchCookie().flatMap {
        Single.just(it.headers().values("Set-Cookie").first())
    }

    //Очистить корзину
    override fun clearCart(): Single<ResponseEntity<Boolean>> = api.fetchClearCartResponse(
        action = "delkorzina"
    ).flatMap { Single.just(it.parseClearCartResponse()) }

    //Добавить продукт в корзину
    override fun addProductToCart(
        productId: Long,
        quantity: Int
    ): Single<ResponseEntity<Boolean>> = api.fetchAddProductResponse(
        action = "add",
        productId = productId,
        quantity = quantity
    ).flatMap {
        Single.just(it.parseAddProductToCartResponse())
    }

    //Удаление продукта из корзины
    override fun deleteProductFromCart(
        productId: Long
    ): Single<ResponseEntity<Boolean>> = api.fetchDeleteProductResponse(
        action = "deletto",
        productId = productId,
    ).flatMap {
        Single.just(it.parseDeleteProductFromCartResponse())
    }

    //Изменение колличества товаров в корзине
    override fun changeProductsQuantityInCart(
        productId: Long,
        quantity: Int
    ): Single<ResponseEntity<Boolean>> = api.fetchChangeProductsQuantityResponse(
        action = "guaty",
        productId = productId,
        quantity = quantity
    ).flatMap {
        Single.just(it.parseChangeProductQuantityInCartResponse())
    }

    //Содержимое корзины
    override fun fetchCart() = api.fetchCartResponse(
        action = "getbasket"
    ).flatMap {
        Single.just(it.parseCartResponse())
    }

    //Информации о странах для слайдера на главной странице
    override fun fetchCountriesSlider(): Single<ResponseEntity<CountryBundleEntity>> = rxSingle {
        api.fetchCountryResponse(action = "glav")
    }.flatMap { Single.just(it.body()!!.parseCountriesSliderResponse()) }

    //Главная информация о выбранной стране
    override fun fetchCountryHeader(
        countryId: Long
    ): Single<ResponseEntity<CategoryEntity>> = rxSingle {
        api.fetchCountryResponse(
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
    ) = api.fetchCountryResponse(
        action = "details",
        countryId = countryId,
        sort = sort,
        orientation = orientation,
        page = page,
        categoryId = categoryId
    )

    //Информация о слайдере акций на главной странице
    override fun fetchPromotionsSlider(): Single<ResponseEntity<List<PromotionEntity>>> = api.fetchPromotionResponse(
        action = "akcii",
        limit = 10,
        platform = "android"
    ).flatMap { Single.just(it.parsePromotionSliderResponse()) }

    //Подробная информация об акции
    override fun fetchPromotionDetails(
        promotionId: Long
    ): Single<ResponseEntity<PromotionDetailEntity>> = api.fetchPromotionResponse(
        action = "detail",
        promotionId = promotionId
    ).flatMap { Single.just(it.parsePromotionDetailResponse()) }

    //Информация о всех акциях
    override fun fetchAllPromotions(
        filterId: Long
    ): Single<ResponseEntity<AllPromotionsBundleEntity>> = api.fetchPromotionResponse(
        action = "akcii",
        filterId = filterId
    ).flatMap { Single.just(it.parseAllPromotionsResponse()) }

    //Информация о слайдере комментариев на главной странице
    override fun fetchCommentsSlider(): Single<ResponseEntity<List<CommentEntity>>> = api.fetchCommentResponse(
        action = "otzivy",
        limit = 10
    ).flatMap { Single.just(it.parseCommentsSliderResponse()) }

    //Информация о слайдере историй на главное странице
    override fun fetchHistoriesSlider(): Single<ResponseEntity<List<HistoryEntity>>> = api.fetchHistoryResponse(
        blockId = 12,
        action = "stories",
        platform = "android"
    ).flatMap { Single.just(it.parseHistoriesSliderResponse()) }

    //Информация о слайдере популярных разделов на главное странице
    override fun fetchPopularSlider(): Single<ResponseEntity<List<CategoryEntity>>> = api.fetchPopularResponse(
        action = "popylrazdel"
    ).flatMap { Single.just(it.parsePopularSliderResponse()) }

    //Информация о слайдере новинок на главной странице
    override fun fetchNoveltiesSlider(): Single<ResponseEntity<List<CategoryDetailEntity>>> = rxSingle {
        api.fetchNoveltiesResponse(action = "novinki")
    }.flatMap { Single.just(it.body()!!.parseNoveltiesSliderResponse()) }

    override fun fetchNoveltiesHeader(): Single<ResponseEntity<CategoryEntity>> = rxSingle {
        api.fetchNoveltiesResponse(
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
    ) = api.fetchNoveltiesResponse(
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
    ) = api.fetchNoveltiesResponse(
        action = "novinki",
        categoryId = categoryId,
        sort = sort,
        orientation = orientation,
        page = page
    )

    //Информация о слайдере самых выгодных продуктов на главной странице
    override fun fetchDiscountsSlider(): Single<ResponseEntity<List<CategoryDetailEntity>>> = rxSingle {
        api.fetchNoveltiesResponse(action = "specpredlosh")
    }.flatMap { Single.just(it.body()!!.parseDiscountSliderResponse()) }

    //Информация о слайдере заказов на главной странице
    override fun fetchOrdersSlider(
        userId: Long
    ): Single<ResponseEntity<List<OrderEntity>>> = api.fetchOrderSlider(
        userId
    ).flatMap { Single.just(it.parseOrderSliderResponse()) }

    //Продукты из категории "Может понравиться"
    override fun fetchMaybeLikeProducts(
        page: Int
    ): Single<ResponseEntity<PaginatedProductListEntity>> = rxSingle {
        api.fetchNoveltiesResponse(
            action = "details",
            page = page
        )
    }.flatMap { Single.just(it.body()!!.parseMaybeLikeProductsResponse()) }

    //Слайдер ранее просмотренных продуктов
    override fun fetchViewedProductsSlider(
        userId: Long?
    ): Single<ResponseEntity<List<CategoryDetailEntity>>> = api.fetchViewedProductSliderResponse(
        action = "viewed",
        userId = userId
    ).flatMap { Single.just(it.parseViewedProductsSliderResponse()) }

    //Слайдер рекламных баннеров
    override fun fetchAdvertisingBannersSlider(): Single<ResponseEntity<List<BannerEntity>>> = api.fetchMainSliderResponse(
        action = "slayder"
    ).flatMap { Single.just(it.parseAdvertisingBannersSliderResponse()) }

    //Продукты по баннеру
    override fun fetchProductsByBanner(categoryId: Long): Single<ResponseEntity<List<ProductEntity>>> = api.fetchMainSliderResponse(
        action = "detailtovar",
        categoryId = categoryId
    ).flatMap { Single.just(it.parseProductsByBannerResponse()) }

    //Акции по баннеру
    override fun fetchPromotionsByBanner(categoryId: Long): Single<ResponseEntity<AllPromotionsBundleEntity>> = api.fetchMainSliderResponse(
        action = "detailaction",
        categoryId = categoryId
    ).flatMap {
        Single.just(it.parsePromotionsByBannerResponse())
    }

    //Слайдер баннеров категорий
    override fun fetchCategoryBannersSlider(): Single<ResponseEntity<List<BannerEntity>>> = api.fetchMiniSliderResponse(
        action = "slayder",
        androidVersion = BuildConfig.VERSION_NAME
    ).flatMap { Single.just(it.parseCategoryBannersSliderResponse()) }

    //Слайдер брендов на главнйо странице
    override fun fetchBrandsSlider(): Single<ResponseEntity<List<BrandEntity>>> = rxSingle {
        api.fetchBrandResponse(
            action = "brand",
            limit = 10
        )
    }.flatMap { Single.just(it.body()!!.parseBrandsSliderResponse()) }

    //Гланвная информация о выбранной стране
    override fun fetchBrandHeader(
        brandId: Long
    ): Single<ResponseEntity<CategoryEntity>> = rxSingle {
        api.fetchBrandResponse(
            action = "detail",
            brandId = brandId.toString()
        )
    }.flatMap { Single.just(it.body()!!.parseBrandHeaderResponse()) }

    override fun fetchDiscountHeader(): Single<ResponseEntity<CategoryEntity>> = rxSingle {
        api.fetchNoveltiesResponse(
            action = "specpredlosh",
            page = 1
        )
    }.flatMap { Single.just(it.body()!!.parseDiscountHeaderResponse()) }

    //Все бренды
    override fun fetchAllBrands(
        brandIdList: List<Long>
    ): Single<ResponseEntity<List<BrandEntity>>> = rxSingle {
        api.fetchBrandResponse(
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
    ) = api.fetchBrandResponse(
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
    ) = api.fetchCategoryResponse(
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
    ) = api.fetchDoubleSliderResponse(
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
        api.fetchDoubleSliderResponse(
            action = "details",
            androidVersion = BuildConfig.VERSION_NAME,
            categoryId = categoryId
        )
    }.flatMap { Single.just(it.body()!!.parseSliderHeaderResponse()) }

    //Главная информация о категории
    override fun fetchCategoryHeader(
        categoryId: Long
    ): Single<ResponseEntity<CategoryEntity>> = rxSingle {
        api.fetchCategoryResponse(
            blockId = 1,
            categoryId = categoryId
        )
    }.flatMap { Single.just(it.body()!!.parseCategoryHeaderResponse()) }

    //Каталог
    override fun fetchCatalog(): Single<ResponseEntity<List<CategoryEntity>>> =
        api.fetchCatalogResponse().flatMap { Single.just(it.parseCatalogResponse()) }

    //Все филтры по продуктам для выбранной категории
    override fun fetchAllFiltersByCategory(
        categoryId: Long
    ): Single<ResponseEntity<FilterBundleEntity>> = api.fetchFilterBundleResponse(
        categoryId = categoryId
    ).flatMap { Single.just(it.parseAllFiltersByCategoryResponse()) }

    //Подробная информация о продукте
    override fun fetchProductDetails(
        productId: Long
    ): Single<ResponseEntity<ProductDetailBundleEntity>> = api.fetchProductResponse(
        blockId = 1,
        productId = productId
    ).flatMap { Single.just(it.parseProductDetailsResponse()) }

    //Филтр для продукта по id
    override fun fetchProductFilterById(
        categoryId: Long,
        filterCode: String,
    ): Single<ResponseEntity<List<FilterValueEntity>>> = api.fetchFilterResponse(
        action = "getAllValueOfProps",
        categoryId = categoryId,
        filterCode = filterCode
    ).flatMap { Single.just(it.parseConcreteFilterResponse()) }

    //Постраничная загрузка нескольких продуктов для выбранного бренда
    override fun fetchSomeProductsByBrand(
        productId: Long,
        brandId: Long,
        page: Int
    ): Single<ResponseEntity<PaginatedProductListEntity>> = api.fetchBrandByProductResponse(
        blockId = 12,
        productId = productId,
        brandId = brandId,
        page = page
    ).flatMap { Single.just(it.parseSomeProductsByBrandResponse()) }

    //Верхний слайдер на главной странице
    override fun fetchTopSlider(): Single<ResponseEntity<List<CategoryDetailEntity>>> = rxSingle {
        api.fetchDoubleSliderResponse(
            action = "topglav",
            arg = "new"
        )
    }.flatMap { Single.just(it.body()!!.parseTopSliderResponse()) }

    //Нижний слайдер на главной странице
    override fun fetchBottomSlider(): Single<ResponseEntity<List<CategoryDetailEntity>>> = rxSingle {
        api.fetchDoubleSliderResponse(
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
    ): Single<ResponseEntity<Long>> = api.fetchRegisterResponse(
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
    ): Single<ResponseEntity<Long>> = api.fetchLoginResponse(
        email = email,
        password = password
    ).flatMap { Single.just(it.parseLoginResponse()) }

    //Информация о пользователе
    override fun fetchUserData(
        userId: Long
    ): Single<ResponseEntity<UserDataEntity>> = api.fetchProfileResponse(
        action = "details",
        userId = userId
    ).flatMap { Single.just(it.parseUserDataResponse()) }

}