package com.vodovoz.app.data.vodovoz_service

import com.vodovoz.app.BuildConfig
import com.vodovoz.app.data.vodovoz_service.model.AllBottlesDetailsDTO
import com.vodovoz.app.data.vodovoz_service.model.AnalogsSectionDTO
import com.vodovoz.app.data.vodovoz_service.model.BannerDTO
import com.vodovoz.app.data.vodovoz_service.model.BrandSectionDTO
import com.vodovoz.app.data.vodovoz_service.model.CATEGORY_NODE_DTO
import com.vodovoz.app.data.vodovoz_service.model.CertificateActivationDetailsDTO
import com.vodovoz.app.data.vodovoz_service.model.FieldsDTO
import com.vodovoz.app.data.vodovoz_service.model.MiniSearchRecommendationsDTO
import com.vodovoz.app.data.vodovoz_service.model.OrderMenuDTO
import com.vodovoz.app.data.vodovoz_service.model.PopularCategoriesDTO
import com.vodovoz.app.data.vodovoz_service.model.PopupWindowDTO
import com.vodovoz.app.data.vodovoz_service.model.PreOrderDTO
import com.vodovoz.app.data.vodovoz_service.model.PresentDTO
import com.vodovoz.app.data.vodovoz_service.model.ProductCommentsDTO
import com.vodovoz.app.data.vodovoz_service.model.ProductsSectionDTO
import com.vodovoz.app.data.vodovoz_service.model.PromotionDetailsDTO
import com.vodovoz.app.data.vodovoz_service.model.PromotionsDTO
import com.vodovoz.app.data.vodovoz_service.model.RAZDEL_DTO
import com.vodovoz.app.data.vodovoz_service.model.RegisterDTO
import com.vodovoz.app.data.vodovoz_service.model.RegistrationDetailsDTO
import com.vodovoz.app.data.vodovoz_service.model.SearchRecommendationsDTO
import com.vodovoz.app.data.vodovoz_service.model.SiteStateResponseDTO
import com.vodovoz.app.data.vodovoz_service.model.StoriesDTO
import com.vodovoz.app.data.vodovoz_service.model.SuperTopAndBottomSectionsDTO
import com.vodovoz.app.data.vodovoz_service.model.VodovozErrorResponseDTO
import com.vodovoz.app.data.vodovoz_service.model.VodovozResponseDTO
import com.vodovoz.app.data.vodovoz_service.model.auth.LoginDetailsDTO
import com.vodovoz.app.data.vodovoz_service.model.auth.UserAuthInfoDTO
import com.vodovoz.app.data.vodovoz_service.model.cart.CartDetailsDTO
import com.vodovoz.app.data.vodovoz_service.model.catalog.CatalogDetailsDTO
import com.vodovoz.app.data.vodovoz_service.model.filters.FiltersDTO
import com.vodovoz.app.data.vodovoz_service.model.order_details.OrderDetailsDTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.ProductDetailsDTO
import com.vodovoz.app.data.vodovoz_service.model.profile.ProfileDetailsDTO
import com.vodovoz.app.data.vodovoz_service.model.unrated_products.UnratedProductsSectionDTO
import com.vodovoz.app.data.vodovoz_service.model.user_data.UserDataDTO
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface VodovozService {




    @GET("profile/historyorder/detailzakaz.php?action=detail")
    suspend fun getOrderDetails(
        @Query("userid") userId: Long?,
        @Query("id") orderId: Long
    ): Response<VodovozResponseDTO<OrderDetailsDTO>>

    /**
     * Brand requests
     * */
    @GET("brand.php?action=detail")
    suspend fun getBrandProducts(
        @Query("id") brandId: Long,
        @Query("nav") page: Int = 1,
        @Query("sort") sort: String = "",
        @Query("ascdesc") order: String = "",
        @Query("sect") categoryId: Int? = null,
    ): Response<VodovozResponseDTO<ProductsSectionDTO>>

    @GET("brand.php?action=brand")
    suspend fun getBrands(
        @Query("nav") page: Int? = null,
        @Query("search") search: String? = null,
    ): Response<VodovozResponseDTO<BrandSectionDTO>>


    /**
     * Category requests
     * */
    @GET("razdel/category.php?iblock_id=12")
    suspend fun getCategoryTree(
        @Query("id") categoryId: Long,
    ): Response<VodovozResponseDTO<List<CATEGORY_NODE_DTO>>>


    /**
     * Profile requests
     * */
    @GET("profile/index.php?action=glav")
    suspend fun getProfileDetails(
        @Query("userid") userId: Long,
    ): Response<VodovozResponseDTO<ProfileDetailsDTO>>

    @GET("profile/index.php?action=details")
    suspend fun getUserData(
        @Query("userid") userId: Long,
    ): Response<VodovozResponseDTO<UserDataDTO>>

    @GET("profile/index.php?action=edit")
    suspend fun updateUserData(
        @Query("userid") userId: Long,
        @QueryMap queries: Map<String, String>,
    ): Response<VodovozResponseDTO<String>>

    @Multipart
    @POST("profile/index.php?action=uploadPhoto")
    suspend fun updateUserAvatar(
        @Query("userid") userId: Long,
        @Part file: MultipartBody.Part,
    ): Response<VodovozResponseDTO<String>>

    @GET("profile/index.php?action=parol")
    suspend fun getChangePasswordDetails(
        @Query("userid") userId: Long,
    ): Response<VodovozResponseDTO<FieldsDTO>>

    @GET("profile/index.php?action=edit")
    suspend fun updatePassword(
        @Query("userid") userId: Long,
        @Query("password") password: String,
    ): Response<VodovozResponseDTO<String>>

    /**
     * Filter requests
     * */
    @GET("razdel/filtercatalog.php?action=getAllProps")
    suspend fun getFilters(@Query("section") categoryId: Int): Response<VodovozResponseDTO<FiltersDTO>>

    @GET("razdel/filtercatalog.php?action=getAllValueOfProps")
    suspend fun getFilterValues(
        @Query("section") categoryId: Int,
        @Query("propCode") filterId: String,
    ): Response<VodovozResponseDTO<List<String>>>

    /**
     *  Certificate activation screen
     * */
    @GET("osnova/sertificat/activaciya.php?action=glav")
    suspend fun getCertificateActivationDetails(): Response<VodovozResponseDTO<CertificateActivationDetailsDTO>>

    @GET("osnova/sertificat/activaciya.php?action=detail")
    suspend fun activateCertificate(
        @Query("userid") userId: Long,
        @QueryMap queries: Map<String, String>,
    ): Response<VodovozResponseDTO<String>>

    /**
     * Catalog screen
     * */
    @GET("razdel/category.php?iblock_id=12")
    suspend fun getCatalogDetails(): Response<VodovozResponseDTO<CatalogDetailsDTO>>

    @GET("razdel/index.php?iblock_id=12")
    suspend fun getCategoryProducts(
        @Query("sectionid") categoryId: Long,
        @Query("nav") page: Int = 1,
        @Query("sort") sort: String = "",
        @Query("ascdesc") order: String = "",
        @Query("filter") filters: String? = null,
        @Query("filtervalue") filtersAndValues: String? = null,
        @Query("price_to") priceTo: Float? = null,
        @Query("price_from") priceFrom: Float? = null,
    ): Response<VodovozResponseDTO<ProductsSectionDTO>>

    /**
     * Search requests
     * */
    //todo - mb put tracking
    @GET("searching/index.php?action=glav")
    suspend fun getSearchRecommendations(): Response<VodovozResponseDTO<SearchRecommendationsDTO>>

    //todo - mb put tracking
    @GET("searching/minipoisk.php?action=glav")
    suspend fun getMiniSearchRecommendations(
        @Query("search") query: String,
    ): Response<VodovozResponseDTO<MiniSearchRecommendationsDTO>>

    @GET("searching/index.php?action=search&nav=1")
    suspend fun getSearchProducts(
        @Query("search") query: String,
        @Query("nav") page: Int = 1,
        @Query("sect") categoryId: Int? = null,
        @Query("sort") sort: String = "",
        @Query("ascdesc") order: String = "",
        @Query("kamera") isCamera: String? = null,
    ): Response<VodovozResponseDTO<ProductsSectionDTO>>

    /**
     * Login requests
     * */
    @GET("auth.php?action=glav")
    suspend fun getLoginDetails(): Response<VodovozResponseDTO<LoginDetailsDTO>>

    @GET("auth.php?action=glav&email=Y")
    suspend fun getLoginByEmailDetails(): Response<VodovozResponseDTO<LoginDetailsDTO>>

    @GET("reg.php?action=glav")
    suspend fun getRegisterFields(): Response<VodovozResponseDTO<RegistrationDetailsDTO>>

    @GET("auth.php?action=otpravka")
    suspend fun loginByEmail(
        @QueryMap queries: Map<String, String>,
    ): Response<VodovozResponseDTO<UserAuthInfoDTO>>

    @GET("reg.php?action=otpravka")
    suspend fun register(
        @QueryMap queries: Map<String, String>,
    ): Response<RegisterDTO>


    @Headers("Cookie: ")
    @GET("config/openuserid.php?sandroid=${BuildConfig.VERSION_NAME}")
    suspend fun relogin(
        @Query("userid") userId: Long,
        @Query("token") token: String,
    ): Response<VodovozResponseDTO<Boolean>>

    /**
     * Main requests
     * */
    @GET("config/closesait.php?action=saitosnova&android=${BuildConfig.VERSION_NAME}")
    suspend fun getSiteState(): Response<SiteStateResponseDTO?>

    /**
     * PreOrder screen
     * */
    @GET("osnova/predzakaz.php?action=predzakaz")
    suspend fun getPreOrderFields(
        @Query("userid") userId: Long,
        @Query("tovar") productId: Long,
    ): Response<VodovozResponseDTO<PreOrderDTO>>

    @GET("osnova/predzakaz.php?action=otpravka")
    suspend fun sendPreorder(
        @Query("userid") userId: Long?,
        @Query("tovar") productId: Long,
        @QueryMap queries: Map<String, String>,
    ): Response<VodovozErrorResponseDTO>

    /**
     * Cart requests
     * */
    @GET("korzina/index.php?action=getbasket")
    suspend fun getCartDetails(
        @Query("userid") userId: Long? = null,
        @Query("coupon") coupon: String? = null
    ): Response<VodovozResponseDTO<CartDetailsDTO>>

    @GET("korzina/function/add/index.php?action=add")
    suspend fun addProductToCart(
        @Query("id") productId: Long,
        @Query("quantity") quantity: Int,
    ): Response<VodovozResponseDTO<String>>

    @GET("korzina/function/add/index.php?action=addtoqua")
    suspend fun addMultipleProductsToCart(
        @Query("idquanit") productIdsWithQuantity: String,
    ): Response<VodovozResponseDTO<String>>

    @GET("korzina/function/deletto/index.php?action=deletto")
    suspend fun removeProductFromCart(
        @Query("id") productId: Long,
    ): Response<VodovozResponseDTO<String>>

    @GET("korzina/function/guaty/index.php?action=guaty")
    suspend fun updateProductInCart(
        @Query("id") productId: Long,
        @Query("quantity") quantity: Int,
    ): Response<VodovozResponseDTO<String>>

    @GET("korzina/index.php?action=delbasket")
    suspend fun clearCart(): Response<Unit>

    @GET("korzina/brand.php?iblock_id=90")
    suspend fun getAllBottles(): Response<VodovozResponseDTO<AllBottlesDetailsDTO>>


    /**
     * ProductComments screen
     */
    @GET("comments.php?action=detail")
    suspend fun getComments(
        @Query("id") productId: Long,
        @Query("nav") page: Int,
        @Query("sort") sort: String = "",
        @Query("ascdesc") order: String = "",
    ): Response<VodovozResponseDTO<ProductCommentsDTO>>


    /**
     * ProductDetails screen
     */
    @GET("details/index.php?iblock_id=12")
    suspend fun getProductDetails(
        @Query("id") productId: Long,
    ): Response<VodovozResponseDTO<ProductDetailsDTO>>

    @GET("details/podarki.php?action=podarki")
    suspend fun getPresentInfo(
        @Query("userid") userId: Long,
    ): Response<VodovozResponseDTO<PresentDTO>>

    /**
     * ProductsCollection screen
     */
    @GET("details/analog.php?id=105622")
    suspend fun getProductAnalogs(
        @Query("id") productId: Long,
        @Query("sort") sort: String = "",
        @Query("ascdesc") order: String = "",
    ): Response<VodovozResponseDTO<AnalogsSectionDTO>>


    /**
     * Promotion screens
     */
    @GET("glavnaya/akcii.php?action=akcii")
    suspend fun getPromotionsWithSections(
        @Query("nav") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("sect") categoryId: Int? = null,
    ): Response<VodovozResponseDTO<PromotionsDTO>>

    @GET("glavnaya/akcii.php?action=detail")
    suspend fun getPromotionDetails(
        @Query("id") promotionId: Int,
        @Query("nav") page: Int = 1,
        @Query("limit") limit: Int = 5,
    ): Response<VodovozResponseDTO<PromotionDetailsDTO>>

    /**
     * Home screen
     */
    @GET("glavnaya/slayders/index.php?action=slayder&android=${BuildConfig.VERSION_NAME}")
    suspend fun getBanners(
    ): Response<VodovozResponseDTO<List<BannerDTO>>>

    @GET("osnova/banners.php")
    suspend fun getBannerProducts(
        @Query("id") bannerId: Long,
        @Query("iblock") blockId: Long,
        @Query("nav") page: Int = 1,
        @Query("sort") sort: String = "",
        @Query("ascdesc") order: String = "",
        @Query("sect") categoryId: Int? = null,
    ): Response<VodovozResponseDTO<ProductsSectionDTO>>

    @GET("osnova/banners.php")
    suspend fun getBannerPromotions(
        @Query("id") bannerId: Long,
        @Query("iblock") blockId: Long,
        @Query("nav") page: Int = 1,
        @Query("sect") categoryId: Int? = null,
    ): Response<VodovozResponseDTO<PromotionsDTO>>

    @GET("glavnaya/otzivtovari.php?action=tovarglav")
    suspend fun getUnratedProductsDetails(
        @Query("userid") userId: Long,
    ): Response<VodovozResponseDTO<UnratedProductsSectionDTO>>


    @GET("glavnaya/stories/index.php?iblock_id=12&action=stories&platforma=android")
    suspend fun getStories(): Response<VodovozResponseDTO<StoriesDTO>>

    @GET("glavnaya/stories/index.php?iblock_id=12&action=storisdetailtovary&platforma=android")
    suspend fun getStoriesProducts(
        @Query("id") productsId: Long,
        @Query("nav") page: Int = 1,
        @Query("sect") categoryId: Int = -1,
        @Query("sort") sort: String = "",
        @Query("ascdesc") order: String = "",
    ): Response<VodovozResponseDTO<ProductsSectionDTO>>

    @GET("glavnaya/stories/index.php?iblock_id=12&action=storisdetailactions&platforma=android")
    suspend fun getStoriesPromotions(
        @Query("id") promotionsId: Long,
        @Query("nav") page: Int = 1,
    ): Response<VodovozResponseDTO<PromotionsDTO>>

    @GET("glavnaya/menushka.php?action=glavnaya")
    suspend fun getOrderMenu(
        @Query("userid") userId: Long? = null,
    ): Response<VodovozResponseDTO<OrderMenuDTO>>

    @GET("glavnaya/razdel.php?action=popylrazdel")
    suspend fun getPopularCategories(): Response<VodovozResponseDTO<PopularCategoriesDTO>>

    @GET("glavnaya/akcii.php?action=akcii&limit=10")
    suspend fun getPromotions(): Response<VodovozResponseDTO<PromotionsDTO>>

    @GET("glavnaya/novinki.php?new=novinki")
    suspend fun getNewProducts(): Response<VodovozResponseDTO<RAZDEL_DTO>>

    @GET("glavnaya/novinki.php?new=novinki&detail=Y")
    suspend fun getAllNewProducts(
        @Query("nav") page: Int = 1,
        @Query("sect") categoryId: Int = -1,
        @Query("sort") sort: String = "",
        @Query("ascdesc") order: String = "",
    ): Response<VodovozResponseDTO<ProductsSectionDTO>>

    @GET("glavnaya/viewedproduct/index.php?action=viewed")
    suspend fun getViewedProducts(
        @Query("userid") userId: Long,
    ): Response<VodovozResponseDTO<RAZDEL_DTO>>

    @GET("glavnaya/viewedproduct/index.php?action=details")
    suspend fun getAllViewedProducts(
        @Query("userid") userId: Long,
        @Query("nav") page: Int = 1,
        @Query("sect") categoryId: Int = -1,
        @Query("sort") sort: String = "",
        @Query("ascdesc") order: String = "",
    ): Response<VodovozResponseDTO<ProductsSectionDTO>>

    @GET("glavnaya/novinki.php?new=specpredlosh&android=${BuildConfig.VERSION_NAME}")
    suspend fun getHurryUpBuyProducts(): Response<VodovozResponseDTO<RAZDEL_DTO>>

    @GET("glavnaya/novinki.php?new=specpredlosh&detail=Y&android=${BuildConfig.VERSION_NAME}")
    suspend fun getAllHurryUpBuyProducts(
        @Query("nav") page: Int = 1,
        @Query("sect") categoryId: Int = -1,
        @Query("sort") sort: String = "",
        @Query("ascdesc") order: String = "",
    ): Response<VodovozResponseDTO<ProductsSectionDTO>>

    @GET("glavnaya/super_top.php?action=topglav")
    suspend fun getSuperTop(): Response<VodovozResponseDTO<SuperTopAndBottomSectionsDTO>>

    @GET("glavnaya/super_top.php?action=details")
    suspend fun getAllSuperTop(
        @Query("id") id: Long,
        @Query("nav") page: Int = 1,
        @Query("sect") categoryId: Int = -1,
        @Query("sort") sort: String = "",
        @Query("ascdesc") order: String = "",
    ): Response<VodovozResponseDTO<ProductsSectionDTO>>

    @GET("glavnaya/okno.php?action=okno&android=${BuildConfig.VERSION_NAME}")
    suspend fun getPopupWindowInfo(
        @Query("userid") userId: Long,
    ): Response<VodovozResponseDTO<PopupWindowDTO>>

    /**
     * Favorite requests
     * */
    @GET("osnova/izbrannoe/izbrannoe.php?action=izbrannoe")
    suspend fun getFavoriteProducts(
        @Query("nav") page: Int = 1,
        @Query("userid") userId: Long? = null,
        @Query("sect") categoryId: Int? = null,
        @Query("sort") sort: String = "",
        @Query("ascdesc") order: String = "",
        @Query("id") productsIds: String? = null,
    ): Response<VodovozResponseDTO<ProductsSectionDTO>>

    @GET("osnova/izbrannoe/adddel.php?action=add")
    suspend fun addToFavorites(
        @Query("id") productId: Long,
        @Query("userid") userId: Long,
    ): Response<VodovozResponseDTO<String>>

    @GET("osnova/izbrannoe/adddel.php?action=del")
    suspend fun removeFromFavorites(
        @Query("id") productId: Long,
        @Query("userid") userId: Long,
    ): Response<VodovozResponseDTO<String>>

}