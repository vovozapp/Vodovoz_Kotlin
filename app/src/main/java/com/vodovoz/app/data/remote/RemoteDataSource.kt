package com.vodovoz.app.data.remote

import com.vodovoz.app.data.model.common.*
import com.vodovoz.app.data.model.features.*
import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.Response

interface RemoteDataSource {

    fun fetchPreOrderFormData(
        userId: Long?
    ): Single<ResponseEntity<PreOrderFormDataEntity>>

    fun preOrderProduct(
        userId: Long?,
        productId: Long?,
        name: String?,
        email: String?,
        phone: String?
    ): Single<ResponseEntity<String>>

    //Общая информация о предоставляемых услугах
    fun fetchAboutServices(): Single<ResponseEntity<AboutServicesBundleEntity>>

    //Отправить отзыв о продукте
    fun sendCommentAboutProduct(
        productId: Long,
        rating: Int,
        comment: String,
        userId: Long
    ): Single<ResponseEntity<String>>

    //Добавить отзыв о магазине
    fun fetchSendCommentAboutShopResponse(
        userId: Long?,
        comment: String?,
        rating: Int?
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

    //Продукты по результатам поиска
    suspend fun fetchProductsByQuery(
        query: String?,
        categoryId: Long?,
        sort: String?,
        orientation: String?,
        page: Int?
    ): Response<ResponseBody>

    //Список подходящих запросов
    fun fetchMatchesQueries(query: String?): Single<ResponseEntity<List<String>>>

    //Хедер продуктов по запросу
    fun fetchProductsByQueryHeader(
        query: String?
    ): Single<ResponseEntity<CategoryEntity>>

    //Превью для поиска
    fun fetchSearchDefaultData(): Single<ResponseEntity<DefaultSearchDataBundleEntity>>

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
    fun fetchCart(
        userId: Long?,
        coupon: String?
    ): Single<ResponseEntity<CartBundleEntity>>

    //Слайдер стран на главной странице
    fun fetchCountriesSlider(): Single<ResponseEntity<CountriesSliderBundleEntity>>

    fun fetchFreeShippingDaysInfoResponse(): Single<ResponseEntity<FreeShippingDaysInfoBundleEntity>>

    fun fetchBottles(): Single<ResponseEntity<List<BottleEntity>>>

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
    fun fetchProductDetails(productId: Long): Single<ResponseEntity<ProductDetailsBundleEntity>>

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

    //Постраничная загрузка продуктов из прошлых покупок
    suspend fun fetchPastPurchasesProducts(
        userId: Long?,
        sort: String?,
        orientation: String?,
        categoryId: Long?,
        isAvailable: Boolean?,
        page: Int?,
    ): Response<ResponseBody>

    //Хедер для прошлых покупок
    fun fetchPastPurchasesHeader(userId: Long?): Single<ResponseEntity<PastPurchasesHeaderBundleEntity>>

    //Востановить пароль
    fun recoverPassword(email: String? = null): Single<ResponseEntity<Boolean>>

    //Персонализированная подборка продуктов
    fun fetchPersonalProducts(
        userId: Long?,
        page: Int?
    ): Single<ResponseEntity<CategoryDetailEntity>>

    fun updateUserData(
        userId: Long,
        firstName: String?,
        secondName: String?,
        password: String?,
        phone: String?,
        sex: String?,
        birthday: String?,
        email: String?,
    ): Single<ResponseEntity<Boolean>>

    fun fetchActivateDiscountCardInfo(userId: Long?): Single<ResponseEntity<ActivateDiscountCardBundleEntity>>

    fun activateDiscountCard(
        userId: Long?,
        value: String?
    ): Single<ResponseEntity<String>>

    fun fetchQuestionnairesTypes(): Single<ResponseEntity<List<QuestionnaireTypeEntity>>>

    fun fetchQuestionnaire(
        userId: Long?,
        questionnaireType: String?
    ): Single<ResponseEntity<List<QuestionEntity>>>

    fun fetchContacts(appVersion: String?): Single<ResponseEntity<ContactsBundleEntity>>

    fun sendMail(
        name: String?,
        phone: String?,
        email: String?,
        comment: String?
    ): Single<ResponseEntity<String>>

    fun fetchServiceById(
        type: String?
    ): Single<ResponseEntity<ServiceEntity>>

    fun fetchFormForOrderService(
        type: String?,
        userId: Long?
    ): Single<ResponseEntity<List<ServiceOrderFormFieldEntity>>>

    fun orderService(
        type: String?,
        userId: Long?,
        value: String?
    ): Single<ResponseEntity<String>>

    fun fetchShippingInfo(
        userId: Long?,
        addressId: Long?,
        date: String?
    ): Single<ResponseEntity<ShippingInfoBundleEntity>>

    fun regOrder(
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
    ) : Single<ResponseEntity<OrderingCompletedInfoBundleEntity>>

    fun cancelOrder(orderId: Long?): Single<ResponseEntity<String>>

    fun requestCode(phone: String?): Single<ResponseEntity<Int>>

    fun authByPhone(
        phone: String?,
        code: String?
    ): Single<ResponseEntity<Long>>

}