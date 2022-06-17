package com.vodovoz.app.data.remote

import com.vodovoz.app.data.model.common.*
import com.vodovoz.app.data.model.features.AllPromotionBundleEntity
import com.vodovoz.app.data.model.features.CountryBundleEntity
import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RemoteDataSource {

    //Страны
    //Информации о странах для слайдера на главной странице
    fun fetchCountrySlider(): Single<ResponseEntity<CountryBundleEntity>>

    //Главная информация о выбранной стране
    fun fetchCountryHeader(
        countryId: Long
    ): Single<ResponseEntity<CategoryEntity>>

    //Постраничная загрузка продуктов по для выбранной страны
    fun fetchProductsByCountry(
        countryId: Long,
        sort: String? = null,
        orientation: String? = null,
        page: Int? = null,
        categoryId: Long? = null
    ): Single<ResponseEntity<List<ProductEntity>>>

    //Акции
    //Информация о слайдере акций на главной странице
    fun fetchPromotionSlider(): Single<ResponseEntity<List<PromotionEntity>>>

    //Подробная информация об акции
    fun fetchPromotionDetail(
        promotionId: Long
    ): Single<ResponseEntity<PromotionDetailEntity>>

    //Информация о всех акциях
    fun fetchAllPromotions(
        filterId: Long
    ): Single<ResponseEntity<AllPromotionBundleEntity>>

    //HomePage
    fun fetchMainBannerResponse(): Single<ResponseEntity<List<BannerEntity>>>
    fun fetchSecondaryBannerResponse(): Single<ResponseEntity<List<BannerEntity>>>
    fun fetchHistoryResponse(): Single<ResponseEntity<List<HistoryEntity>>>
    fun fetchPopularResponse(): Single<ResponseEntity<List<CategoryEntity>>>
    fun fetchDiscountCategoryResponse(): Single<ResponseEntity<List<CategoryDetailEntity>>>
    fun fetchBrandResponse(): Single<ResponseEntity<List<BrandEntity>>>
    fun fetchNoveltiesCategoryResponse(): Single<ResponseEntity<List<CategoryDetailEntity>>>
    fun fetchDoubleCategoryResponse(): Single<ResponseEntity<List<List<CategoryDetailEntity>>>>
    fun fetchCommentResponse(): Single<ResponseEntity<List<CommentEntity>>>
    fun fetchOrderSlider(userId: Long): Single<ResponseEntity<List<OrderEntity>>>
    fun fetchViewedProductSlider(userId: Long): Single<ResponseEntity<List<CategoryDetailEntity>>>


    //CatalogPage
    fun fetchCatalogResponse(): Single<ResponseEntity<List<CategoryEntity>>>
    fun fetchCategoryHeader(categoryId: Long): Single<ResponseEntity<CategoryEntity>>
    fun fetchFilterBundleResponse(categoryId: Long): Single<ResponseEntity<FilterBundleEntity>>
    fun fetchProductDetailResponse(productId: Long): Single<ResponseEntity<ProductDetailBundleEntity>>
    fun fetchPaginatedMaybeLikeProductListResponse(pageIndex: Int): Single<ResponseEntity<PaginatedProductListEntity>>
    fun fetchPaginatedBrandProductListResponse(
        productId: Long,
        brandId: Long,
        pageIndex: Int
    ): Single<ResponseEntity<PaginatedProductListEntity>>
    fun fetchConcreteFilterResponse(
        categoryId: Long,
        filterCode: String,
    ): Single<ResponseEntity<List<FilterValueEntity>>>
    suspend fun fetchCategoryDetailResponse(
        categoryId: Long,
        pageIndex: Int,
        sort: String,
        orientation: String,
        filter: String,
        filterValue: String,
        priceFrom: Int,
        priceTo: Int
    ): Response<ResponseBody>

    //Profile
    fun register(
        firstName: String,
        secondName: String,
        email: String,
        password: String,
        phone: String
    ): Single<ResponseEntity<Long>>

    fun login(
        email: String,
        password: String
    ): Single<ResponseEntity<Long>>

    fun fetchUserData(userId: Long): Single<ResponseEntity<UserDataEntity>>

}