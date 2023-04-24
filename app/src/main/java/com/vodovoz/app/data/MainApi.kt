package com.vodovoz.app.data

import com.vodovoz.app.common.product.rating.RatingResponse
import com.vodovoz.app.feature.map.test.model.MapTestResponse
import com.vodovoz.app.feature.sitestate.model.SiteStateResponse
import com.vodovoz.app.feature.profile.cats.ProfileCategoriesModel
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface MainApi {

    //Рекламный слайдер
    @GET("/newmobile/glavnaya/slayders/index.php")
    suspend fun fetchAdvBanners(
        @Query("action") action: String? = null,
        @Query("id") categoryId: Long? = null,
    ): ResponseBody

    //Истории
    @GET("/newmobile/glavnaya/stories")
    suspend fun fetchHistories(
        @Query("iblock_id") blockId: Int,
        @Query("action") action: String,
        @Query("platforma") platform: String,
    ): ResponseBody

    //Популярное
    @GET("/newmobile/glavnaya/razdel.php")
    suspend fun fetchPopulars(
        @Query("action") action: String,
    ): ResponseBody

    //Слайдер категорий
    @GET("/newmobile/glavnaya/slayders/mini_index.php")
    suspend fun fetchCategoryBanners(
        @Query("action") action: String,
        @Query("android") androidVersion: String,
    ): ResponseBody

    //Новинки
    @GET("/newmobile/glavnaya/novinki.php")
    suspend fun fetchNovelties(
        @Query("new") action: String,
        @Query("sort") sort: String? = null,
        @Query("ascdesc") orientation: String? = null,
        @Query("nav") page: Int? = null,
        @Query("sect") categoryId: Long? = null,
    ): ResponseBody

    //Двойной слайдер
    @GET("/newmobile/glavnaya/super_top.php")
    suspend fun fetchDoubleSlider(
        @Query("action") action: String? = null,
        @Query("new") arg: String? = null,
        @Query("android") androidVersion: String? = null,
        @Query("sort") sort: String? = null,
        @Query("orientation") orientation: String? = null,
        @Query("id") categoryId: Long? = null,
        @Query("nav") page: Int? = null,
    ): ResponseBody

    //Заказы
    @GET("/newmobile/glavnaya/orderzakaz.php")
    suspend fun fetchOrderSlider(
        @Query("userid") userId: Long,
    ): ResponseBody

    //Акции
    @GET("/newmobile/glavnaya/akcii.php")
    suspend fun fetchPromotions(
        @Query("action") action: String? = null,
        @Query("razdelid") filterId: Long? = null,
        @Query("id") promotionId: Long? = null,
        @Query("platforma") platform: String? = null,
        @Query("limit") limit: Int? = null,
    ): ResponseBody

    //Бренды
    @GET("/newmobile/brand.php") //
    suspend fun fetchBrands(
        @Query("action") action: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("id") brandId: String? = null,
        @Query("idbrand") brandIdList: String? = null,
        @Query("sort") sort: String? = null,
        @Query("ascdesc") orientation: String? = null,
        @Query("simvolkod") code: String? = null,
        @Query("nav") page: Int? = null,
        @Query("sect") categoryId: Long? = null,
    ): ResponseBody


    //Страны
    @GET("/newmobile/glavnaya/strany.php")
    suspend fun fetchCountries(
        @Query("action") action: String,
        @Query("id") countryId: Long? = null,
        @Query("sort") sort: String? = null,
        @Query("ascdesc") orientation: String? = null,
        @Query("nav") page: Int? = null,
        @Query("sect") categoryId: Long? = null,
    ): ResponseBody

    //Просмотренные продукты
    @GET("/newmobile/viewedproduct/index.php")
    suspend fun fetchViewedProducts(
        @Query("action") action: String? = null,
        @Query("userid") userId: Long? = null,
    ): ResponseBody

    //Отзывы
    @GET("/newmobile/otzivomagaz.php")
    suspend fun fetchComments(
        @Query("otziv") action: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("userid") userId: Long? = null,
        @Query("text") comment: String? = null,
        @Query("rating") rating: Int? = null,
    ): ResponseBody

    @GET("/newmobile/osnova/izbrannoe/adddel.php")
    suspend fun fetchChangeFavoriteResponse(
        @Query("iblock") blockId: Long? = null,
        @Query("action") action: String? = null,
        @Query("id") productIdList: String? = null,
        @Query("userid") userId: Long? = null,
    ): ResponseBody

    //Добавление в корзину
    @GET("/newmobile/korzina/function/add/index.php")
    suspend fun fetchAddProductResponse(
        @Query("action") action: String? = null,
        @Query("id") productId: Long? = null,
        @Query("quantity") quantity: Int? = null,
    ): ResponseBody

    //Изменение колличества товаров в корзине
    @GET("/newmobile/korzina/function/guaty/index.php")
    suspend fun fetchChangeProductsQuantityResponse(
        @Query("action") action: String? = null,
        @Query("id") productId: Long? = null,
        @Query("quantity") quantity: Int? = null,
    ): ResponseBody

    /**
     * Избранное
     */

    @GET("/newmobile/izbrannoe.php?")
    suspend fun fetchFavoriteResponse(
        @Query("id") productIdList: String? = null,
        @Query("userid") userId: Long? = null,
        @Query("sort") sort: String? = null,
        @Query("ascdesc") orientation: String? = null,
        @Query("nav") page: Int? = null,
        @Query("sect") categoryId: Long? = null,
        @Query("action") action: String? = null,
    ): ResponseBody

    /**
     * Каталог
     */

    //Каталог
    @GET("/newmobile/razdel/category.php?iblock_id=12")
    suspend fun fetchCatalogResponse(): ResponseBody

    //Категория
    @GET("/newmobile/razdel/index.php")
    suspend fun fetchCategoryResponse(
        @Query("iblock_id") blockId: Int? = null,
        @Query("sectionid") categoryId: Long? = null,
        @Query("sort") sort: String? = null,
        @Query("ascdesc") orientation: String? = null,
        @Query("nav") page: Int? = null,
        @Query("filter") filter: String? = null,
        @Query("filtervalue") filterValue: String? = null,
        @Query("price_from") priceFrom: Int? = null,
        @Query("price_to") priceTo: Int? = null,
    ): ResponseBody


    /**
     * Корзина
     * **/

    //Корзина
    @GET("/newmobile/korzina/index.php")
    suspend fun fetchCartResponse(
        @Query("action") action: String? = null,
        @Query("userid") userId: Long? = null,
        @Query("coupon") coupon: String? = null,
        @Query("quantity") amount: Int? = null,
    ): ResponseBody

    //Добавление в корзину fetchAddProductResponse
    //Изменение колличества товаров в корзине fetchChangeProductsQuantityResponse

    //Удаление из корзины
    @GET("/newmobile/korzina/function/deletto/index.php")
    suspend fun fetchDeleteProductResponse(
        @Query("action") action: String? = null,
        @Query("id") productId: Long? = null,
    ): ResponseBody

    //Очистить корзину
    @GET("newmobile/korzina/function/delkorzina/index.php")
    suspend fun fetchClearCartResponse(
        @Query("action") action: String? = null,
    ): ResponseBody

    /**
     * Продукт
     */

    //Продукт
    @GET("/newmobile/details/index_new.php")
    suspend fun fetchProductResponse(
        @Query("iblock_id") blockId: Int,
        @Query("detail") productId: Long,
    ): ResponseBody

    //Продукты выбранного бренда
    @GET("/newmobile/details/brandtovar.php")
    suspend fun fetchProductsByBrandResponse(
        @Query("iblock_id") blockId: Int,
        @Query("productid") productId: Long,
        @Query("id") brandId: Long,
        @Query("nav") page: Int,
    ): ResponseBody

    /**
     * Поиск
     */

    @GET("/newmobile/searching/index.php")
    suspend fun fetchSearchResponse(
        @Query("action") action: String? = null,
        @Query("search") query: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("sort") sort: String? = null,
        @Query("ascdesc") orientation: String? = null,
        @Query("nav") page: Int? = null,
        @Query("sect") categoryId: Long? = null,
    ): ResponseBody

    @GET("/newmobile/searching/minipoisk.php")
    suspend fun fetchMatchesQueries(
        @Query("action") action: String? = null,
        @Query("search") query: String? = null,
    ): ResponseBody

    /**
     * Продукты без фильтра
     */

    //Бренды
    @GET("/newmobile/brand.php") //
    suspend fun fetchBrandResponse(
        @Query("action") action: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("id") brandId: String? = null,
        @Query("idbrand") brandIdList: String? = null,
        @Query("sort") sort: String? = null,
        @Query("ascdesc") orientation: String? = null,
        @Query("simvolkod") code: String? = null,
        @Query("nav") page: Int? = null,
        @Query("sect") categoryId: Long? = null,
    ): ResponseBody

    //Страны
    @GET("/newmobile/glavnaya/strany.php")
    suspend fun fetchCountryResponse(
        @Query("action") action: String,
        @Query("id") countryId: Long? = null,
        @Query("sort") sort: String? = null,
        @Query("ascdesc") orientation: String? = null,
        @Query("nav") page: Int? = null,
        @Query("sect") categoryId: Long? = null,
    ): ResponseBody

    //Новинки
    @GET("/newmobile/glavnaya/novinki.php")
    suspend fun fetchNoveltiesResponse(
        @Query("new") action: String,
        @Query("sort") sort: String? = null,
        @Query("ascdesc") orientation: String? = null,
        @Query("nav") page: Int? = null,
        @Query("sect") categoryId: Long? = null,
    ): ResponseBody

    //Двойной слайдер
    @GET("/newmobile/glavnaya/super_top.php")
    suspend fun fetchDoubleSliderResponse(
        @Query("action") action: String? = null,
        @Query("new") arg: String? = null,
        @Query("android") androidVersion: String? = null,
        @Query("sort") sort: String? = null,
        @Query("orientation") orientation: String? = null,
        @Query("id") categoryId: Long? = null,
        @Query("nav") page: Int? = null,
    ): ResponseBody

    /**
     * only products
     */

    //Рекламный слайдер
    @GET("/newmobile/glavnaya/slayders/index.php")
    suspend fun fetchMainSliderResponse(
        @Query("action") action: String? = null,
        @Query("id") categoryId: Long? = null,
        @Query("baner") baner: String? = "uzkiy"
    ): ResponseBody

    /**
     * promo details
     */

    //Акции
    @GET("/newmobile/glavnaya/akcii.php")
    suspend fun fetchPromotionResponse(
        @Query("action") action: String? = null,
        @Query("razdelid") filterId: Long? = null,
        @Query("id") promotionId: Long? = null,
        @Query("platforma") platform: String? = null,
        @Query("limit") limit: Int? = null,
    ): ResponseBody

    /**
     * news
     */

    @GET("newmobile/glavnaya/okno.php")
    suspend fun fetchNewsResponse(
        @Query("action") action: String? = null,
        @Query("id") userId: Long? = null,
        @Query("platform") platform: String? = null,
    ): ResponseBody

    /**
     * pre order
     */

    @GET("newmobile/osnova/predzakaz.php")
    suspend fun fetchPreOrderResponse(
        @Query("action") action: String? = null,
        @Query("userid") userId: Long? = null,
        @Query("fio") name: String? = null,
        @Query("email") email: String? = null,
        @Query("phone") phone: String? = null,
        @Query("tovar") productId: Long? = null
    ): ResponseBody

    @GET("newmobile/voteRating.php")
    suspend fun rateProduct(
        @Query("element_id") productId: Long,
        @Query("rating_value") ratingValue: Float
    ): RatingResponse

    /**
     * all comments by products
     */

    @GET("/newmobile/comments.php")
    suspend fun fetchCommentsResponse(
        @Query("iblock_id") blockId: Long? = null,
        @Query("action") action: String? = null,
        @Query("id") productId: Long? = null,
        @Query("nav") page: Int? = null,
        @Query("rating_value") rating: Int? = null,
        @Query("message") comment: String? = null,
        @Query("userid") userId: Long? = null,
    ): ResponseBody

    /**
     * past purchases
     */

    @GET("/newmobile/profile/historyorder/proshlpokipki.php")
    suspend fun fetchPastPurchasesResponse(
        @Query("action") action: String? = null,
        @Query("userid") userId: Long? = null,
        @Query("sort") sort: String? = null,
        @Query("ascdesc") orientation: String? = null,
        @Query("nav") page: Int? = null,
        @Query("sect") categoryId: Long? = null,
        @Query("act") isAvailable: String? = null,
    ): ResponseBody

    /**
     * all orders
     */

    @GET("/newmobile/profile/historyorder/spisokzakazov.php")
    suspend fun fetchOrdersHistoryResponse(
        @Query("userid") userId: Long? = null,
        @Query("action") action: String? = null,
        @Query("nav") page: Int? = null,
        @Query("android") appVersion: String? = null,
        @Query("status") status: String? = null,
        @Query("id") orderId: Long? = null,
    ): ResponseBody

    /**
     * repeat order && order details
     */

    @GET("/newmobile/profile/historyorder/detailzakaz.php")
    suspend fun fetchOrdersResponse(
        @Query("userid") userId: Long? = null,
        @Query("action") action: String? = null,
        @Query("android") appVersion: String? = null,
        @Query("id") orderId: Long? = null,
    ): ResponseBody

    @GET("newmobile/changeOrderStatus.php")
    suspend fun cancelOrder(
        @Query("orderID") orderId: Long?
    ): ResponseBody

    /**
     * products filters
     */
    //Все филтры продуктов
    @GET("/newmobile/razdel/filtercatalog.php")
    suspend fun fetchFilterBundleResponse(
        @Query("action") action: String = "getAllProps",
        @Query("section") categoryId: Long,
    ): ResponseBody

    /**
     * map
     */

    @GET("/newmobile/profile/karta/index.php")
    suspend fun fetchMapResponse(
        @Query("action") action: String? = null
    ): ResponseBody

    /**
     * profile
     */

    //Профиль
    @GET("/newmobile/profile/index.php")
    suspend fun fetchProfileResponse(
        @Query("action") action: String? = null,
        @Query("userid") userId: Long? = null,
        @Query("name") firstName: String? = null,
        @Query("lastname") secondName: String? = null,
        @Query("password") password: String? = null,
        @Query("phone") phone: String? = null,
        @Query("gender") sex: String? = null,
        @Query("birthday") birthday: String? = null,
        @Query("email") email: String? = null,
    ): ResponseBody

    //Профиль категории
    @GET("/newmobile/profile/index.php")
    suspend fun fetchProfileCategoriesResponse(
        @Query("action") action: String? = null,
        @Query("userid") userId: Long? = null,
    ): ProfileCategoriesModel

    @GET("newmobile/profile/tovary.php")
    suspend fun fetchPersonalProducts(
        @Query("action") action: String? = null,
        @Query("userid") userId: Long? = null,
        @Query("nav") page: Int? = null,
    ): ResponseBody

    /**
     * auth
     */

    //Авторизация
    @GET("/newmobile/auth.php")
    suspend fun fetchLoginResponse(
        @Query("email") email: String,
        @Query("pass") password: String,
    ): ResponseBody

    @GET
    suspend fun fetchAuthByPhoneResponse(
        @Url url: String
    ): ResponseBody

    /**
     * password recovery
     */

    @GET("newmobile/recoverPass.php")
    suspend fun recoverPassword(
        @Query("email") email: String? = null,
        @Query("forgot_password") forgotPassword: String? = null,
    ): ResponseBody

    /**
     * block site
     */

    @GET("newmobile/config/closesait.php")
    suspend fun fetchSiteState(
        @Query("action") action: String? = null
    ): SiteStateResponse

    /**
     * registration
     */

    //Регистрация
    @GET("/newmobile/reg.php")
    suspend fun fetchRegisterResponse(
        @Query("name") firstName: String,
        @Query("lastname") secondName: String,
        @Query("email") email: String,
        @Query("pass") password: String,
        @Query("phone") phone: String,
    ): ResponseBody

    //Firebase Token
    @GET("newmobile/osnova/userpushapi.php")
    suspend fun sendFirebaseToken(
        @Query("action") action: String? = null,
        @Query("userid") userId: Long? = null,
        @Query("token") token: String? = null,
    ): ResponseBody

    /**
     * Services
     */

    @GET("/newmobile/glavnaya/uslygi/uslygi.php")
    suspend fun fetchServicesResponse(
        @Query("action") action: String? = null,
    ): ResponseBody

    @GET("http://szorinvodovoz.tw1.ru/local/components/semap/delivery.calc/order.php")
    suspend fun sendTestMapRequest(
        @Query("SOURCE") sourse: String,
        @Query("ADDRESS", encoded = false) address: String,
        @Query("COORDS_FROM[0]", encoded = false) latitude: String,
        @Query("COORDS_FROM[1]", encoded = false) longitude: String,
        @Query("LENGTH") length: String,
        @Query("DELIVERY_DATE", encoded = true) date: String
    ): ResponseBody

    /**
     * Contacts
     */

    @GET("/newmobile/glavnaya/svyssnam.php")
    suspend fun fetchContactsResponse(
        @Query("action") action: String? = null,
        @Query("versiyaan") appVersion: String? = null,
    ): ResponseBody

    /**
     * Send Mail
     */

    @GET("/newmobile/mail.php")
    suspend fun sendMail(
        @Query("name") name: String? = null,
        @Query("phone") phone: String? = null,
        @Query("email") email: String? = null,
        @Query("comment") comment: String? = null,
    ): ResponseBody

    /**
     * Addresses
     */

    @GET("/newmobile/address.php")
    suspend fun fetchAddressResponse(
        @Query("city") locality: String? = null,
        @Query("description") comment: String? = null,
        @Query("entrance") entrance: String? = null,
        @Query("flat") office: String? = null,
        @Query("floor") floor: String? = null,
        @Query("house") house: String? = null,
        @Query("street") street: String? = null,
        @Query("userid") userid: Long? = null,
        @Query("iblock_id") blockId: Int? = null,
        @Query("action") action: String? = null,
        @Query("tip") type: Int? = null,
        @Query("addressid") addressId: Long? = null,
        @Query("leghtkm") length: String? = null,
        @Query("polnadres") fullAddress: String? = null,
        @Query("ktochka") longAndLat: String? = null
    ): ResponseBody

    /**
     * Ordering
     */

    @GET("newmobile/getActualDelivery.php")
    suspend fun fetchInfoAboutOrderingResponse(
        @Query("address_id") addressId: Long? = null,
        @Query("userid") userId: Long? = null,
        @Query("curDate") date: String? = null,
    ): ResponseBody

    @GET("newmobile/doorder.php")
    suspend fun fetchRegOrderResponse(
        @Query("type") orderType: Int?, //Тип заказа (1/2)
        @Query("device") device: String?, //Телефон Android:12 Версия: 1.4.83
        @Query("profile_buyer") addressId: Long?, //адрес id - (150543)
        @Query("date") date: String?, //23.08.2022
        @Query("payment") paymentId: Long?, //Pay method Id
        @Query("operator") needOperatorCall: String?, // Y/N
        @Query("driver") needShippingAlert: String?, //За 90 минут
        @Query("comment") comment: String?,
        @Query("summ") totalPrice: Int?, //Итоговая сумма заказа
        @Query("delivery_id") shippingId: Long?, //
        @Query("summdelivery") shippingPrice: Int?, // цена доставки
        @Query("fio_f") name: String?,
        @Query("phone_f") phone: String?,
        @Query("email_f") email: String?,
        @Query("companyname") companyName: String?,
        @Query("userid") userId: Long?,
        @Query("zalogcena") deposit: Int?, //?
        @Query("sroch") fastShippingPrice: Int?, // 500 р
        @Query("nacenka") extraShippingPrice: Int?, // из delivery
        @Query("obichd") commonShippingPrice: Int?, //?
        @Query("kupon") coupon: String?, // передавать из корзины
        @Query("indos") shippingIntervalId: Long?, //id Интервал доставки
        @Query("sdacha") overMoney: Int?, //?
        @Query("parkovka") parking: Int?, // числовое значение
    ): ResponseBody

    @Multipart
    @POST("/newmobile/profile/index.php?action=uploadPhoto")
    suspend fun addAvatar(
        @Field("userid") id: Long,
        @Part file: MultipartBody.Part
    ): Response<Void>

}