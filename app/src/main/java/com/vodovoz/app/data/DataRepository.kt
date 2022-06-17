package com.vodovoz.app.data

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.model.common.*
import com.vodovoz.app.data.model.features.CountryBundleEntity
import com.vodovoz.app.data.parser.response.CategoryDetailResponseJsonParser
import com.vodovoz.app.data.remote.ProductListPagingSource
import com.vodovoz.app.data.remote.RemoteDataSource
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject

class DataRepository(
    private val remoteData: RemoteDataSource,
    private val localData: LocalDataSource,
    private val categoryDetailResponseJsonParser: CategoryDetailResponseJsonParser
) {

    companion object {
        const val DISCOUNT_PRODUCT_TYPE = "discount"
        const val NOVELTIES_PRODUCT_TYPE = "novelties"
        const val TOP_PRODUCT = "top_product"
        const val BOTTOM_PRODUCT = "bottom_product"
        const val VIEWED_PRODUCT = "viewed_product"

        const val MAIN_BANNER_TYPE = "main_banner"
        const val SECONDARY_BANNER_TYPE = "secondary_banner"
    }

    //HomePage
    val mainBannerSubject: BehaviorSubject<ResponseEntity<List<BannerEntity>>> = BehaviorSubject.create()
    val historySubject: BehaviorSubject<ResponseEntity<List<HistoryEntity>>> = BehaviorSubject.create()
    val popularSubject: BehaviorSubject<ResponseEntity<List<CategoryEntity>>> = BehaviorSubject.create()
    val discountSubject: BehaviorSubject<ResponseEntity<List<CategoryDetailEntity>>> = BehaviorSubject.create()
    val secondaryBannerSubject: BehaviorSubject<ResponseEntity<List<BannerEntity>>> = BehaviorSubject.create()
    val topCategorySubject: BehaviorSubject<ResponseEntity<List<CategoryDetailEntity>>> = BehaviorSubject.create()
    val noveltiesSubject: BehaviorSubject<ResponseEntity<List<CategoryDetailEntity>>> = BehaviorSubject.create()
    val bottomCategorySubject: BehaviorSubject<ResponseEntity<List<CategoryDetailEntity>>> = BehaviorSubject.create()
    val brandSubject: BehaviorSubject<ResponseEntity<List<BrandEntity>>> = BehaviorSubject.create()
    val countrySubject: BehaviorSubject<ResponseEntity<CountryBundleEntity>> = BehaviorSubject.create()
    val commentSubject: BehaviorSubject<ResponseEntity<List<CommentEntity>>> = BehaviorSubject.create()
    val promotionSubject: BehaviorSubject<ResponseEntity<List<PromotionEntity>>> = BehaviorSubject.create()
    val viewedSubject: BehaviorSubject<ResponseEntity<List<CategoryDetailEntity>>> = BehaviorSubject.create()

    //CatalogPage
    val catalogSubject: BehaviorSubject<ResponseEntity<List<CategoryEntity>>> = BehaviorSubject.create()

    fun getSliderSubjectByType(sliderType: String) = when(sliderType) {
        DISCOUNT_PRODUCT_TYPE -> discountSubject
        TOP_PRODUCT -> topCategorySubject
        BOTTOM_PRODUCT -> bottomCategorySubject
        NOVELTIES_PRODUCT_TYPE -> noveltiesSubject
        VIEWED_PRODUCT -> viewedSubject
        else -> throw Exception()
    }

    fun getBannerSubjectByType(
        bannerType: String
    ) = when(bannerType) {
        MAIN_BANNER_TYPE -> mainBannerSubject
        SECONDARY_BANNER_TYPE -> secondaryBannerSubject
        else -> throw java.lang.Exception()
    }

    //Home Page
    fun updateMainBannerList() = remoteData
        .fetchMainBannerResponse()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess { response -> mainBannerSubject.onNext(response) }

    fun updateSecondaryBannerList() = remoteData
        .fetchSecondaryBannerResponse()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess { response -> secondaryBannerSubject.onNext(response) }

    fun updateHistoryList() = remoteData
        .fetchHistoryResponse()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess { response -> historySubject.onNext(response) }

    fun updateBrandList() = remoteData
        .fetchBrandResponse()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess { response -> brandSubject.onNext(response) }

    fun updateCountryList() = remoteData
        .fetchCountrySlider()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess { response -> countrySubject.onNext(response) }

    fun updateDiscountProductList() = remoteData
        .fetchDiscountCategoryResponse()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess { response -> discountSubject.onNext(response) }

    fun updateNoveltiesProductList() = remoteData
        .fetchNoveltiesCategoryResponse()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess { response -> noveltiesSubject.onNext(response) }

    fun updatePopularSectionList() = remoteData
        .fetchPopularResponse()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess { response -> popularSubject.onNext(response) }

    fun updateDoubleCategory() = remoteData
        .fetchDoubleCategoryResponse()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess { response ->
            when(response) {
                is ResponseEntity.Success -> {
                    response.data?.let {
                        topCategorySubject.onNext(ResponseEntity.Success(response.data.first()))
                        bottomCategorySubject.onNext(ResponseEntity.Success(response.data.last()))
                    }
                }
                is ResponseEntity.Error -> {
                    topCategorySubject.onNext(ResponseEntity.Error())
                    bottomCategorySubject.onNext(ResponseEntity.Error())
                }
            }
        }

    fun updateCommentList() = remoteData
        .fetchCommentResponse()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess { response -> commentSubject.onNext(response)}

    fun updatePromotionsList() = remoteData
        .fetchPromotionSlider()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess { response -> promotionSubject.onNext(response)}

    fun fetchOrderSlider(userId: Long) = remoteData.fetchOrderSlider(userId = userId)
    fun fetchViewedProductSlider(userId: Long) = remoteData
        .fetchViewedProductSlider(userId = userId)
        .doOnSubscribe { Log.i(LogSettings.ID_LOG, "ID == $userId") }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess { response -> viewedSubject.onNext(response)}

    //Promotion
    fun fetchPromotionDetail(promotionId: Long) = remoteData.fetchPromotionDetail(promotionId = promotionId)
    fun fetchAllPromotions(filterId: Long) = remoteData.fetchAllPromotions(filterId = filterId)

    //Catalog
    fun fetchCategoryDetailById(
        categoryId: Long,
        sort: String,
        orientation: String,
        filter: String,
        filterValue: String,
        priceFrom: Int,
        priceTo: Int
    ) = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            ProductListPagingSource(
                categoryId = categoryId,
                sort = sort,
                orientation = orientation,
                filter = filter,
                filterValue = filterValue,
                priceFrom = priceFrom,
                priceTo = priceTo,
                remoteData = remoteData,
                categoryDetailResponseJsonParser = categoryDetailResponseJsonParser
            )
        }
    ).flow

    fun fetchCategoryHeader(categoryId: Long) = remoteData.fetchCategoryHeader(categoryId = categoryId)
    fun fetchFilterBundle(categoryId: Long) = remoteData.fetchFilterBundleResponse(categoryId = categoryId)
    fun fetchProductDetail(productId: Long) = remoteData.fetchProductDetailResponse(productId = productId)
    fun fetchPaginatedMaybeLikeProductList(pageIndex: Int) =
        remoteData.fetchPaginatedMaybeLikeProductListResponse(pageIndex = pageIndex)
    fun fetchPaginatedBrandProductList(
        productId: Long,
        brandId: Long,
        pageIndex: Int
    ) = remoteData.fetchPaginatedBrandProductListResponse(
        productId = productId,
        brandId = brandId,
        pageIndex = pageIndex
    )
    fun fetchConcreteFilterResponse(
        categoryId: Long,
        filterCode: String
    ) = remoteData.fetchConcreteFilterResponse(categoryId = categoryId, filterCode = filterCode)

    fun updateCatalog() = remoteData
        .fetchCatalogResponse()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess { response -> catalogSubject.onNext(response) }

    //Profile
    fun register(
        firstName: String,
        secondName: String,
        email: String,
        password: String,
        phone: String,
    ) = remoteData.register(
        firstName = firstName,
        secondName = secondName,
        email = email,
        password = password,
        phone = phone
    ).doOnSuccess { response ->
        if (response is ResponseEntity.Success) updateUserId(response.data!!)
    }

    fun login(
        email: String,
        password: String
    ) = remoteData.login(
        email = email,
        password = password
    ).doOnSuccess { response ->
        if (response is ResponseEntity.Success) {
            updateLastLoginData(LastLoginDataEntity(
                email = email,
                password = password
            ))
            updateUserId(response.data!!)
        }
    }.flatMap { response ->
        val newResponse = when(response) {
            is ResponseEntity.Success -> ResponseEntity.Success(true)
            is ResponseEntity.Error -> ResponseEntity.Error(response.errorMessage)
        }
        Single.just(newResponse)
    }

    fun logout() = localData.removeUserId()

    fun fetchUserData(
        userId: Long
    ) = remoteData.fetchUserData(
        userId = userId
    )

    fun fetchLastLoginData() = localData.fetchLastLoginData()
    fun updateLastLoginData(lastLoginDataEntity: LastLoginDataEntity) =
        localData.updateLastLoginData(lastLoginDataEntity)

    fun updateUserId(userId: Long) = localData.updateUserId(userId)
    fun fetchUserId() = localData.fetchUserId()

    fun isAlreadyLogin() = localData.isAlreadyLogin()

}
