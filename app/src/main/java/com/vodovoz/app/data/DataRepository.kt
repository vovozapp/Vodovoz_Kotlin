package com.vodovoz.app.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import com.vodovoz.app.data.LocalSyncExtensions.syncCartQuantity
import com.vodovoz.app.data.LocalSyncExtensions.syncFavoriteProducts
import com.vodovoz.app.data.LocalSyncExtensions.syncFavoriteStatus
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.model.common.*
import com.vodovoz.app.data.model.features.CartBundleEntity
import com.vodovoz.app.data.paging.comments.CommentsPagingSource
import com.vodovoz.app.data.paging.orders.OrdersPagingSource
import com.vodovoz.app.data.paging.products.ProductsPagingSource
import com.vodovoz.app.data.paging.products.source.*
import com.vodovoz.app.data.remote.RemoteDataSource
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.flow.map
import java.lang.Exception

class DataRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
) {

    fun fetchPopupNews() = remoteDataSource.fetchPopupNews(userId = localDataSource.fetchUserId())

    fun fetchCart(): Single<ResponseEntity<CartBundleEntity>> = remoteDataSource
        .fetchCart()
        .doOnSuccess { response ->
            if (response is ResponseEntity.Success) {
                response.data.let { cartBundleEntity ->
                    localDataSource.clearCart()
                    cartBundleEntity.availableProductEntityList.forEach { productUI ->
                        localDataSource.changeProductQuantityInCart(
                            productId = productUI.id,
                            quantity = productUI.cartQuantity
                        )
                    }
                    cartBundleEntity.availableProductEntityList.syncFavoriteProducts(localDataSource)
                    cartBundleEntity.notAvailableProductEntityList.syncFavoriteProducts(localDataSource)
                }
            }
        }

    fun clearCart(): Single<ResponseEntity<Boolean>> = remoteDataSource
        .clearCart()
        .doFinally { localDataSource.clearCart() }

    fun fetchBrandHeader(brandId: Long) = remoteDataSource.fetchBrandHeader(brandId = brandId)

    fun fetchMatchesQueries(query: String?) = remoteDataSource.fetchMatchesQueries(query = query)

    fun fetchCountryHeader(countryId: Long) = remoteDataSource.fetchCountryHeader(countryId = countryId)

    fun fetchAdvertisingBannersSlider() = remoteDataSource.fetchAdvertisingBannersSlider()

    fun fetchCategoryBannersSlider() = remoteDataSource.fetchCategoryBannersSlider()

    fun fetchHistoriesSlider() = remoteDataSource.fetchHistoriesSlider()

    fun fetchBrandsSlider() = remoteDataSource.fetchBrandsSlider()

    fun fetchCountriesSlider() = remoteDataSource.fetchCountriesSlider()

    fun fetchDiscountProductsSlider(): Single<ResponseEntity<List<CategoryDetailEntity>>> = remoteDataSource
        .fetchDiscountsSlider()
        .doOnSuccess { response ->
            if (response is ResponseEntity.Success) {
                response.data.forEach { categoryDetailEntity ->
                    categoryDetailEntity.productEntityList.syncFavoriteProducts(localDataSource)
                    categoryDetailEntity.productEntityList.syncCartQuantity(localDataSource)
                }
            }
        }

    fun sendCommentAboutShop(
        comment: String?,
        rating: Int?
    ) = remoteDataSource.fetchSendCommentAboutShopResponse(
        userId = fetchUserId()!!,
        comment = comment,
        rating = rating
    )

    fun fetchNoveltiesProductsSlider(): Single<ResponseEntity<List<CategoryDetailEntity>>> = remoteDataSource
        .fetchNoveltiesSlider()
        .doOnSuccess { response ->
            if (response is ResponseEntity.Success) {
                response.data.forEach { categoryDetailEntity ->
                    categoryDetailEntity.productEntityList.syncFavoriteProducts(localDataSource)
                    categoryDetailEntity.productEntityList.syncCartQuantity(localDataSource)
                }
            }
        }

    fun fetchPopularCategoriesSlider() = remoteDataSource.fetchPopularSlider()

    fun fetchTopProductsSlider(): Single<ResponseEntity<List<CategoryDetailEntity>>> = remoteDataSource
        .fetchTopSlider()
        .doOnSuccess { response ->
            if (response is ResponseEntity.Success) {
                response.data.forEach { categoryDetailEntity ->
                    categoryDetailEntity.productEntityList.syncFavoriteProducts(localDataSource)
                    categoryDetailEntity.productEntityList.syncCartQuantity(localDataSource)
                }
            }
        }

    fun fetchBottomProductsSlider(): Single<ResponseEntity<List<CategoryDetailEntity>>> = remoteDataSource
        .fetchBottomSlider()
        .doOnSuccess { response ->
            if (response is ResponseEntity.Success) {
                response.data.forEach { categoryDetailEntity ->
                    categoryDetailEntity.productEntityList.syncFavoriteProducts(localDataSource)
                    categoryDetailEntity.productEntityList.syncCartQuantity(localDataSource)
                }
            }
        }

    fun fetchCommentsSlider() = remoteDataSource.fetchCommentsSlider()

    fun fetchPromotionsSlider(): Single<ResponseEntity<List<PromotionEntity>>> = remoteDataSource
        .fetchPromotionsSlider()
        .doOnSuccess { response ->
            if (response is ResponseEntity.Success) {
                response.data.forEach { promotionEntity ->
                    promotionEntity.productEntityList.syncFavoriteProducts(localDataSource)
                    promotionEntity.productEntityList.syncCartQuantity(localDataSource)
                }
            }
        }

    fun fetchOrdersSlider(): Single<ResponseEntity<List<OrderEntity>>> = Single.create { emitter ->
        when(val userId = fetchUserId()) {
            null -> emitter.onSuccess(ResponseEntity.Hide())
            else -> {
                remoteDataSource.fetchOrdersSlider(userId = userId).subscribeBy(
                    onSuccess = { response ->  emitter.onSuccess(response) },
                    onError = { throwable -> emitter.onError(throwable) }
                )
            }
        }
    }

    fun fetchViewedProductsSlider(): Single<ResponseEntity<List<CategoryDetailEntity>>> = remoteDataSource
        .fetchViewedProductsSlider(userId = fetchUserId())
        .doOnSuccess { response ->
            if (response is ResponseEntity.Success) {
                response.data.forEach { categoryDetailEntity ->
                    categoryDetailEntity.productEntityList.syncFavoriteProducts(localDataSource)
                    categoryDetailEntity.productEntityList.syncCartQuantity(localDataSource)
                }
            }
        }

    fun fetchPromotionDetails(promotionId: Long): Single<ResponseEntity<PromotionDetailEntity>> = remoteDataSource
        .fetchPromotionDetails(promotionId = promotionId)
        .doOnSuccess { response ->
            if (response is ResponseEntity.Success) {
                response.data.promotionCategoryDetailEntity?.productEntityList.syncFavoriteProducts(localDataSource)
                response.data.promotionCategoryDetailEntity?.productEntityList.syncCartQuantity(localDataSource)
                response.data.forYouCategoryDetailEntity.productEntityList.syncFavoriteProducts(localDataSource)
                response.data.forYouCategoryDetailEntity.productEntityList.syncCartQuantity(localDataSource)
            }
        }

    fun fetchAllPromotions(filterId: Long) = remoteDataSource.fetchAllPromotions(filterId = filterId)

    fun fetchAllBrands(brandIdList: List<Long> = listOf()) = remoteDataSource.fetchAllBrands(brandIdList = brandIdList)

    fun fetchPromotionsByBanner(categoryId: Long) = remoteDataSource.fetchPromotionsByBanner(categoryId = categoryId)

    fun fetchProductsByBanner(categoryId: Long): Single<ResponseEntity<List<ProductEntity>>> = remoteDataSource
        .fetchProductsByBanner(categoryId = categoryId)
        .doOnSuccess { response ->
            if (response is ResponseEntity.Success) {
                response.data.syncFavoriteProducts(localDataSource)
                response.data.syncCartQuantity(localDataSource)
            }
        }

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
                remoteDataSource = remoteDataSource,
                localDataSource = localDataSource
            ))
        }
    ).flow

    fun fetchProductsByQueryHeader(query: String?) = remoteDataSource.fetchProductsByQueryHeader(query)

    fun fetchSearchDefaultData() = remoteDataSource.fetchSearchDefaultData()

    fun fetchProductsByQuery(
        query: String?,
        categoryId: Long?,
        sort: String?,
        orientation: String?,
    ) = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            ProductsPagingSource(ProductsByQuerySource(
                query = query,
                categoryId = categoryId,
                sort = sort,
                orientation = orientation,
                remoteDataSource = remoteDataSource,
                localDataSource = localDataSource
            ))
        }
    ).flow

    //Отправить отзыв о продукте
    fun sendCommentAboutProduct(
        productId: Long,
        rating: Int,
        comment: String
    ) = remoteDataSource.sendCommentAboutProduct(
        productId = productId,
        rating = rating,
        comment = comment,
        userId = fetchUserId()!!
    )

    //Все отзывы о продукте
    fun fetchAllCommentsByProduct(
        productId: Long
    ) = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            CommentsPagingSource(
                productId = productId,
                remoteDataSource = remoteDataSource
            )
        }
    ).flow

    fun changeCart(
        productId: Long,
        quantity: Int
    ): Completable = Completable.create { emitter ->
        val cart = localDataSource.fetchCart()
        val oldQuantity = cart[productId]
        if (oldQuantity == quantity) emitter.onComplete()

        when(oldQuantity) {
            null -> remoteDataSource.addProductToCart(
                productId = productId,
                quantity = quantity
            )
            else -> remoteDataSource.changeProductsQuantityInCart(
                productId = productId,
                quantity = quantity
            )
        }.subscribeBy(
            onSuccess = { response ->
                when(response) {
                    is ResponseEntity.Success -> {
                        localDataSource.changeProductQuantityInCart(
                            productId = productId,
                            quantity = quantity
                        )
                        emitter.onComplete()
                    }
                    is ResponseEntity.Error -> {
                        emitter.onError(NullPointerException(response.errorMessage))
                    }
                    is ResponseEntity.Hide -> {
                        emitter.onError(Exception("Hide"))
                    }
                }
            },
            onError = { throwable -> emitter.onError(throwable) }
        )
    }

    fun fetchAboutServices() = remoteDataSource.fetchAboutServices()

    fun fetchCategoryHeader(categoryId: Long) = remoteDataSource.fetchCategoryHeader(categoryId = categoryId)

    fun fetchAllFiltersByCategory(categoryId: Long) = remoteDataSource.fetchAllFiltersByCategory(categoryId = categoryId)

    fun fetchProductDetails(productId: Long): Single<ResponseEntity<ProductDetailsBundleEntity>> = remoteDataSource
        .fetchProductDetails(productId = productId)
        .doOnSuccess { response ->
            if(response is ResponseEntity.Success) {
                response.data.productDetailEntity.syncCartQuantity(localDataSource)
                response.data.productDetailEntity.syncFavoriteStatus(localDataSource)
                response.data.buyWithProductEntityList.syncCartQuantity(localDataSource)
                response.data.buyWithProductEntityList.syncFavoriteProducts(localDataSource)
                response.data.maybeLikeProductEntityList.syncCartQuantity(localDataSource)
                response.data.maybeLikeProductEntityList.syncFavoriteProducts(localDataSource)
                response.data.recommendProductEntityList.syncCartQuantity(localDataSource)
                response.data.recommendProductEntityList.syncFavoriteProducts(localDataSource)
            }
        }

    fun fetchMaybeLikeProducts(page: Int) = remoteDataSource
        .fetchMaybeLikeProducts(page = page)
        .doOnSuccess { response ->
            if (response is ResponseEntity.Success) {
                response.data.productEntityList.syncCartQuantity(localDataSource)
                response.data.productEntityList.syncFavoriteProducts(localDataSource)
            }
        }

    fun fetchDiscountHeader() = remoteDataSource.fetchDiscountHeader()

    fun fetchNoveltiesHeader() = remoteDataSource.fetchNoveltiesHeader()

    fun fetchSliderHeader(categoryId: Long) = remoteDataSource.fetchSliderHeader(categoryId = categoryId)

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
                remoteDataSource = remoteDataSource,
                localDataSource = localDataSource
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
                orientation = orientation,
                sort = sort,
                remoteDataSource = remoteDataSource,
                localDataSource = localDataSource
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
                remoteDataSource = remoteDataSource,
                localDataSource = localDataSource
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
                remoteDataSource = remoteDataSource,
                localDataSource = localDataSource
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
                remoteDataSource = remoteDataSource,
                localDataSource = localDataSource
            ))
        }
    ).flow

    fun fetchSomeProductsByBrand(
        productId: Long,
        brandId: Long,
        page: Int
    ): Single<ResponseEntity<PaginatedProductListEntity>> = remoteDataSource.fetchSomeProductsByBrand(
        productId = productId,
        brandId = brandId,
        page = page
    ).doOnSuccess { response ->
        if (response is ResponseEntity.Success) {
            response.data.productEntityList.syncFavoriteProducts(localDataSource)
            response.data.productEntityList.syncCartQuantity(localDataSource)
        }
    }

    fun fetchProductFilterById(
        categoryId: Long,
        filterCode: String
    ) = remoteDataSource.fetchProductFilterById(categoryId = categoryId, filterCode = filterCode)

    fun fetchCatalog() = remoteDataSource.fetchCatalog()

    //Profile
    fun register(
        firstName: String,
        secondName: String,
        email: String,
        password: String,
        phone: String,
    ): Single<ResponseEntity<Long>> = remoteDataSource.register(
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
    ): Single<ResponseEntity<Boolean>> = remoteDataSource.login(
        email = email,
        password = password
    ).doOnSuccess { response ->
        if (response is ResponseEntity.Success) {
            updateLastLoginData(LastLoginDataEntity(
                email = email,
                password = password
            ))
            localDataSource.updateUserId(response.data)
        }

        //addToFavorite(localData.fetchAllFavoriteProductsOfDefaultUser()).subscribe()
    }.flatMap { response ->
        val newResponse = when(response) {
            is ResponseEntity.Success -> ResponseEntity.Success(true)
            is ResponseEntity.Error -> ResponseEntity.Error(response.errorMessage)
            is ResponseEntity.Hide -> ResponseEntity.Error("Hide content")
        }
        Single.just(newResponse)
    }

    fun logout(): Single<String> = remoteDataSource
        .fetchCookie()
        .doOnSubscribe {
            localDataSource.removeUserId()
            localDataSource.removeCookieSessionId()
        }

    fun fetchUserData(
        userId: Long
    ) = remoteDataSource.fetchUserData(
        userId = userId
    )

    fun fetchLastLoginData() = localDataSource.fetchLastLoginData()

    fun updateLastLoginData(
        lastLoginDataEntity: LastLoginDataEntity
    ) = localDataSource.updateLastLoginData(lastLoginDataEntity)

    fun updateUserId(userId: Long) = localDataSource.updateUserId(userId)

    fun fetchUserId() = localDataSource.fetchUserId()

    fun isAlreadyLogin() = localDataSource.isAlreadyLogin()

    fun addToFavorite(
        productId: Long
    ) = addToFavorite(listOf(productId))

    fun addToFavorite(
        productIdList: List<Long>
    ): Single<String> = Single.create { emitter ->
        when(isAlreadyLogin()) {
            false -> {
                localDataSource.changeFavoriteStatus(
                    pairList = mutableListOf<Pair<Long, Boolean>>().apply {
                        productIdList.forEach { add(Pair(it, true)) }
                    }.toList()
                )
                emitter.onSuccess("Продукт доавблен в избранное")
            }
            true -> {
                remoteDataSource.addToFavorite(
                    productIdList = productIdList,
                    userId = fetchUserId()!!
                ).subscribeBy(
                    onSuccess = { response ->
                        when (response) {
                            is ResponseEntity.Success -> {
                                localDataSource.changeFavoriteStatus(
                                    pairList = mutableListOf<Pair<Long, Boolean>>().apply {
                                        productIdList.forEach { add(Pair(it, true)) }
                                    }.toList()
                                )
                                emitter.onSuccess(response.data)
                            }
                            is ResponseEntity.Error -> emitter.onError(Exception(response.errorMessage))
                            is ResponseEntity.Hide -> emitter.onError(Exception("Ошибка!"))
                        }
                    },
                    onError = { throwable -> emitter.onError(throwable) }
                )
            }
        }
    }

    fun removeFromFavorite(
        productId: Long
    ): Single<String> = Single.create { emitter ->
        when(isAlreadyLogin()) {
            false -> {
                localDataSource.changeFavoriteStatus(listOf(Pair(productId, false)))
                emitter.onSuccess("Продукт удален из избранного")
            }
            true -> {
                remoteDataSource.removeFromFavorite(
                    productId = productId,
                    userId = fetchUserId()!!
                ).subscribeBy(
                    onSuccess = { response ->
                        when (response) {
                            is ResponseEntity.Success -> {
                                localDataSource.changeFavoriteStatus(listOf(Pair(productId, false)))
                                emitter.onSuccess(response.data)
                            }
                            is ResponseEntity.Error -> emitter.onError(Exception(response.errorMessage))
                            is ResponseEntity.Hide -> emitter.onError(Exception("Ошибка!"))
                        }
                    },
                    onError = { throwable -> emitter.onError(throwable) }
                )
            }
        }
    }

    fun fetchFavoriteProductsHeaderBundle() = remoteDataSource.fetchFavoriteProductsHeaderBundleResponse(
        userId = fetchUserId(),
        productIdListStr = when(isAlreadyLogin()) {
            true -> null
            false -> StringBuilder().apply {
                localDataSource.fetchAllFavoriteProducts().forEach {
                    append(it).append(",")
                }
            }.toString()
        }
    )

    fun fetchFavoriteProducts(
        categoryId: Long?,
        sort: String?,
        orientation: String?,
        isAvailable: Boolean?
    ) = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            ProductsPagingSource(ProductsFavoriteSource(
                userId = fetchUserId(),
                productIdListStr = StringBuilder().apply {
                    localDataSource.fetchAllFavoriteProducts().forEach {
                        append(it).append(",")
                    }
                }.toString(),
                categoryId = categoryId,
                sort = sort,
                orientation = orientation,
                isAvailable = isAvailable,
                remoteDataSource = remoteDataSource,
                localDataSource = localDataSource
            ))
        }
    ).flow

    fun fetchPastPurchasesProducts(
        categoryId: Long?,
        sort: String?,
        orientation: String?,
        isAvailable: Boolean?
    ) = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            ProductsPagingSource(PastPurchasesProductsSource(
                userId = fetchUserId(),
                categoryId = categoryId,
                sort = sort,
                orientation = orientation,
                isAvailable = isAvailable,
                remoteDataSource = remoteDataSource,
                localDataSource = localDataSource
            ))
        }
    ).flow

    fun fetchPastPurchasesHeader() = remoteDataSource.fetchPastPurchasesHeader(fetchUserId()!!)

    fun fetchDeliveryZonesBundle() = remoteDataSource.fetchDeliveryZonesResponse()

    fun fetchAddressByGeocode(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ) = remoteDataSource.fetchAddressByGeocodeResponse(
        latitude = latitude,
        longitude = longitude,
        apiKey = apiKey
    )

    fun fetchAddressesSaved(
        type: Int?
    ) = remoteDataSource.fetchAddressesSaved(
        userId = fetchUserId()!!,
        type = type
    )

    fun addAddress(
        locality: String?,
        street: String?,
        house: String?,
        entrance: String?,
        floor: String?,
        office: String?,
        comment: String?,
        type: Int?
    ) = remoteDataSource.addAddress(
        locality = locality,
        street = street,
        house = house,
        entrance = entrance,
        floor = floor,
        office = office,
        comment = comment,
        type = type,
        userId = fetchUserId()
    )

    fun updateAddress(
        addressId: Long?,
        locality: String?,
        street: String?,
        house: String?,
        entrance: String?,
        floor: String?,
        office: String?,
        comment: String?,
        type: Int?
    ) = remoteDataSource.updateAddress(
        addressId = addressId,
        locality = locality,
        street = street,
        house = house,
        entrance = entrance,
        floor = floor,
        office = office,
        comment = comment,
        type = type,
        userId = fetchUserId()
    )

    fun deleteAddress(
        addressId: Long?
    ) = remoteDataSource.deleteAddress(
        addressId = addressId,
        userId = fetchUserId()!!
    )

    fun fetchAllOrders(
        appVersion: String?,
        orderId: Long?,
        status: String?
    ) = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            OrdersPagingSource(
                userId = fetchUserId(),
                appVersion = appVersion,
                orderId = orderId,
                status = status,
                remoteDataSource = remoteDataSource
            )
        }
    ).flow.map { pagingData ->
        pagingData.map { orderEntity ->
            orderEntity.productEntityList.syncCartQuantity(localDataSource)
            orderEntity.productEntityList.syncFavoriteProducts(localDataSource)
        }
        pagingData
    }

    fun fetchOrderDetails(
        orderId: Long?,
        appVersion: String?
    ) = remoteDataSource.fetchOrderDetailsResponse(
        userId = fetchUserId()!!,
        orderId = orderId,
        appVersion = appVersion
    ).doOnSuccess { response ->
        if (response is ResponseEntity.Success) {
            response.data.productEntityList.syncCartQuantity(localDataSource)
            response.data.productEntityList.syncFavoriteProducts(localDataSource)
        }
    }

    fun fetchPersonalProducts(page: Int = 1) = remoteDataSource.fetchPersonalProducts(
        userId = fetchUserId()!!,
        page = page
    ).doOnSuccess { response ->
        if (response is ResponseEntity.Success) {
            response.data.productEntityList.syncCartQuantity(localDataSource)
            response.data.productEntityList.syncFavoriteProducts(localDataSource)
        }
    }

    fun updateUserData(
        firstName: String?,
        secondName: String?,
        password: String?,
        phone: String?,
        sex: String?,
        birthday: String?,
        email: String?,
    ) = remoteDataSource.updateUserData(
        userId = fetchUserId()!!,
        secondName = secondName,
        firstName = firstName,
        password = password,
        phone = phone,
        sex = sex,
        birthday = birthday,
        email = email
    )

    fun fetchActivateDiscountCardInfo() = remoteDataSource.fetchActivateDiscountCardInfo(userId = fetchUserId()!!)
    fun activateDiscountCard(
        value: String?
    ) = remoteDataSource.activateDiscountCard(
        userId = fetchUserId(),
        value = value
    )

    fun recoverPassword(email: String?) = remoteDataSource.recoverPassword(email)

    fun clearSearchHistory() = localDataSource.clearSearchHistory()
    fun addQueryToHistory(query: String) = localDataSource.addQueryToHistory(query)
    fun fetchSearchHistory() = localDataSource.fetchSearchHistory()

    fun fetchQuestionnairesTypes() = remoteDataSource.fetchQuestionnairesTypes()
    fun fetchQuestionnaire(
        questionnaireType: String?
    ) = remoteDataSource.fetchQuestionnaire(
        questionnaireType = questionnaireType,
        userId = fetchUserId()
    )

    fun fetchContacts(appVersion: String?) = remoteDataSource.fetchContacts(appVersion)

    fun sendMail(
        name: String?,
        phone: String?,
        email: String?,
        comment: String?
    ) = remoteDataSource.sendMail(
        name = name,
        phone = phone,
        email = email,
        comment = comment
    )

    fun fetchServiceById(type: String?) = remoteDataSource.fetchServiceById(type)

    fun fetchFormForOrderService(
        type: String?
    ) = remoteDataSource.fetchFormForOrderService(
        type = type,
        userId = fetchUserId()!!
    )

    fun orderService(
        type: String?,
        value: String?
    ) = remoteDataSource.orderService(
        type = type,
        userId = fetchUserId()!!,
        value = value
    )

}
