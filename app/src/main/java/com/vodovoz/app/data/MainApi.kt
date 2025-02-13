package com.vodovoz.app.data

import com.vodovoz.app.BuildConfig
import com.vodovoz.app.data.model.common.UserReloginEntity
import com.vodovoz.app.feature.bottom.services.newservs.model.AboutServicesNew
import com.vodovoz.app.feature.productdetail.present.model.PresentInfo
import com.vodovoz.app.feature.profile.cats.ProfileCategoriesModel
import com.vodovoz.app.feature.profile.notificationsettings.model.NotificationSettingsModel
import com.vodovoz.app.feature.search.qrcode.model.QrCodeModel
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface MainApi {

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
        @Query("versiyaan") version: String = BuildConfig.VERSION_NAME,
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
        @Query("sect") sectId: Long? = null,
    ): ResponseBody

    //Заказы
    @GET("/newmobile/glavnaya/orderzakaz.php")
    suspend fun fetchOrderSlider(
        @Query("userid") userId: Long,
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

//    @GET("/newmobile/osnova/izbrannoe/adddel.php")
//    suspend fun fetchChangeFavoriteResponse(
//        @Query("iblock") blockId: Long? = null,
//        @Query("action") action: String? = null,
//        @Query("id") productIdList: String? = null,
//        @Query("userid") userId: Long? = null,
//    ): ResponseBody

    //Добавление в корзину
    @GET("/newmobile/korzina/function/add/index.php")
    suspend fun fetchAddProductResponse(
        @Query("action") action: String? = null,
        @Query("id") productId: Long? = null,
        @Query("quantity") quantity: Int? = null,
        @Query("idquanit", encoded = true) idWithGift: String? = null,
    ): ResponseBody

    //Изменение количества товаров в корзине
    @GET("/newmobile/korzina/function/guaty/index.php")
    suspend fun fetchChangeProductsQuantityResponse(
        @Query("action") action: String? = null,
        @Query("id") productId: Long? = null,
        @Query("quantity") quantity: Int? = null,
    ): ResponseBody

    @GET("/newmobile/korzina.php")
    suspend fun fetchBottomCartResponse(
        @Query("action") action: String = "getbasketuser",
    ): ResponseBody

    /**
     * Избранное
     */

    @GET("/newmobile/izbrannoe.php")
    suspend fun fetchFavoriteResponse(
        @Query("id", encoded = true) productIdList: String? = null,
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
        @QueryMap filterMap: HashMap<String, String> = hashMapOf(),
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
        @Query("versiyaan") appVersion: String? = null,
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
        @Query("userid") userId: Long? = null,
        @QueryMap tracking: Map<String, String> = mapOf(),
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
        @QueryMap tracking: Map<String, String> = mapOf(),
    ): ResponseBody

    @GET("/newmobile/searching/minipoisk.php")
    suspend fun fetchMatchesQueries(
        @Query("action") action: String? = null,
        @Query("search") query: String? = null,
        @Query("versiyaan") version: String = BuildConfig.VERSION_NAME,
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

    /**
     * only products
     */

    //Рекламный слайдер
    @GET("/newmobile/glavnaya/slayders/index.php")
    suspend fun fetchMainSliderResponse(
        @Query("action") action: String? = null,
        @Query("id") categoryId: Long? = null,
        @Query("baner") baner: String? = null,
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
        @Query("tovar") productId: Long? = null,
    ): ResponseBody

//    @GET("newmobile/voteRating.php")
//    suspend fun rateProduct(
//        @Query("element_id") productId: Long,
//        @Query("rating_value") ratingValue: Float,
//    ): RatingResponse

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

    @GET("/newmobile/korzina/function/povtor/index.php")
    suspend fun repeatOrder(
        @Query("orderID") orderId: Long? = null,
        @Query("userid") userId: Long? = null,
    ): ResponseBody

    @GET("newmobile/changeOrderStatus.php")
    suspend fun cancelOrder(
        @Query("orderID") orderId: Long?,
    ): ResponseBody

    @GET("/newmobile/glavnaya/uslygi/form.php")
    suspend fun fetchOrderServiceResponse(
        @Query("action") action: String? = null,
        @Query("tip") type: String? = null,
        @Query("userid") userId: Long? = null,
        @Query("filtervalue") value: String? = null,
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

    //Фильтр продуктов
    @GET("/newmobile/filterCatalog.php")
    suspend fun fetchFilterResponse(
        @Query("action") action: String = "getAllValueOfProps",
        @Query("propCode") filterCode: String,
        @Query("section") categoryId: Long,
    ): ResponseBody

    /**
     * map
     */

    @GET("/newmobile/profile/karta/index.php")
    suspend fun fetchMapResponse(
        @Query("action") action: String? = null,
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

    @GET("newmobile/user.php?action=logout")
    suspend fun logout(@Query("userid") userId: Long? = null): Response<ResponseBody>

    //Профиль категории
    @GET("/newmobile/profile/index.php")
    suspend fun fetchProfileCategoriesResponse(
        @Query("action") action: String? = null,
        @Query("userid") userId: Long? = null,
        @Query("versiyaan") appVersion: String? = null,
        @Query("device_type_android") isTablet: String = "phone",
    ): ProfileCategoriesModel

    @GET("newmobile/profile/tovary.php")
    suspend fun fetchPersonalProducts(
        @Query("action") action: String? = null,
        @Query("userid") userId: Long? = null,
        @Query("nav") page: Int? = null,
    ): ResponseBody

    @GET("/newmobile/profile/anketa/index.php")
    suspend fun fetchQuestionnairesResponse(
        @Query("action") action: String? = null,
        @Query("userid") userId: Long? = null,
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

    // https://szorin.vodovoz.ru/newmobile/config/openuserid.php?userid=515&token=a6af096d-7aee-0694-b7d6-431f-b073-6258c34be4bb
    @GET("/newmobile/config/openuserid.php")
    suspend fun fetchReloginResponse(
        @Query("userid") userId: Long,
        @Query("token") token: String,
    ): Response<UserReloginEntity>

    @GET
    suspend fun fetchAuthByPhoneResponse(
        @Url url: String,
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
        @Query("action") action: String? = null,
    ): ResponseBody

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
        @Query("action") action: String,
        @Query("userid") userId: Long,
        @Query("token") token: String,
    ): ResponseBody

    /**
     * Services
     */

    @GET("/newmobile/glavnaya/uslygi/uslygi.php")
    suspend fun fetchServicesResponse(
        @Query("action") action: String? = null,
    ): ResponseBody

    @GET("/newmobile/glavnaya/uslygi/uslygi_new.php")
    suspend fun fetchServicesNewResponse(
        @Query("action") action: String? = null,
    ): AboutServicesNew

    @GET("/newmobile/glavnaya/uslygi/uslygi_new.php")
    suspend fun fetchServicesNewDetailsResponse(
        @Query("action") action: String? = null,
        @Query("id") id: String? = null,
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
        @Query("domofon") intercom: String? = null,
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
        @Query("ktochka") longAndLat: String? = null,
    ): ResponseBody

    /**
     * Ordering
     */

    @GET("newmobile/getActualDelivery.php")
    suspend fun fetchInfoAboutOrderingResponse(
        @Query("address_id") addressId: Long? = null,
        @Query("userid") userId: Long? = null,
        @Query("curDate") date: String? = null,
        @Query("versiyaan") appVersion: String?,
    ): ResponseBody

    @GET("newmobile/doorder.php")
    suspend fun fetchRegOrderResponse(
        @Query("type") orderType: Int?, //Тип заказа (1/2)
        @Query("device") device: String?, //Телефон Android:12 Версия: 1.4.83
        @Query("profile_buyer") addressId: Long?, //адрес id - (150543)
        @Query("date") date: String?, //23.08.2022
        @Query("payment") paymentId: Long?, //Pay method Id
        @Query("operator") needOperatorCall: String?, // Y/N
        @Query("driver") needShippingAlert: String?,//За 90 минут
        @Query("dopphone") shippingAlertPhone: String?,//телефон для водителя
        @Query("comment") comment: String?,
        @Query("summ") totalPrice: Int?, //Итоговая сумма заказа
        @Query("delivery_id") shippingId: Long?, //
        @Query("summdelivery") shippingPrice: Int?, // цена доставки
        @Query("fio_f") name: String?,
        @Query("phone_f") phone: String?,
        @Query("email_f") email: String?,
        @Query("inn") inn: String?,
        @Query("companyname") companyName: String?,
        @Query("userid") userId: Long?,
        @Query("zalogcena") deposit: Int?, //?
        @Query("sroch") fastShippingPrice: Int?, // 500 р
        @Query("nacenka") extraShippingPrice: Int?, // из delivery
        @Query("obichd") commonShippingPrice: Int?, //?
        @Query("kupon") coupon: String?, // передавать из корзины
        @Query("indos") shippingIntervalId: Long?, //id Интервал доставки
        @Query("sdacha") overMoney: Int?, //?
        @Query("parkovka") parking: Int?, // числовое значение,
        @Query("versiyaan") appVersion: String?,
        @Query("nettovar") checkDeliveryValue: Int?,
        @Query("schet") useScore: String = "N",
    ): ResponseBody

    @Multipart
    @POST("/newmobile/profile/index.php?action=uploadPhoto")
    suspend fun addAvatar(
        @Part("userid") id: Long,
        @Part file: MultipartBody.Part,
    ): Response<Void>

    @GET("/newmobile/profile/aktivacija_karty/index.php")
    suspend fun fetchDiscountCardBaseRequest(
        @Query("action") action: String? = null,
        @Query("userid") userId: Long? = null,
        @Query("filtervalue") value: String? = null,
    ): ResponseBody

    @GET("/newmobile/osnova/sertificat/activaciya.php")
    suspend fun fetchCertificateBaseRequest(
        @Query("action") action: String? = null,
        @Query("userid") userId: Long? = null,
        @Query("code") code: String? = null,
    ): ResponseBody

    @GET("newmobile/osnova/sertificat/index.php")
    suspend fun fetchBuyCertificateData(
        @Query("action") action: String = "glav",
        @Query("userid") userId: Long? = null,
    ): ResponseBody

    @GET("/newmobile/glavnaya/otzivtovari.php")
    suspend fun fetchRateBottomData(
        @Query("action") action: String? = null,
        @Query("id") productId: Long? = null,
        @Query("userid") userId: Long? = null,
    ): ResponseBody

    @GET("newmobile/el.php")
    suspend fun fetchSearchDataByQrCode(
        @Query("iblock_id") blockId: Int,
        @Query("search") searchText: String? = null,
    ): QrCodeModel

    @GET
    suspend fun fetchNotificationSettingsData(
        @Url url: String,
    ): NotificationSettingsModel

    @GET("newmobile/el.php")
    suspend fun fetchBottles(
        @Query("iblock_id") iBlockId: Int? = 90,
    ): ResponseBody

    @GET("newmobile/osnova/sertificat/index.php")
    suspend fun fetchBuyCertificateResponse(
        @Query("action") action: String = "oformlenie",
        @Query("userid") userId: Long? = null,
        @QueryMap buyCertificateMap: HashMap<String, String> = hashMapOf(),
    ): ResponseBody

    @POST
    suspend fun postUrl(url: String)

    @GET("newmobile/details/podarki.php")
    suspend fun fetchPresentInfo(
        @Query("action") action: String = "podarki",
        @Query("userid") userId: Long? = null,
        @Query("id") productId: Long,
    ): PresentInfo

}