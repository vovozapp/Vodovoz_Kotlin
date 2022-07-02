package com.vodovoz.app.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.model.common.LastLoginDataEntity
import com.vodovoz.app.data.model.common.OrderEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.features.CartBundleEntity
import com.vodovoz.app.data.paging.ProductsPagingSource
import com.vodovoz.app.data.paging.source.*
import com.vodovoz.app.data.remote.RemoteDataSource
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.flow.map

class DataRepository(
    private val remoteData: RemoteDataSource,
    private val localData: LocalDataSource,
) {

    fun fetchPopupNews() = remoteData.fetchPopupNews(userId = localData.fetchUserId())

    fun fetchCart(): Single<ResponseEntity<CartBundleEntity>> = remoteData
        .fetchCart()
        .doOnSuccess { response ->
            if (response is ResponseEntity.Success) {
                response.data.let { cartBundleEntity ->
                    localData.clearCart()
                    cartBundleEntity.availableProductEntityList.forEach { productUI ->
                        localData.changeProductQuantityInCart(
                            productId = productUI.id,
                            quantity = productUI.cartQuantity
                        )
                    }
                }
            }
        }

    fun clearCart(): Single<ResponseEntity<Boolean>> = remoteData
        .clearCart()
        .doFinally { localData.clearCart() }

    fun fetchBrandHeader(brandId: Long) = remoteData.fetchBrandHeader(brandId = brandId)

    fun fetchCountryHeader(countryId: Long) = remoteData.fetchCountryHeader(countryId = countryId)

    fun fetchAdvertisingBannersSlider() = remoteData.fetchAdvertisingBannersSlider()

    fun fetchCategoryBannersSlider() = remoteData.fetchCategoryBannersSlider()

    fun fetchHistoriesSlider() = remoteData.fetchHistoriesSlider()

    fun fetchBrandsSlider() = remoteData.fetchBrandsSlider()

    fun fetchCountriesSlider() = remoteData.fetchCountriesSlider()

    fun fetchDiscountProductsSlider() = remoteData.fetchDiscountsSlider()

    fun fetchNoveltiesProductsSlider() = remoteData.fetchNoveltiesSlider()

    fun fetchPopularCategoriesSlider() = remoteData.fetchPopularSlider()

    fun fetchTopProductsSlider() = remoteData.fetchTopSlider()

    fun fetchBottomProductsSlider() = remoteData.fetchBottomSlider()

    fun fetchCommentsSlider() = remoteData.fetchCommentsSlider()

    fun fetchPromotionsSlider() = remoteData.fetchPromotionsSlider()

    fun fetchOrdersSlider(): Single<ResponseEntity<List<OrderEntity>>> = Single.create { emitter ->
        when(val userId = fetchUserId()) {
            null -> emitter.onSuccess(ResponseEntity.Hide())
            else -> {
                remoteData.fetchOrdersSlider(userId = userId).subscribeBy(
                    onSuccess = { response ->  emitter.onSuccess(response) },
                    onError = { throwable -> emitter.onError(throwable) }
                )
            }
        }
    }

    fun fetchViewedProductsSlider() = remoteData.fetchViewedProductsSlider(userId = fetchUserId())

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
    ).flow.map { pagingData ->
        val cart = fetchLocalCart()
        pagingData.map { product ->
            product.apply { cartQuantity = cart[product.id] ?: 0 }
        }
    }

    fun changeCart(
        productId: Long,
        quantity: Int
    ): Completable = Completable.create { emitter ->
        val cart = localData.fetchCart()
        val oldQuantity = cart[productId]
        if (oldQuantity == quantity) emitter.onComplete()

        when(oldQuantity) {
            null -> remoteData.addProductToCart(
                productId = productId,
                quantity = quantity
            )
            else -> remoteData.changeProductsQuantityInCart(
                productId = productId,
                quantity = quantity
            )
        }.subscribeBy(
            onSuccess = { response ->
                when(response) {
                    is ResponseEntity.Success -> {
                        localData.changeProductQuantityInCart(
                            productId = productId,
                            quantity = quantity
                        )
                        emitter.onComplete()
                    }
                    is ResponseEntity.Error -> {
                        emitter.onError(NullPointerException(response.errorMessage))
                    }
                }
            },
            onError = { throwable -> emitter.onError(throwable) }
        )
    }

    fun changeProductQuantityInCart(
        productId: Long,
        quantity: Int
    ): Single<ResponseEntity<Boolean>> = remoteData.changeProductsQuantityInCart(
        productId = productId,
        quantity = quantity
    ).doFinally {
        localData.changeProductQuantityInCart(
            productId = productId,
            quantity = quantity
        )
    }

    fun addProductToCart(
        productId: Long,
        quantity: Int
    ): Single<ResponseEntity<Boolean>> = remoteData.addProductToCart(
        productId = productId,
        quantity = quantity
    ).doFinally {
        localData.changeProductQuantityInCart(
            productId = productId,
            quantity = quantity
        )
    }

    fun fetchLocalCart() = localData.fetchCart()

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
            localData.updateUserId(response.data!!)
        }
    }.flatMap { response ->
        val newResponse = when(response) {
            is ResponseEntity.Success -> ResponseEntity.Success(true)
            is ResponseEntity.Error -> ResponseEntity.Error(response.errorMessage)
            is ResponseEntity.Hide -> ResponseEntity.Error("Hide content")
        }
        Single.just(newResponse)
    }

    fun logout(): Single<String> = remoteData
        .fetchCookie()
        .doOnSubscribe {
            localData.removeUserId()
            localData.removeCookieSessionId()
        }

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
