package com.vodovoz.app.data.remote

import com.vodovoz.app.data.parser.response.*
import com.vodovoz.app.data.parser.response.promotion.AllPromotionsResponseJsonParser.parseAllPromotionsResponse
import com.vodovoz.app.data.parser.response.ConcreteFilterResponseJsonParser.parseConcreteFilterResponse
import com.vodovoz.app.data.parser.response.country.CountrySliderResponseJsonParser.parseSliderCountryResponse
import com.vodovoz.app.data.parser.response.FilterBundleResponseJsonParser.parseFilterBundleResponse
import com.vodovoz.app.data.parser.response.LoginResponseJsonParser.parseLoginResponse
import com.vodovoz.app.data.parser.response.OrderSliderResponseJsonParser.parseOrderSliderResponse
import com.vodovoz.app.data.parser.response.PaginatedBrandProductListResponseJsonParser.parsePaginatedBrandProductListResponse
import com.vodovoz.app.data.parser.response.PaginatedMaybeLikeProductListResponseJsonParser.parsePaginatedMaybeLikeProductListResponse
import com.vodovoz.app.data.parser.response.ProductDetailResponseJsonParser.parseProductDetailResponse
import com.vodovoz.app.data.parser.response.promotion.PromotionDetailResponseJsonParser.parsePromotionDetailResponse
import com.vodovoz.app.data.parser.response.RegisterResponseJsonParser.parseRegisterResponse
import com.vodovoz.app.data.parser.response.UserDataResponseJsonParser.parseUserDataResponse
import com.vodovoz.app.data.parser.response.ViewedProductSliderResponseJsonParser.parseViewedProductSliderResponse
import com.vodovoz.app.data.parser.response.country.CountryHeaderResponseJsonParser.parseCountryHeaderResponse
import com.vodovoz.app.data.parser.response.country.ProductsByCountryResponseJsonParser.parseProductsByCountryResponse
import com.vodovoz.app.data.parser.response.promotion.PromotionSliderResponseJsonParser
import com.vodovoz.app.data.parser.response.promotion.PromotionSliderResponseJsonParser.parsePromotionSliderResponse
import io.reactivex.rxjava3.core.Single

class RemoteData(
    private val api: Api,
    private val bannerResponseJsonParser: BannerResponseJsonParser,
    private val catalogResponseJsonParser: CatalogResponseJsonParser,
    private val doubleCategoryResponseJsonParser: DoubleCategoryResponseJsonParser,
    private val discountCategoryResponseParser: DiscountCategoryResponseParser,
    private val noveltiesCategoryResponseParser: NoveltiesCategoryResponseParser,
    private val commentResponseJsonParser: CommentResponseJsonParser,
    private val historyResponseJsonParser: HistoryResponseJsonParser,
    private val categoryPopularResponseJsonParser: CategoryPopularResponseJsonParser,
    private val brandResponseJsonParser: BrandResponseJsonParser,
    private val categoryHeaderResponseJsonParser: CategoryHeaderResponseJsonParser,
) : RemoteDataSource {

    //Страны
    override fun fetchCountrySlider() =
        api.fetchCountryResponse(action = "glav").flatMap { Single.just(it.parseSliderCountryResponse()) }

    override fun fetchCountryHeader(
        countryId: Long
    ) =  api.fetchCountryResponse(
        action = "details",
        countryId = countryId
    ).flatMap { Single.just(it.parseCountryHeaderResponse()) }

    override fun fetchProductsByCountry(
        countryId: Long,
        sort: String?,
        orientation: String?,
        page: Int?,
        categoryId: Long?,
    ) = api.fetchCountryResponse(
        action = "details",
        countryId = countryId,
        sort = sort,
        orientation = orientation,
        page = page,
        categoryId = categoryId
    ).flatMap { Single.just(it.parseProductsByCountryResponse()) }

    override fun fetchPromotionSlider() = api.fetchPromotionResponse(
        action = "akcii",
        limit = 10,
        platform = "android"
    ).flatMap { Single.just(it.parsePromotionSliderResponse()) }

    override fun fetchPromotionDetail(
        promotionId: Long
    ) = api.fetchPromotionResponse(
        action = "detail",
        promotionId = promotionId
    ).flatMap { Single.just(it.parsePromotionDetailResponse()) }

    override fun fetchAllPromotions(
        filterId: Long
    ) = api.fetchPromotionResponse(
        action = "akcii",
        filterId = filterId
    ).flatMap { Single.just(it.parseAllPromotionsResponse()) }

    //Акции



    //HomePage
    override fun fetchMainBannerResponse() =
        api.fetchMainBannerResponse().flatMap { Single.just(bannerResponseJsonParser.parseResponse(it)) }

    override fun fetchSecondaryBannerResponse() =
        api.fetchSecondaryBannerResponse().flatMap { Single.just(bannerResponseJsonParser.parseResponse(it)) }

    override fun fetchCommentResponse() =
        api.fetchCommentList().flatMap { Single.just(commentResponseJsonParser.parseResponse(it)) }

    override fun fetchOrderSlider(userId: Long) =
        api.fetchOrderSlider(userId).flatMap { Single.just(it.parseOrderSliderResponse()) }

    override fun fetchViewedProductSlider(userId: Long) =
        api.fetchViewedProductSlider(userId = userId).flatMap { Single.just(it.parseViewedProductSliderResponse()) }

    override fun fetchHistoryResponse() =
        api.fetchHistoryResponse().flatMap { Single.just(historyResponseJsonParser.parseResponse(it)) }

    override fun fetchPopularResponse() =
        api.fetchPopularResponse().flatMap { Single.just(categoryPopularResponseJsonParser.parseResponse(it)) }

    override fun fetchDiscountCategoryResponse() =
        api.fetchDiscountResponse().flatMap { Single.just(discountCategoryResponseParser.parseResponse(it)) }

    override fun fetchBrandResponse() =
        api.fetchBrandResponse().flatMap { Single.just(brandResponseJsonParser.parseResponse(it)) }

    override fun fetchNoveltiesCategoryResponse() =
        api.fetchNoveltiesResponse().flatMap { Single.just(noveltiesCategoryResponseParser.parseResponse(it)) }

    override fun fetchDoubleCategoryResponse() =
        api.fetchDoubleSectionList().flatMap { Single.just(doubleCategoryResponseJsonParser.parseResponse(it)) }

    //CatalogPage
    override fun fetchCatalogResponse() =
        api.fetchCatalogResponse().flatMap { Single.just(catalogResponseJsonParser.parseResponse(it)) }

    override fun fetchCategoryHeader(
        categoryId: Long
    ) = api.fetchCategory(
        categoryId = categoryId
    ).flatMap { Single.just(categoryHeaderResponseJsonParser.parseResponse(it)) }

    override fun fetchFilterBundleResponse(
        categoryId: Long
    ) = api.fetchFilterBundleResponse(
        categoryId = categoryId
    ).flatMap { Single.just(it.parseFilterBundleResponse()) }

    override fun fetchProductDetailResponse(
        productId: Long
    ) = api.fetchProductDetailResponse(
        productId = productId
    ).flatMap { Single.just(it.parseProductDetailResponse()) }

    override fun fetchPaginatedMaybeLikeProductListResponse(
        pageIndex: Int
    ) = api.fetchPaginatedMaybeLikeProductListResponse(
        pageIndex = pageIndex
    ).flatMap { Single.just(it.parsePaginatedMaybeLikeProductListResponse()) }

    override fun fetchConcreteFilterResponse(
        categoryId: Long,
        filterCode: String,
    ) = api.fetchConcreteFilterResponse(
        categoryId = categoryId,
        filterCode = filterCode
    ).flatMap { Single.just(it.parseConcreteFilterResponse()) }

    override fun fetchPaginatedBrandProductListResponse(
        productId: Long,
        brandId: Long,
        pageIndex: Int
    ) = api.fetchPaginatedBrandProductListResponse(
        productId = productId,
        brandId = brandId,
        pageIndex = pageIndex
    ).flatMap { Single.just(it.parsePaginatedBrandProductListResponse()) }

    override suspend fun fetchCategoryDetailResponse(
        categoryId: Long,
        pageIndex: Int,
        sort: String,
        orientation: String,
        filter: String,
        filterValue: String,
        priceFrom: Int,
        priceTo: Int
    ) = api.fetchCategoryDetailResponse(
        categoryId = categoryId,
        pageIndex = pageIndex,
        sort = sort,
        orientation = orientation,
        filter = filter,
        filterValue = filterValue,
        priceFrom = priceFrom,
        priceTo = priceTo
    )

    override fun register(
        firstName: String,
        secondName: String,
        email: String,
        password: String,
        phone: String,
    ) = api.register(
        firstName = firstName,
        secondName = secondName,
        email = email,
        password = password,
        phone = phone
    ).flatMap { Single.just(it.parseRegisterResponse()) }

    override fun login(
        email: String,
        password: String
    ) = api.login(
        email = email,
        password = password
    ).flatMap { Single.just(it.parseLoginResponse()) }

    override fun fetchUserData(
        userId: Long
    ) = api.fetchUserData(
        userId = userId
    ).flatMap { Single.just(it.parseUserDataResponse()) }

}