package com.vodovoz.app.data.remote

import com.vodovoz.app.BuildConfig
import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    //Страны
    @GET("/newmobile/glavnaya/strany.php")
    fun fetchCountryResponse(
        @Query("action") action: String,
        @Query("id") countryId: Long? = null,
        @Query("sort") sort: String? = null,
        @Query("ascdesc") orientation: String? = null,
        @Query("nav") page: Int? = null,
        @Query("sect") categoryId: Long? = null
    ): Single<ResponseBody>

    //Рекламный слайдер
    @GET("/newmobile/glavnaya/slayders/index.php")
    fun fetchMainBannerResponse(
        @Query("action") action: String = "slayder"
    ): Single<ResponseBody>

    //Слайдер категорий
    @GET("/newmobile/glavnaya/slayders/mini_index.php")
    fun fetchSecondaryBannerResponse(
        @Query("action") action: String = "slayder",
        @Query("android") androidVersion: String = BuildConfig.VERSION_NAME
    ): Single<ResponseBody>

    //Акции
    @GET("/newmobile/glavnaya/akcii.php")
    fun fetchPromotionResponse(
        @Query("action") action: String? = null,
        @Query("razdelid") filterId: Long? = null,
        @Query("id") promotionId: Long? = null,
        @Query("platforma") platform: String? = null,
        @Query("limit") limit: Int? = null
    ): Single<ResponseBody>


    @GET("/newmobile/glavnaya/super_top.php?action=topglav&new=new")
    fun fetchDoubleSectionList(): Single<ResponseBody>

    @GET("/newmobile/otzivomagaz.php?otziv=otzivy&limit=10")
    fun fetchCommentList(): Single<ResponseBody>

    @GET("/newmobile/glavnaya/stories/?iblock_id=12&action=stories&platforma=android")
    fun fetchHistoryResponse(): Single<ResponseBody>

    @GET("/newmobile/glavnaya/razdel.php?action=popylrazdel")
    fun fetchPopularResponse(): Single<ResponseBody>

    @GET("/newmobile/brand.php?action=brand&limit=10")
    fun fetchBrandResponse(): Single<ResponseBody>

    @GET("/newmobile/brand.php")
    fun fetchAllBrands(
        @Query("action") action: String = "brand"
    ): Single<ResponseBody>

    @GET("newmobile/brand.php?action=detail&id=\"+IDBrand+\"&simvolkod=\"+SymboleCode+\"&nav=\"+currentPage+\"&sect=\"+SectionID")
    fun fetchProductsByBrand(

    ): Single<ResponseBody>

    @GET("/newmobile/glavnaya/novinki.php")
    fun fetchNoveltiesResponse(
        @Query("new") new: String = "novinki",
        @Query("sort") sort: String? = null,
        @Query("ascdesc") orientation: String? = null,
        @Query("nav") page: Int? = null,
        @Query("sect") categoryId: Long? = null
    ): Single<ResponseBody>

    @GET("/newmobile/glavnaya/novinki.php")
    fun fetchDiscountResponse(
        @Query("new") new: String = "specpredlosh",
        @Query("sort") sort: String? = null,
        @Query("ascdesc") orientation: String? = null,
        @Query("nav") page: Int? = null,
        @Query("sect") categoryId: Long? = null
    ): Single<ResponseBody>

    @GET("/newmobile/glavnaya/strany.php")
    fun fetchProductsByCountry(

    ): Single<ResponseBody>

    //CatalogPage
    @GET("/newmobile/razdel/category.php?iblock_id=12")
    fun fetchCatalogResponse(): Single<ResponseBody>

    @GET("/newmobile/razdel/index.php")
    suspend fun fetchCategoryDetailResponse(
        @Query("iblock_id") blockId: Int = 1,
        @Query("sectionid") categoryId: Long,
        @Query("sort") sort: String,
        @Query("ascdesc") orientation: String,
        @Query("nav") pageIndex: Int,
        @Query("filter") filter: String,
        @Query("filtervalue") filterValue: String,
        @Query("price_from") priceFrom: Int,
        @Query("price_to") priceTo: Int
    ): Response<ResponseBody>

    @GET("/newmobile/razdel/index.php")
    fun fetchCategory(
        @Query("iblock_id") blockId: Int = 1,
        @Query("sectionid") categoryId: Long
    ): Single<ResponseBody>

    @GET("/newmobile/razdel/filtercatalog.php")
    fun fetchFilterBundleResponse(
        @Query("action") action: String = "getAllProps",
        @Query("section") categoryId: Long
    ): Single<ResponseBody>

    @GET("/newmobile/filterCatalog.php")
    fun fetchConcreteFilterResponse(
        @Query("action") action: String = "getAllValueOfProps",
        @Query("propCode") filterCode: String,
        @Query("section") categoryId: Long
    ): Single<ResponseBody>

    @GET("/newmobile/details/index_new.php")
    fun fetchProductDetailResponse(
        @Query("iblock_id") blockId: Int = 1,
        @Query("detail") productId: Long
    ): Single<ResponseBody>

    @GET("/newmobile/details/brandtovar.php")
    fun fetchPaginatedBrandProductListResponse(
        @Query("iblock_id") blockId: Int = 12,
        @Query("productid") productId: Long,
        @Query("id") brandId: Long,
        @Query("nav") pageIndex: Int
    ): Single<ResponseBody>

    @GET("/newmobile/glavnaya/novinki.php")
    fun fetchPaginatedMaybeLikeProductListResponse(
        @Query("new") arg: String = "details",
        @Query("nav") pageIndex: Int
    ): Single<ResponseBody>

    @GET("/newmobile/reg.php")
    fun register(
        @Query("name") firstName: String,
        @Query("lastname") secondName: String,
        @Query("email") email: String,
        @Query("pass") password: String,
        @Query("phone") phone: String
    ): Single<ResponseBody>

    @GET("/newmobile/auth.php")
    fun login(
        @Query("email") email: String,
        @Query("pass") password: String
    ): Single<ResponseBody>

    @GET("/newmobile/profile/index.php")
    fun fetchUserData(
        @Query("action") action: String = "details",
        @Query("userid") userId: Long
    ): Single<ResponseBody>

    @GET("/newmobile/glavnaya/orderzakaz.php")
    fun fetchOrderSlider(
        @Query("userid") userId: Long
    ): Single<ResponseBody>

    @GET("/newmobile/viewedproduct/index.php")
    fun fetchViewedProductSlider(
        @Query("action") action: String = "viewed",
        @Query("userid") userId: Long
    ): Single<ResponseBody>



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