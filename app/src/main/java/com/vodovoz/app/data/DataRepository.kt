package com.vodovoz.app.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.model.common.*
import com.vodovoz.app.data.paging.ProductsPagingSource
import com.vodovoz.app.data.paging.source.*
import com.vodovoz.app.data.remote.RemoteDataSource
import io.reactivex.rxjava3.core.Single

class DataRepository(
    private val remoteData: RemoteDataSource,
    private val localData: LocalDataSource,
) {

    fun fetchBrandHeader(brandId: Long) = remoteData.fetchBrandHeader(brandId = brandId)

    fun fetchCountryHeader(countryId: Long) = remoteData.fetchCountryHeader(countryId = countryId)

    fun fetchAdvertisingBannersSlider() = remoteData.fetchAdvertisingBannersSlider()

    fun fetchCategoryBannersSlider() = remoteData.fetchCategoryBannersSlider()

    fun fetchHistoriesSlider() = remoteData.fetchHistoriesSlider()

    fun fetchBrandsSlider() = remoteData.fetchBrandsSlider()

    fun fetchCountriesSlider() = remoteData.fetchCountriesSlider()

    fun fetchDiscountsSlider() = remoteData.fetchDiscountsSlider()

    fun fetchNoveltiesSlider() = remoteData.fetchNoveltiesSlider()

    fun fetchPopularSlider() = remoteData.fetchPopularSlider()

    fun fetchTopSlider() = remoteData.fetchTopSlider()

    fun fetchBottomSlider() = remoteData.fetchBottomSlider()

    fun fetchCommentsSlider() = remoteData.fetchCommentsSlider()

    fun fetchPromotionsSlider() = remoteData.fetchPromotionsSlider()

    fun fetchOrderSlider(userId: Long) = remoteData.fetchOrdersSlider(userId = userId)

    fun fetchViewedProductSlider(userId: Long?) = remoteData.fetchViewedProductsSlider(userId = userId)

    fun fetchPromotionDetails(promotionId: Long) = remoteData.fetchPromotionDetails(promotionId = promotionId)

    fun fetchAllPromotions(filterId: Long) = remoteData.fetchAllPromotions(filterId = filterId)

    fun fetchAllBrands(brandIdList: List<Long> = listOf()) = remoteData.fetchAllBrands(brandIdList = brandIdList)

    fun fetchPromotionsByBanner(categoryId: Long) = remoteData.fetchPromotionsByBanner(categoryId = categoryId)

    fun fetchProductsByBanner(categoryId: Long) = remoteData.fetchProductsByBanner(categoryId = categoryId)

    fun fetchProductsByCategory(
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
            ProductsPagingSource(ProductsByCategorySource(
                categoryId = categoryId,
                sort = sort,
                orientation = orientation,
                filter = filter,
                filterValue = filterValue,
                priceFrom = priceFrom,
                priceTo = priceTo,
                remoteData = remoteData
            ))
        }
    ).flow

    fun fetchCategoryHeader(categoryId: Long) = remoteData.fetchCategoryHeader(categoryId = categoryId)

    fun fetchAllFiltersByCategory(categoryId: Long) = remoteData.fetchAllFiltersByCategory(categoryId = categoryId)

    fun fetchProductDetails(productId: Long) = remoteData.fetchProductDetails(productId = productId)

    fun fetchMaybeLikeProducts(page: Int) = remoteData.fetchMaybeLikeProducts(page = page)

    fun fetchDiscountHeader() = remoteData.fetchDiscountHeader()

    fun fetchNoveltiesHeader() = remoteData.fetchNoveltiesHeader()

    fun fetchSliderHeader(categoryId: Long) = remoteData.fetchSliderHeader(categoryId = categoryId)

    fun fetchProductsBySlider(
        categoryId: Long?,
        sort: String?,
        orientation: String?
    ) = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            ProductsPagingSource(ProductsBySliderSource(
                categoryId = categoryId,
                sort = sort,
                orientation = orientation,
                remoteData = remoteData,
            ))
        }
    ).flow


    fun fetchProductsByBrand(
        brandId: Long?,
        code: String?,
        categoryId: Long?,
        sort: String?,
        orientation: String?
    ) = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            ProductsPagingSource(ProductsByBrandSource(
                brandId = brandId,
                code = code,
                categoryId = categoryId,
                remoteData = remoteData,
                orientation = orientation,
                sort = sort
            ))
        }
    ).flow

    fun fetchProductsByCountry(
        countryId: Long,
        sort: String?,
        orientation: String?,
        categoryId: Long?,
    ) = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            ProductsPagingSource(ProductsByCountrySource(
                countryId = countryId,
                sort = sort,
                orientation = orientation,
                categoryId = categoryId,
                remoteData = remoteData
            ))
        }
    ).flow

    fun fetchProductsDiscount(
        sort: String?,
        orientation: String?,
        categoryId: Long?,
    ) = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            ProductsPagingSource(ProductsDiscountSource(
                sort = sort,
                orientation = orientation,
                categoryId = categoryId,
                remoteData = remoteData
            ))
        }
    ).flow

    fun fetchProductsNovelties(
        sort: String?,
        orientation: String?,
        categoryId: Long?,
    ) = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            ProductsPagingSource(ProductsNoveltiesSource(
                sort = sort,
                orientation = orientation,
                categoryId = categoryId,
                remoteData = remoteData
            ))
        }
    ).flow

    fun fetchSomeProductsByBrand(
        productId: Long,
        brandId: Long,
        page: Int
    ) = remoteData.fetchSomeProductsByBrand(
        productId = productId,
        brandId = brandId,
        page = page
    )

    fun fetchProductFilterById(
        categoryId: Long,
        filterCode: String
    ) = remoteData.fetchProductFilterById(categoryId = categoryId, filterCode = filterCode)

    fun fetchCatalog() = remoteData.fetchCatalog()

    //Profile
    fun register(
        firstName: String,
        secondName: String,
        email: String,
        password: String,
        phone: String,
    ): Single<ResponseEntity<Long>> = remoteData.register(
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
    ): Single<ResponseEntity<Boolean>> = remoteData.login(
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

    fun updateLastLoginData(
        lastLoginDataEntity: LastLoginDataEntity
    ) = localData.updateLastLoginData(lastLoginDataEntity)

    fun updateUserId(userId: Long) = localData.updateUserId(userId)

    fun fetchUserId() = localData.fetchUserId()

    fun isAlreadyLogin() = localData.isAlreadyLogin()

}
