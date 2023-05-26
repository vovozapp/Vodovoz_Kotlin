package com.vodovoz.app.data

import androidx.core.net.toUri
import com.vodovoz.app.BuildConfig
import com.vodovoz.app.common.product.rating.RatingResponse
import com.vodovoz.app.core.network.ApiConfig
import com.vodovoz.app.data.config.ShippingAlertConfig
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.banner.AdvertisingBannersSliderResponseJsonParser.parseAdvertisingBannersSliderResponse
import com.vodovoz.app.data.parser.response.banner.CategoryBannersSliderResponseJsonParser.parseCategoryBannersSliderResponse
import com.vodovoz.app.data.parser.response.brand.BrandsSliderResponseJsonParser.parseBrandsSliderResponse
import com.vodovoz.app.data.parser.response.comment.CommentsSliderResponseJsonParser.parseCommentsSliderResponse
import com.vodovoz.app.data.parser.response.country.CountrySliderResponseJsonParser.parseCountriesSliderResponse
import com.vodovoz.app.data.parser.response.discount.DiscountSliderResponseParser.parseDiscountSliderResponse
import com.vodovoz.app.data.parser.response.doubleSlider.DoubleSliderResponseJsonParser.parseBottomSliderResponse
import com.vodovoz.app.data.parser.response.doubleSlider.DoubleSliderResponseJsonParser.parseTopSliderResponse
import com.vodovoz.app.data.parser.response.history.HistoriesSliderResponseJsonParser.parseHistoriesSliderResponse
import com.vodovoz.app.data.parser.response.novelties.NoveltiesSliderResponseParser.parseNoveltiesSliderResponse
import com.vodovoz.app.data.parser.response.order.OrderSliderResponseJsonParser.parseOrderSliderResponse
import com.vodovoz.app.data.parser.response.popular.PopularSliderResponseJsonParser.parsePopularSliderResponse
import com.vodovoz.app.data.parser.response.popupNews.PopupNewsResponseJsonParser.parsePopupNewsResponse
import com.vodovoz.app.data.parser.response.promotion.PromotionSliderResponseJsonParser.parsePromotionSliderResponse
import com.vodovoz.app.data.parser.response.viewed.ViewedProductSliderResponseJsonParser.parseViewedProductsSliderResponse
import com.vodovoz.app.feature.home.HomeFlowViewModel
import com.vodovoz.app.feature.home.viewholders.homebanners.HomeBanners
import com.vodovoz.app.feature.home.viewholders.homebrands.HomeBrands
import com.vodovoz.app.feature.home.viewholders.homecomments.HomeComments
import com.vodovoz.app.feature.home.viewholders.homecountries.HomeCountries
import com.vodovoz.app.feature.home.viewholders.homehistories.HomeHistories
import com.vodovoz.app.feature.home.viewholders.homeorders.HomeOrders
import com.vodovoz.app.feature.home.viewholders.homepopulars.HomePopulars
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts
import com.vodovoz.app.feature.home.viewholders.homeproductstabs.HomeProductsTabs
import com.vodovoz.app.feature.home.viewholders.homepromotions.HomePromotions
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitle
import com.vodovoz.app.feature.map.api.MapKitFlowApi
import com.vodovoz.app.feature.profile.notificationsettings.model.NotificationSettingsModel
import com.vodovoz.app.feature.search.qrcode.model.QrCodeModel
import com.vodovoz.app.mapper.BannerMapper.mapToUI
import com.vodovoz.app.mapper.BrandMapper.mapToUI
import com.vodovoz.app.mapper.CategoryDetailMapper.mapToUI
import com.vodovoz.app.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.mapper.CommentMapper.mapToUI
import com.vodovoz.app.mapper.CountriesSliderBundleMapper.mapToUI
import com.vodovoz.app.mapper.HistoryMapper.mapToUI
import com.vodovoz.app.mapper.OrderMapper.mapToUI
import com.vodovoz.app.mapper.PopupNewsMapper.mapToUI
import com.vodovoz.app.mapper.PromotionMapper.mapToUI
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderConfig
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.PopupNewsUI
import com.vodovoz.app.ui.model.custom.PromotionsSliderBundleUI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.File
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val api: MainApi,
    private val mapKitApi: MapKitFlowApi,
) {

    //Слайдер рекламных баннеров на главной странице
    suspend fun fetchAdvertisingBannersSlider(position: Int): List<HomeFlowViewModel.PositionItem> {
        return coroutineScope {
            val responseBody = api.fetchAdvBanners(action = "slayder")
            withContext(Dispatchers.Default) {
                val response = responseBody.parseAdvertisingBannersSliderResponse()
                if (response is ResponseEntity.Success) {
                    listOf(
                        HomeFlowViewModel.PositionItem(
                            position,
                            HomeBanners(position, response.data.mapToUI(), bannerRatio = 0.41)
                        )
                    )
                } else {
                    emptyList()
                }
            }
        }
    }

    //Слайдер историй на главное странице
    suspend fun fetchHistoriesSlider(
        positionTitle: Int,
        position: Int,
    ): List<HomeFlowViewModel.PositionItem> {
        return coroutineScope {
            val responseBody = api.fetchHistories(blockId = 12, action = "stories", platform = "android")
            withContext(Dispatchers.Default) {
                val response = responseBody.parseHistoriesSliderResponse()
                if (response is ResponseEntity.Success) {
                    listOf(
                        HomeFlowViewModel.PositionItem(
                            position,
                            HomeHistories(position, response.data.mapToUI())
                        ),
                        HomeFlowViewModel.PositionItem(
                            positionTitle,
                            HomeTitle(
                                id = positionTitle,
                                type = HomeTitle.HISTORIES_TITLE,
                                name = "Истории"
                            )
                        )
                    )
                } else {
                    emptyList()
                }
            }
        }
    }

    //Слайдер популярных разделов на главной странице
    suspend fun fetchPopularSlider(
        positionTitle: Int,
        position: Int,
    ): List<HomeFlowViewModel.PositionItem> {
        return coroutineScope {
            val responseBody = api.fetchPopulars(action = "popylrazdel")
            withContext(Dispatchers.Default) {
                val response = responseBody.parsePopularSliderResponse()
                if (response is ResponseEntity.Success) {
                    listOf(
                        HomeFlowViewModel.PositionItem(
                            position,
                            HomePopulars(position, response.data.mapToUI())
                        ),
                        HomeFlowViewModel.PositionItem(
                            positionTitle,
                            HomeTitle(
                                id = positionTitle,
                                type = HomeTitle.POPULARS_TITLE,
                                name = "Популярные разделы"
                            )
                        )
                    )
                } else {
                    emptyList()
                }
            }
        }
    }

    //Слайдер баннеров категорий на главной странице
    suspend fun fetchCategoryBannersSlider(position: Int): List<HomeFlowViewModel.PositionItem> {
        return coroutineScope {
            val responseBody = api.fetchCategoryBanners(action = "slayder", androidVersion = "1.4.84")
            withContext(Dispatchers.Default) {
                val response = responseBody.parseCategoryBannersSliderResponse()
                if (response is ResponseEntity.Success) {
                    listOf(
                        HomeFlowViewModel.PositionItem(
                            position,
                            HomeBanners(position, response.data.mapToUI(), bannerRatio = 0.5)
                        )
                    )
                } else {
                    emptyList()
                }
            }
        }
    }

    //Слайдер самых выгодных продуктов на главной странице
    suspend fun fetchDiscountsSlider(
        positionTitle: Int,
        position: Int,
    ): List<HomeFlowViewModel.PositionItem> {
        return coroutineScope {
            val responseBody = api.fetchNovelties(action = "specpredlosh")
            withContext(Dispatchers.Default) {
                val response = responseBody.parseDiscountSliderResponse()
                if (response is ResponseEntity.Success) {
                    val data = response.data.mapToUI()
                    listOf(
                        HomeFlowViewModel.PositionItem(
                            position,
                            fetchHomeProductsByType(data, HomeProducts.DISCOUNT, position)
                        ),
                        HomeFlowViewModel.PositionItem(
                            positionTitle,
                            HomeTitle(
                                id = positionTitle,
                                type = HomeTitle.DISCOUNT_TITLE,
                                name = "Самое выгодное",
                                showAll = true,
                                showAllName = "СМ.ВСЕ",
                                categoryProductsName = if (data.size == 1) {
                                    data.first().name
                                } else {
                                    ""
                                }
                            )
                        )
                    )
                } else {
                    emptyList()
                }
            }
        }
    }

    //Верхний слайдер на главной странице
    suspend fun fetchTopSlider(
        positionTab: Int,
        position: Int,
    ): List<HomeFlowViewModel.PositionItem> {
        return coroutineScope {
            val responseBody = api.fetchDoubleSlider(action = "topglav", arg = "new")
            withContext(Dispatchers.Default) {
                val response = responseBody.parseTopSliderResponse()
                if (response is ResponseEntity.Success) {
                    val data = response.data.mapToUI()
                    listOf(
                        HomeFlowViewModel.PositionItem(
                            position,
                            fetchHomeProductsByType(
                                response.data.mapToUI(),
                                HomeProducts.TOP_PROD,
                                position
                            )
                        ),
                        HomeFlowViewModel.PositionItem(
                            positionTab,
                            HomeProductsTabs(id = positionTab, data.mapIndexed { index, cat ->
                                if (index == 0) {
                                    cat.copy(isSelected = true, position = positionTab)
                                } else {
                                    cat.copy(isSelected = false, position = positionTab)
                                }
                            })
                        )
                    )
                } else {
                    emptyList()
                }
            }
        }
    }

    //Информация о слайдере заказов на главной странице

    suspend fun fetchOrdersSlider(userId: Long): ResponseBody {
        return api.fetchOrderSlider(userId)
    }

    suspend fun fetchOrdersSlider(
        userId: Long?,
        positionTitle: Int,
        position: Int,
    ): List<HomeFlowViewModel.PositionItem> {
        return coroutineScope {
            if (userId == null) {
                return@coroutineScope emptyList()
            }

            val responseBody = api.fetchOrderSlider(userId)
            withContext(Dispatchers.Default) {
                val response = responseBody.parseOrderSliderResponse()
                if (response is ResponseEntity.Success) {
                    listOf(
                        HomeFlowViewModel.PositionItem(
                            position,
                            HomeOrders(position, response.data.mapToUI())
                        ),
                        HomeFlowViewModel.PositionItem(
                            positionTitle,
                            HomeTitle(
                                id = positionTitle,
                                type = HomeTitle.ORDERS_TITLE,
                                name = "Мои заказы",
                                showAll = true,
                                showAllName = "СМ.ВСЕ"
                            )
                        )
                    )
                } else {
                    emptyList()
                }
            }
        }
    }

    //Слайдер новинок на главной странице
    suspend fun fetchNoveltiesSlider(
        positionTitle: Int,
        position: Int,
    ): List<HomeFlowViewModel.PositionItem> {
        return coroutineScope {
            val responseBody = api.fetchNovelties(action = "novinki")
            withContext(Dispatchers.Default) {
                val response = responseBody.parseNoveltiesSliderResponse()
                if (response is ResponseEntity.Success) {
                    val data = response.data.mapToUI()
                    listOf(
                        HomeFlowViewModel.PositionItem(
                            position,
                            fetchHomeProductsByType(data, HomeProducts.NOVELTIES, position)
                        ),
                        HomeFlowViewModel.PositionItem(
                            positionTitle,
                            HomeTitle(
                                id = positionTitle,
                                type = HomeTitle.NOVELTIES_TITLE,
                                name = "Новинки",
                                showAll = true,
                                showAllName = "СМ.ВСЕ",
                                categoryProductsName = if (data.size == 1) {
                                    data.first().name
                                } else {
                                    ""
                                }
                            )
                        )
                    )
                } else {
                    emptyList()
                }
            }
        }
    }

    //Слайдер акций на главной странице
    suspend fun fetchPromotionsSlider(
        positionTitle: Int,
        position: Int,
    ): List<HomeFlowViewModel.PositionItem> {
        return coroutineScope {
            val responseBody = api.fetchPromotions(action = "akcii", limit = 10, platform = "android")
            withContext(Dispatchers.Default) {
                val response = responseBody.parsePromotionSliderResponse()
                if (response is ResponseEntity.Success) {
                    listOf(
                        HomeFlowViewModel.PositionItem(
                            position, HomePromotions(
                                position, PromotionsSliderBundleUI(
                                    title = "Акции",
                                    containShowAllButton = true,
                                    promotionUIList = response.data.mapToUI()
                                )
                            )
                        ),
                        HomeFlowViewModel.PositionItem(
                            positionTitle,
                            HomeTitle(
                                id = positionTitle,
                                type = HomeTitle.PROMOTIONS_TITLE,
                                name = "Акции",
                                showAll = true,
                                showAllName = "СМ.ВСЕ",
                                lightBg = false
                            )
                        )
                    )
                } else {
                    emptyList()
                }
            }
        }
    }

    //Нижний слйдер на главнйо странице
    suspend fun fetchBottomSlider(
        positionTab: Int,
        position: Int,
    ): List<HomeFlowViewModel.PositionItem> {
        return coroutineScope {
            val responseBody = api.fetchDoubleSlider(action = "topglav", arg = "new")
            withContext(Dispatchers.Default) {
                val response = responseBody.parseBottomSliderResponse()
                if (response is ResponseEntity.Success) {
                    val data = response.data.mapToUI()
                    listOf(
                        HomeFlowViewModel.PositionItem(
                            position,
                            fetchHomeProductsByType(
                                response.data.mapToUI(),
                                HomeProducts.BOTTOM_PROD,
                                position
                            )
                        ),
                        HomeFlowViewModel.PositionItem(
                            positionTab,
                            HomeProductsTabs(id = positionTab, data.mapIndexed { index, cat ->
                                if (index == 0) {
                                    cat.copy(isSelected = true, position = positionTab)
                                } else {
                                    cat.copy(isSelected = false, position = positionTab)
                                }
                            })
                        )
                    )
                } else {
                    emptyList()
                }
            }
        }
    }

    //Слайдер брендов на главной странице
    suspend fun fetchBrandsSlider(
        positionTitle: Int,
        position: Int,
    ): List<HomeFlowViewModel.PositionItem> {
        return coroutineScope {
            val responseBody = api.fetchBrands(action = "brand", limit = 10)
            withContext(Dispatchers.Default) {
                val response = responseBody.parseBrandsSliderResponse()
                if (response is ResponseEntity.Success) {
                    listOf(
                        HomeFlowViewModel.PositionItem(
                            position,
                            HomeBrands(position, response.data.mapToUI())
                        ),
                        HomeFlowViewModel.PositionItem(
                            positionTitle,
                            HomeTitle(
                                id = positionTitle,
                                type = HomeTitle.BRANDS_TITLE,
                                name = "Бренды",
                                showAll = true,
                                showAllName = "СМ.ВСЕ"
                            )
                        )
                    )
                } else {
                    emptyList()
                }
            }
        }
    }

    //Слайдер стран на главной странице
    suspend fun fetchCountriesSlider(position: Int): List<HomeFlowViewModel.PositionItem> {
        return coroutineScope {
            val responseBody = api.fetchCountries(action = "glav")
            withContext(Dispatchers.Default) {
                val response = responseBody.parseCountriesSliderResponse()
                if (response is ResponseEntity.Success) {
                    listOf(
                        HomeFlowViewModel.PositionItem(
                            position,
                            HomeCountries(position, response.data.mapToUI())
                        )
                    )
                } else {
                    emptyList()
                }
            }
        }
    }

//Слайдер ранее просмотренных продуктов

    suspend fun fetchViewedProductsSlider(userId: Long?): ResponseBody {
        return api.fetchViewedProducts(
            action = "viewed",
            userId = userId
        )
    }

    suspend fun fetchViewedProductsSlider(
        userId: Long?,
        positionTitle: Int,
        position: Int,
    ): List<HomeFlowViewModel.PositionItem> {
        return coroutineScope {
            if (userId == null) {
                return@coroutineScope emptyList()
            }

            val responseBody = api.fetchViewedProducts(action = "viewed", userId = userId)
            withContext(Dispatchers.Default) {
                val response = responseBody.parseViewedProductsSliderResponse()
                if (response is ResponseEntity.Success) {
                    val data = response.data.mapToUI()
                    listOf(
                        HomeFlowViewModel.PositionItem(
                            position,
                            fetchHomeProductsByType(data, HomeProducts.VIEWED, position)
                        ),
                        HomeFlowViewModel.PositionItem(
                            positionTitle,
                            HomeTitle(
                                id = positionTitle,
                                type = HomeTitle.VIEWED_TITLE,
                                name = "Вы смотрели",
                                showAll = false,
                                showAllName = "СМ.ВСЕ",
                                categoryProductsName = if (data.size == 1) {
                                    data.first().name
                                } else {
                                    ""
                                }
                            )
                        )
                    )
                } else {
                    emptyList()
                }
            }
        }
    }

    //Слайдер комментариев на главной странице
    suspend fun fetchCommentsSlider(
        positionTitle: Int,
        position: Int,
    ): List<HomeFlowViewModel.PositionItem> {
        return coroutineScope {
            val responseBody = api.fetchComments(action = "otzivy", limit = 10)
            withContext(Dispatchers.Default) {
                val response = responseBody.parseCommentsSliderResponse()
                if (response is ResponseEntity.Success) {
                    listOf(
                        HomeFlowViewModel.PositionItem(
                            position,
                            HomeComments(position, response.data.mapToUI())
                        ),
                        HomeFlowViewModel.PositionItem(
                            positionTitle,
                            HomeTitle(
                                id = positionTitle,
                                type = HomeTitle.COMMENTS_TITLE,
                                name = "Отзывы",
                                showAll = true,
                                showAllName = "Написать отзыв"
                            )
                        )
                    )
                } else {
                    emptyList()
                }
            }
        }
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
        userId: Long,
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
        userId: Long,
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
        productIdListStr: String?,
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
        productIdListStr: String?,
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
        amount: Int? = null,
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
        productId: Long,
    ): ResponseBody {
        return api.fetchProductResponse(blockId, productId)
    }

    //Продукты выбранного бренда
    suspend fun fetchProductsByBrandResponse(
        blockId: Int = 12,
        productId: Long,
        brandId: Long,
        page: Int,
    ): ResponseBody {
        return api.fetchProductsByBrandResponse(blockId, productId, brandId, page)
    }

    //Продукты могут понравиться
    suspend fun fetchMaybeLikeProductsResponse(
        page: Int,
    ): ResponseBody {
        return api.fetchNovelties(action = "details", page = page)
    }

    //Главная информация о категории
    suspend fun fetchCategoryHeader(
        categoryId: Long,
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
        page: Int?,
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

    suspend fun fetchSearchDefaultData(): ResponseBody {
        return api.fetchSearchResponse(action = "glav")
    }

    suspend fun fetchProductsByQueryHeader(query: String): ResponseBody {
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
        page: Int?,
    ): ResponseBody {
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

    suspend fun fetchMatchesQueries(query: String): ResponseBody {
        return api.fetchMatchesQueries(
            action = "glav",
            query = query
        )
    }

    /**
     * Продукты без фильтра
     */

    suspend fun fetchBrandHeader(
        brandId: Long,
    ): ResponseBody {
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
        page: Int?,
    ): ResponseBody {
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
        countryId: Long,
    ): ResponseBody {
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
    ): ResponseBody {
        return api.fetchCountryResponse(
            action = "details",
            countryId = countryId,
            sort = sort,
            orientation = orientation,
            page = page,
            categoryId = categoryId
        )
    }

    suspend fun fetchDiscountHeader(): ResponseBody {
        return api.fetchNoveltiesResponse(
            action = "specpredlosh",
            page = 1
        )
    }

    suspend fun fetchProductsByDiscount(
        categoryId: Long?,
        sort: String?,
        orientation: String?,
        page: Int?,
    ): ResponseBody {
        return api.fetchNoveltiesResponse(
            action = "specpredlosh",
            categoryId = categoryId,
            sort = sort,
            orientation = orientation,
            page = page
        )
    }

    suspend fun fetchNoveltiesHeader(): ResponseBody {
        return api.fetchNoveltiesResponse(
            action = "novinki",
            page = 1
        )
    }

    suspend fun fetchProductsByNovelties(
        categoryId: Long?,
        sort: String?,
        orientation: String?,
        page: Int?,
    ): ResponseBody {
        return api.fetchNoveltiesResponse(
            action = "novinki",
            categoryId = categoryId,
            sort = sort,
            orientation = orientation,
            page = page
        )
    }

    suspend fun fetchDoubleSliderHeader(
        categoryId: Long,
    ): ResponseBody {
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
        orientation: String?,
    ): ResponseBody {
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
        filterId: Long,
    ): ResponseBody = api.fetchPromotionResponse(
        action = "akcii",
        filterId = filterId
    )

    //Акции по баннеру
    suspend fun fetchPromotionsByBanner(categoryId: Long): ResponseBody =
        api.fetchMainSliderResponse(
            action = "detailaction",
            categoryId = categoryId
        )

    //Все бренды
    suspend fun fetchAllBrands(
        brandIdList: List<Long>,
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
    suspend fun fetchPopupNews(userId: Long?): PopupNewsUI? {
        return coroutineScope {
            val response = api.fetchNewsResponse(
                action = "okno",
                userId = userId,
                platform = "android"
            ).parsePopupNewsResponse()

            if (response is ResponseEntity.Success) {
                response.data.mapToUI()
            } else {
                null
            }
        }
    }

    /**
     * pre order
     */

    suspend fun fetchPreOrderFormData(
        userId: Long?,
    ): ResponseBody = api.fetchPreOrderResponse(
        action = "predzakaz",
        userId = userId
    )

    suspend fun preOrderProduct(
        userId: Long?,
        productId: Long?,
        name: String?,
        email: String?,
        phone: String?,
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
    )

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
    )

    suspend fun dontCommentProduct(
        productId: Long,
        userId: Long,
    ) = api.dontCommentProduct(
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
        isAvailable = when (isAvailable) {
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
        page: Int?,
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
        orderId: Long?,
    ) = api.fetchOrdersResponse(
        action = "detail",
        userId = userId,
        appVersion = appVersion,
        orderId = orderId
    )

    //Отмена заказа
    suspend fun cancelOrder(
        orderId: Long?,
    ) = api.cancelOrder(
        orderId = orderId
    )

    /**
     * products filters
     */
//Все филтры по продуктам для выбранной категории
    suspend fun fetchAllFiltersByCategory(
        categoryId: Long,
    ) = api.fetchFilterBundleResponse(categoryId = categoryId)

    /**
     * map
     */

//Информация о зонах доставки
    suspend fun fetchDeliveryZonesResponse(): ResponseBody = api.fetchMapResponse(
        action = "tochkakarta"
    )

    //Адрес по координатам
    suspend fun fetchAddressByGeocodeResponse(
        latitude: Double,
        longitude: Double,
    ) = mapKitApi.fetchAddressByGeocodeResponse(
        apiKey = "346ef353-b4b2-44b3-b597-210d62eeb66b",
        geocode = "$longitude,$latitude",
        format = "json"
    )

    /**
     * profile
     */

//Информация о пользователе
    suspend fun fetchUserData(
        userId: Long,
    ) = api.fetchProfileResponse(
        action = "details",
        userId = userId
    )

    //Информация о пользователе
    suspend fun fetchProfileCategories(
        userId: Long,
        isTablet: Boolean,
    ) = api.fetchProfileCategoriesResponse(
        action = "glav",
        userId = userId,
        appVersion = BuildConfig.VERSION_NAME,
        isTablet = isTablet
    )

    suspend fun fetchPersonalProducts(
        userId: Long?,
        page: Int?,
    ) = api.fetchPersonalProducts(
        action = "tovarchik",
        userId = userId,
        page = page
    )

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
    )

    suspend fun authByPhone(
        phone: String?,
        url: String,
        code: String?,
    ): ResponseBody {

        val uri = ApiConfig.VODOVOZ_URL
            .toUri()
            .buildUpon()
            .encodedPath(url)
            .appendQueryParameter("action", "tochkakarta")
            .appendQueryParameter("telefon", phone)
            .appendQueryParameter("code", code)
            .build()
            .toString()

        return api.fetchAuthByPhoneResponse(uri)
    }

    suspend fun requestCode(
        phone: String?,
        url: String,
    ): ResponseBody {

        val uri = ApiConfig.VODOVOZ_URL
            .toUri()
            .buildUpon()
            .encodedPath(url)
            .appendQueryParameter("action", "tochkakarta")
            .appendQueryParameter("telefon", phone)
            .build()
            .toString()

        return api.fetchAuthByPhoneResponse(uri)
    }

    /**
     * password recovery
     */

    suspend fun recoverPassword(
        email: String?,
    ) = api.recoverPassword(
        forgotPassword = "yes",
        email = email
    )

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
    )

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
        )

    suspend fun fetchTestMapResponse(
        address: String,
        latitude: String,
        longitude: String,
        length: String,
        date: String,
    ): ResponseBody {
        return api.sendTestMapRequest(
            sourse = "API",
            address = address,
            latitude = latitude,
            longitude = longitude,
            length = length,
            date = date
        )
    }

    /**
     * Contacts
     */

    suspend fun fetchContacts() = api.fetchContactsResponse(
        action = "dannyesvyazi",
        appVersion = "1.5.10"
    ) //todo Build.VERSION.RELEASE

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
    )

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
    )

    //Удалить адресс
    suspend fun deleteAddress(
        addressId: Long?,
        userId: Long?,
    ) = api.fetchAddressResponse(
        addressId = addressId,
        userid = userId,
        action = "del",
        blockId = 102
    )

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
    )

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
    )

    /**
     * Ordering
     */

    suspend fun fetchShippingInfo(
        userId: Long?,
        addressId: Long?,
        date: String?,
    ) = api.fetchInfoAboutOrderingResponse(
        userId = userId,
        addressId = addressId,
        date = date
    )

    suspend fun fetchFreeShippingDaysInfoResponse() =
        api.fetchInfoAboutOrderingResponse()

    suspend fun regOrder(
        orderType: Int?, //Тип заказа (1/2)
        device: String?, //Телефон Android:12 Версия: 1.4.83
        addressId: Long?, //адрес id - (150543)
        date: String?, //23.08.2022
        paymentId: Long?, //Pay method Id
        needOperatorCall: String?, // Y/N
        needShippingAlert: String?, //За 90 минут
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
    ) = api.fetchRegOrderResponse(
        orderType = orderType,
        device = device,
        addressId = addressId,
        date = date,
        paymentId = paymentId,
        needOperatorCall = needOperatorCall,
        needShippingAlert = needShippingAlert,
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
        parking = parking
    )

    fun fetchShippingAlertEntityList() = ShippingAlertConfig.shippingAlertEntityList

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
    )

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
    )

    suspend fun activateDiscountCard(
        userId: Long?,
        value: String?,
    ) = api.fetchDiscountCardBaseRequest(
        action = "edit",
        userId = userId,
        value = value
    )

    suspend fun fetchRateBottomData(
        userId: Long?,
    ) = api.fetchRateBottomData(
        action = "tovarglav",
        userId = userId
    )

    suspend fun addProductFromServiceDetails(
        idWithGift: String,
    ) = api.addProductFromServiceDetails(
        "addtoqua",
        idWithGift = idWithGift
    )

    private fun fetchHomeProductsByType(
        data: List<CategoryDetailUI>,
        type: Int,
        position: Int,
    ): HomeProducts {
        return HomeProducts(
            position,
            data,
            productsType = type,
            productsSliderConfig = ProductsSliderConfig(
                containShowAllButton = true
            ),
            prodList = data.first().productUIList
        )
    }

    suspend fun fetchSearchDataByQrCode(searchText: String) = coroutineScope {
        api.fetchSearchDataByQrCode(
            blockId = 12,
            searchText = searchText
        )
    }

    suspend fun fetchNotificationSettingsData(uri: String) = coroutineScope {
        api.fetchNotificationSettingsData(uri)
    }
}