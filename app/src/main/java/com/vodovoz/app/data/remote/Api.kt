package com.vodovoz.app.data.remote

import android.widget.RatingBar
import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    //Получить Cookie Session Id
    @GET("/newmobile/korzina/function/guaty/index.php")
    fun fetchCookie(): Single<Response<ResponseBody>>


    //Отзывы
    @GET("/newmobile/otzivomagaz.php")
    fun fetchCommentResponse(
        @Query("otziv") action: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("userid") userId: Long? = null,
        @Query("text") comment: String? = null,
//        @Query("rating")
    ): Single<ResponseBody>

    //"newmobile/otzivomagaz.php?otziv=add&userid=" + User.getInstance().getuser_id().toString() + "&text=".toString() + editTextComments.getText().toString().toString() + "&rating=" + ratingBar.getRating()
    //Страны
    @GET("/newmobile/glavnaya/strany.php")
    suspend fun fetchCountryResponse(
        @Query("action") action: String,
        @Query("id") countryId: Long? = null,
        @Query("sort") sort: String? = null,
        @Query("ascdesc") orientation: String? = null,
        @Query("nav") page: Int? = null,
        @Query("sect") categoryId: Long? = null,
    ): Response<ResponseBody>

    //Рекламный слайдер
    @GET("/newmobile/glavnaya/slayders/index.php")
    fun fetchMainSliderResponse(
        @Query("action") action: String? = null,
        @Query("id") categoryId: Long? = null,
    ): Single<ResponseBody>

    //Слайдер категорий
    @GET("/newmobile/glavnaya/slayders/mini_index.php")
    fun fetchMiniSliderResponse(
        @Query("action") action: String,
        @Query("android") androidVersion: String,
    ): Single<ResponseBody>

    //Акции
    @GET("/newmobile/glavnaya/akcii.php")
    fun fetchPromotionResponse(
        @Query("action") action: String? = null,
        @Query("razdelid") filterId: Long? = null,
        @Query("id") promotionId: Long? = null,
        @Query("platforma") platform: String? = null,
        @Query("limit") limit: Int? = null,
    ): Single<ResponseBody>

    //Истории
    @GET("/newmobile/glavnaya/stories")
    fun fetchHistoryResponse(
        @Query("iblock_id") blockId: Int,
        @Query("action") action: String,
        @Query("platforma") platform: String,
    ): Single<ResponseBody>

    //Популярное
    @GET("/newmobile/glavnaya/razdel.php")
    fun fetchPopularResponse(
        @Query("action") action: String,
    ): Single<ResponseBody>

    //Новинки
    @GET("/newmobile/glavnaya/novinki.php")
    suspend fun fetchNoveltiesResponse(
        @Query("new") action: String,
        @Query("sort") sort: String? = null,
        @Query("ascdesc") orientation: String? = null,
        @Query("nav") page: Int? = null,
        @Query("sect") categoryId: Long? = null,
    ): Response<ResponseBody>

    //Заказы
    @GET("/newmobile/glavnaya/orderzakaz.php")
    fun fetchOrderSlider(
        @Query("userid") userId: Long,
    ): Single<ResponseBody>

    //Просмотренные продукты
    @GET("/newmobile/viewedproduct/index.php")
    fun fetchViewedProductSliderResponse(
        @Query("action") action: String? = null,
        @Query("userid") userId: Long? = null,
    ): Single<ResponseBody>

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
    ): Response<ResponseBody>

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
    ): Response<ResponseBody>

    //Каталог
    @GET("/newmobile/razdel/category.php?iblock_id=12")
    fun fetchCatalogResponse(): Single<ResponseBody>

    //Все филтры продуктов
    @GET("/newmobile/razdel/filtercatalog.php")
    fun fetchFilterBundleResponse(
        @Query("action") action: String = "getAllProps",
        @Query("section") categoryId: Long,
    ): Single<ResponseBody>

    //Фильтр продуктов
    @GET("/newmobile/filterCatalog.php")
    fun fetchFilterResponse(
        @Query("action") action: String,
        @Query("propCode") filterCode: String,
        @Query("section") categoryId: Long,
    ): Single<ResponseBody>

    //Продукт
    @GET("/newmobile/details/index_new.php")
    fun fetchProductResponse(
        @Query("iblock_id") blockId: Int,
        @Query("detail") productId: Long,
    ): Single<ResponseBody>

    //Бренд выбранного продукта
    @GET("/newmobile/details/brandtovar.php")
    fun fetchBrandByProductResponse(
        @Query("iblock_id") blockId: Int,
        @Query("productid") productId: Long,
        @Query("id") brandId: Long,
        @Query("nav") page: Int,
    ): Single<ResponseBody>

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
    ): Response<ResponseBody>

    //Регистрация
    @GET("/newmobile/reg.php")
    fun fetchRegisterResponse(
        @Query("name") firstName: String,
        @Query("lastname") secondName: String,
        @Query("email") email: String,
        @Query("pass") password: String,
        @Query("phone") phone: String,
    ): Single<ResponseBody>

    //Авторизация
    @GET("/newmobile/auth.php")
    fun fetchLoginResponse(
        @Query("email") email: String,
        @Query("pass") password: String,
    ): Single<ResponseBody>

    //Профиль
    @GET("/newmobile/profile/index.php")
    fun fetchProfileResponse(
        @Query("action") action: String,
        @Query("userid") userId: Long,
    ): Single<ResponseBody>

    //Корзина
    @GET("/newmobile/korzina/index.php")
    fun fetchCartResponse(
        @Query("action") action: String? = null,
        @Query("userid") userId: Long? = null,
        @Query("quantity") amount: Int? = null,
    ): Single<ResponseBody>

    //Добавление в корзину
    @GET("/newmobile/korzina/function/add/index.php")
    fun fetchAddProductResponse(
        @Query("action") action: String? = null,
        @Query("id") productId: Long? = null,
        @Query("quantity") quantity: Int? = null,
    ): Single<ResponseBody>

    //удаление из корзины
    @GET("/newmobile/korzina/function/deletto/index.php")
    fun fetchDeleteProductResponse(
        @Query("action") action: String? = null,
        @Query("id") productId: Long? = null,
    ): Single<ResponseBody>

    //Изменение колличества товаров в корзине
    @GET("/newmobile/korzina/function/guaty/index.php")
    fun fetchChangeProductsQuantityResponse(
        @Query("action") action: String? = null,
        @Query("id") productId: Long? = null,
        @Query("quantity") quantity: Int? = null,
    ): Single<ResponseBody>

    //Очистить корзину
    @GET("newmobile/korzina/function/delkorzina/index.php")
    fun fetchClearCartResponse(
        @Query("action") action: String? = null,
    ): Single<ResponseBody>

    @GET("newmobile/glavnaya/okno.php")
    fun fetchNewsResponse(
        @Query("action") action: String? = null,
        @Query("id") userId: Long? = null,
        @Query("platform") platform: String? = null,
    ): Single<ResponseBody>


    ///newmobile/korzina/function/delkorzina/index.php?action=delkorzina
    ///newmobile/korzina/index.php?action=getbasket&userid="+user_id
    ///newmobile/korzina/function/delkorzina/index.php?action=delkorzina
    ///newmobile/korzina/index.php?action=getbasket&userid="+User.getInstance().getuser_id()+"&coupon=
    ///newmobile/osnova/izbrannoe/adddel.php?action=del&id="+id+"&iblock=12&userid="+User.getInstance().getuser_id()
    ///newmobile/osnova/izbrannoe/adddel.php?action=add&id="+id+"&iblock=12&userid="+User.getInstance().getuser_id())
    //newmobile/korzina/index.php?action=getbasket&userid="+user_id+"&coupon="+txtCopon.
    //newmobile/korzina.php?action=getbasketuser&coupon="+txtCopon
    //newmobile/korzina/function/add/index.php?action=add&id=" + idProducts + "&quantity=" + diffren


    ///newmobile/viewedproduct/index.php?action=viewed&userid=
    ///newmobile/glavnaya/orderzakaz.php?userid="+User.getInstance().getuser_id()
    //"newmobile/osnova/userpushapi.php?action=token&userid="+User.getInstance().getuser_id()+"&token="+token
    //"newmobile/profile/index.php?action=details&userid="+User.getInstance().getuser_id())
    //newmobile/auth.php?email="+email+"&"+"pass="+body)
    //newmobile/profile/index.php?action=details&userid="+User.getInstance().getuser_id()
    //newmobile/user.php?action=logout
    //"newmobile/reg.php?name="+Name+"&lastname="+LastName+"&email="+Email+"&pass="+Password+"&phone="+Phone
    //http://m.vodovoz.ru/newmobile/details/brandtovar.php?iblock_id=12&productid=84860&id=58591&nav=1
    //http://m.vodovoz.ru/newmobile/details/index_new.php?iblock_id=12&detail=84860
    ///newmobile/viewedproduct/index.php?action=viewed&userid=user_id
}