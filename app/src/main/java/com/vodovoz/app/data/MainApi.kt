package com.vodovoz.app.data

import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

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

}