package com.vodovoz.app.data.remote

import com.vodovoz.app.data.model.common.*
import com.vodovoz.app.data.model.features.*
import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Query

interface RemoteDataSource {

    //Общая информация о предоставляемых услугах
    fun fetchAboutServices(): Single<ResponseEntity<AboutServicesBundleEntity>>

    //Отправить отзыв о продукте
    fun sendCommentAboutProduct(
        productId: Long,
        rating: Int,
        comment: String,
        userId: Long
    ): Single<ResponseEntity<String>>

    //Все отзывы о продукте
    suspend fun fetchAllCommentsByProduct(
        productId: Long,
        page: Int
    ): Response<ResponseBody>

    //Всплывающая новость
    fun fetchPopupNews(userId: Long?): Single<ResponseEntity<PopupNewsEntity>>

    //Получить Cookie Session Id
    fun fetchCookie(): Single<String>

    //Очистить корзину
    fun clearCart(): Single<ResponseEntity<Boolean>>

    //Изменение колличества товаров в корзине
    fun changeProductsQuantityInCart(
        productId: Long,
        quantity: Int
    ): Single<ResponseEntity<Boolean>>

    //Удаление продукта из корзины
    fun deleteProductFromCart(
        productId: Long
    ): Single<ResponseEntity<Boolean>>

    //Добавление продукта в корзину
    fun addProductToCart(
        productId: Long,
        quantity: Int
    ): Single<ResponseEntity<Boolean>>

    //Содержимое корзины
    fun fetchCart(): Single<ResponseEntity<CartBundleEntity>>

    //Слайдер стран на главной странице
    fun fetchCountriesSlider(): Single<ResponseEntity<CountriesSliderBundleEntity>>

    //Информация о выбранной стране
    fun fetchCountryHeader(
        countryId: Long
    ): Single<ResponseEntity<CategoryEntity>>

    //Постраничная загрузка продуктов для выбранной страны
    suspend fun fetchProductsByCountry(
        countryId: Long,
        sort: String? = null,
        orientation: String? = null,
        categoryId: Long? = null,
        page: Int? = null,
    ): Response<ResponseBody>

    //Слайдер акций на главной странице
    fun fetchPromotionsSlider(): Single<ResponseEntity<List<PromotionEntity>>>

    //Продукты по баннеру
    fun fetchProductsByBanner(categoryId: Long): Single<ResponseEntity<List<ProductEntity>>>

    //Подробно об акции
    fun fetchPromotionDetails(
        promotionId: Long
    ): Single<ResponseEntity<PromotionDetailEntity>>

    //Все акции
    fun fetchAllPromotions(
        filterId: Long
    ): Single<ResponseEntity<AllPromotionsBundleEntity>>

    //Слайдер комментариев на главной странице
    fun fetchCommentsSlider(): Single<ResponseEntity<List<CommentEntity>>>

    //Слайдер историй на главное странице
    fun fetchHistoriesSlider(): Single<ResponseEntity<List<HistoryEntity>>>

    //Слайдер популярных разделов на главной странице
    fun fetchPopularSlider(): Single<ResponseEntity<List<CategoryEntity>>>

    //Информация о слайдере заказов на главной странице
    fun fetchOrdersSlider(userId: Long): Single<ResponseEntity<List<OrderEntity>>>

    //Слайдер самых выгодных продуктов на главной странице
    fun fetchDiscountsSlider(): Single<ResponseEntity<List<CategoryDetailEntity>>>

    //Слайдер новинок на главной странице
    fun fetchNoveltiesSlider(): Single<ResponseEntity<List<CategoryDetailEntity>>>

    //Продукты и категории "Может понравиться"
    fun fetchMaybeLikeProducts(page: Int): Single<ResponseEntity<PaginatedProductListEntity>>

    //Слайдер ранее просмотренных продуктов
    fun fetchViewedProductsSlider(userId: Long?): Single<ResponseEntity<List<CategoryDetailEntity>>>

    //Слайдер рекламных баннеров на главной странице
    fun fetchAdvertisingBannersSlider(): Single<ResponseEntity<List<BannerEntity>>>

    //Слайдер баннеров категорий на главной странице
    fun fetchCategoryBannersSlider(): Single<ResponseEntity<List<BannerEntity>>>

    //Слайдер брендов на главной странице
    fun fetchBrandsSlider(): Single<ResponseEntity<List<BrandEntity>>>

    //Гланвная информация о выбранной стране
    fun fetchBrandHeader(brandId: Long): Single<ResponseEntity<CategoryEntity>>

    //Главная информация о самых выгодных продуктах
    fun fetchDiscountHeader(): Single<ResponseEntity<CategoryEntity>>

    //Главная информация о новинках
    fun fetchNoveltiesHeader(): Single<ResponseEntity<CategoryEntity>>

    //Постраничная загрузка продуктов для выбранного бренда
    suspend fun fetchProductsByBrand(
        brandId: Long?,
        code: String?,
        categoryId: Long?,
        sort: String?,
        orientation: String?,
        page: Int?
    ): Response<ResponseBody>

    //Постраничная загрузка новинок
    suspend fun fetchProductsNovelties(
        categoryId: Long?,
        sort: String?,
        orientation: String?,
        page: Int?
    ): Response<ResponseBody>

    //Постраничная загрузка самых выгодных продуктов
    suspend fun fetchProductsDiscount(
        categoryId: Long?,
        sort: String?,
        orientation: String?,
        page: Int?
    ): Response<ResponseBody>

    //Все бренды
    fun fetchAllBrands(
        brandIdList: List<Long>
    ): Single<ResponseEntity<List<BrandEntity>>>

    //Акции по баннеру
    fun fetchPromotionsByBanner(categoryId: Long): Single<ResponseEntity<AllPromotionsBundleEntity>>

    suspend fun fetchProductsByCategory(
        categoryId: Long,
        sort: String,
        orientation: String,
        filter: String,
        filterValue: String,
        priceFrom: Int,
        priceTo: Int,
        page: Int
    ): Response<ResponseBody>

    fun fetchCategoryHeader(categoryId: Long): Single<ResponseEntity<CategoryEntity>>

    //Каталог
    fun fetchCatalog(): Single<ResponseEntity<List<CategoryEntity>>>

    //Все филтры по продуктам для выбранной категории
    fun fetchAllFiltersByCategory(categoryId: Long): Single<ResponseEntity<FilterBundleEntity>>

    //Подробная информация о продукте
    fun fetchProductDetails(productId: Long): Single<ResponseEntity<ProductDetailBundleEntity>>

    //Филтр для продукта по id
    fun fetchProductFilterById(
        categoryId: Long,
        filterCode: String,
    ): Single<ResponseEntity<List<FilterValueEntity>>>

    //Постраничная загрузка нескольких продуктов для выбранного бренда
    fun fetchSomeProductsByBrand(
        productId: Long,
        brandId: Long,
        page: Int
    ): Single<ResponseEntity<PaginatedProductListEntity>>

    //Верхний слайдер на главной странице
    fun fetchTopSlider(): Single<ResponseEntity<List<CategoryDetailEntity>>>

    //Постраничная загрузка продуктов для выбранного слайдера
    suspend fun fetchProductsBySlider(
        categoryId: Long?,
        page: Int?,
        sort: String?,
        orientation: String?
    ): Response<ResponseBody>

    //Главная информация о слайдере
    fun fetchSliderHeader(
        categoryId: Long
    ): Single<ResponseEntity<CategoryEntity>>

    //Нижний слйдер на главнйо странице
    fun fetchBottomSlider(): Single<ResponseEntity<List<CategoryDetailEntity>>>

    //Регистрация нового пользователя
    fun register(
        firstName: String,
        secondName: String,
        email: String,
        password: String,
        phone: String
    ): Single<ResponseEntity<Long>>

    //Авторизация
    fun login(
        email: String,
        password: String
    ): Single<ResponseEntity<Long>>

    //Данные пользователя
    fun fetchUserData(userId: Long): Single<ResponseEntity<UserDataEntity>>

    //Добавить в избранное для авторизованного пользователя
    fun addToFavorite(
        productIdList: List<Long>,
        userId: Long
    ): Single<ResponseEntity<String>>

    //Удалить из избранного для авторизованного пользователя
    fun removeFromFavorite(
        productId: Long,
        userId: Long
    ): Single<ResponseEntity<String>>

    //Основная информация об избранных продуктах
    fun fetchFavoriteProductsHeaderBundleResponse(
        userId: Long?,
        productIdListStr: String?
    ): Single<ResponseEntity<FavoriteProductsHeaderBundleEntity>>

    //Постраничная загрузка избранных продуктов
    suspend fun fetchFavoriteProductsResponse(
        userId: Long?,
        productIdListStr: String,
        categoryId: Long?,
        sort: String?,
        orientation: String?,
        page: Int?,
        isAvailable: Boolean?,
    ): Response<ResponseBody>

    //Информация о зонах доставки
    fun fetchDeliveryZonesResponse(): Single<ResponseEntity<DeliveryZonesBundleEntity>>

    //Получить сохраненные адреса
    fun fetchAddressesSaved(
        userId: Long?,
        type: Int?
    ): Single<ResponseEntity<List<AddressEntity>>>

    //Адрес по координатам
    fun fetchAddressByGeocodeResponse(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): Single<ResponseEntity<AddressEntity>>

    //Обновить адрес
    fun updateAddress(
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
    ): Single<ResponseEntity<String>>

    //Добавить адрес в сохраненные
    fun addAddress(
        locality: String?,
        street: String?,
        house: String?,
        entrance: String?,
        floor: String?,
        office: String?,
        comment: String?,
        type: Int?,
        userId: Long?
    ): Single<ResponseEntity<String>>

    //Удалить адресс
    fun deleteAddress(
        addressId: Long?,
        userId: Long?
    ): Single<ResponseEntity<String>>

    //Постраничная загрузка всех заказов
    suspend fun fetchAllOrders(
        userId: Long?,
        appVersion: String?,
        orderId: Long?,
        status: String?,
        page: Int?
    ): Response<ResponseBody>

    //Детальная информация о заказе
    fun fetchOrderDetailsResponse(
        userId: Long?,
        appVersion: String?,
        orderId: Long?
    ): Single<ResponseEntity<OrderDetailsEntity>>

}