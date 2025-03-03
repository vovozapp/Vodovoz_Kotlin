package com.vodovoz.app.data.vodovoz_service

import com.vodovoz.app.BuildConfig
import com.vodovoz.app.data.vodovoz_service.model.AnalogsSectionDTO
import com.vodovoz.app.data.vodovoz_service.model.BannerDTO
import com.vodovoz.app.data.vodovoz_service.model.MiniSearchRecommendationsDTO
import com.vodovoz.app.data.vodovoz_service.model.OrderMenuDTO
import com.vodovoz.app.data.vodovoz_service.model.PopularCategoriesDTO
import com.vodovoz.app.data.vodovoz_service.model.PopupWindowDTO
import com.vodovoz.app.data.vodovoz_service.model.PreOrderDTO
import com.vodovoz.app.data.vodovoz_service.model.PreOrderResponseDTO
import com.vodovoz.app.data.vodovoz_service.model.PresentDTO
import com.vodovoz.app.data.vodovoz_service.model.ProductCommentsDTO
import com.vodovoz.app.data.vodovoz_service.model.ProductsSectionDTO
import com.vodovoz.app.data.vodovoz_service.model.PromotionDetailsDTO
import com.vodovoz.app.data.vodovoz_service.model.PromotionsDTO
import com.vodovoz.app.data.vodovoz_service.model.RAZDEL_DTO
import com.vodovoz.app.data.vodovoz_service.model.RegistrationSectionDTO
import com.vodovoz.app.data.vodovoz_service.model.SearchRecommendationsDTO
import com.vodovoz.app.data.vodovoz_service.model.SiteStateResponseDTO
import com.vodovoz.app.data.vodovoz_service.model.StoriesDTO
import com.vodovoz.app.data.vodovoz_service.model.SuperTopAndBottomSectionsDTO
import com.vodovoz.app.data.vodovoz_service.model.VodovozResponseDTO
import com.vodovoz.app.data.vodovoz_service.model.catalog_details.CatalogDetailsDTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.ProductDetailsDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface VodovozService {

    /**
     * Catalog screen
     * */
    @GET("razdel/category.php?iblock_id=12")
    suspend fun getCatalogDetails(): Response<VodovozResponseDTO<CatalogDetailsDTO>>

    /**
     * Search requests
     * */
    @GET("searching/index.php?action=glav")
    suspend fun getSearchRecommendations(): Response<VodovozResponseDTO<SearchRecommendationsDTO>>

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
    ): Response<VodovozResponseDTO<ProductsSectionDTO>>

    /**
     * Login requests
     * */
    @GET("reg.php?action=glav")
    suspend fun getRegisterFields(): Response<VodovozResponseDTO<RegistrationSectionDTO>>

    @GET("reg.php?action=otpravka")
    suspend fun register(
        @Query("name") name: String,
        @Query("lastname") lastName: String,
        @Query("phone") phone: String,
        @Query("email") email: String,
        @Query("pass") code: Int,
    ): Response<Unit>

    //TODO - change return type
    @GET("config/openuserid.php?&android=${BuildConfig.VERSION_NAME}")
    suspend fun relogin(
        @Query("userid") userId: Long,
        @Query("token") token: String,
    ): Response<Unit>

    /**
     * Main requests
     * */
    @GET("config/closesait.php?action=saitosnova&android=${BuildConfig.VERSION_NAME}")
    suspend fun getSiteState(): Response<SiteStateResponseDTO>

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
        @Query("userid") userId: Long,
        @Query("tovar") productId: Long,
        @QueryMap queries: Map<String, String>,
    ): Response<PreOrderResponseDTO>

    /**
     * Cart requests
     * */
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

    @GET("newmobile_new/korzina/function/delkorzina/index.php?action=delkorzina")
    suspend fun clearCart(): Response<VodovozResponseDTO<String>>


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
        @Query("userid") userId: String,
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
     * Promotions screens
     */
    @GET("glavnaya/akcii.php?action=akcii")
    suspend fun getPromotionsWithSections(
        @Query("nav") page: Int = 1,
        @Query("limit") limit: Int = 10,
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

    @GET("https://vodovoz.net/newmobile_new/glavnaya/slayders/index.php?action=detailaction&android=${BuildConfig.VERSION_NAME}")
    suspend fun getBannersPromotions(
        @Query("id") bannerId: Long,
        @Query("nav") page: Int = 1,
    )

    @GET("https://vodovoz.net/newmobile_new/glavnaya/slayders/index.php?action=detailtovar&android=${BuildConfig.VERSION_NAME}")
    suspend fun getBannersProducts(
        @Query("id") bannerId: Long,
        @Query("nav") page: Int = 1,
        @Query("sect") categoryId: Int = -1,
        @Query("sort") sort: String = "",
        @Query("ascdesc") order: String = "",
    )

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
        @Query("userid") userId: Long,
    ): Response<VodovozResponseDTO<OrderMenuDTO>>

    @GET("glavnaya/razdel.php?action=popylrazdel")
    suspend fun getPopularSections(): Response<VodovozResponseDTO<PopularCategoriesDTO>>

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
     * Favorite screen
     * */
    @GET("izbrannoe.php?action=izbrannoe")
    suspend fun getFavoriteProducts(
        @Query("userid") userId: Long,
        @Query("nav") page: Int = 1,
        @Query("sect") categoryId: Int = -1,
        @Query("sort") sort: String = "",
        @Query("ascdesc") order: String = "",
    ): Response<VodovozResponseDTO<ProductsSectionDTO>>

    @GET("izbrannoe.php?action=izbrannoe")
    suspend fun addFavoriteProducts(
        @Query("userid") userId: Long,
        @Query("id") ids: String,
    ): Response<VodovozResponseDTO<ProductsSectionDTO>>

}