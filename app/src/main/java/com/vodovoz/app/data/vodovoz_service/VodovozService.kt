package com.vodovoz.app.data.vodovoz_service

import com.vodovoz.app.data.vodovoz_service.model.BannerDTO
import com.vodovoz.app.data.vodovoz_service.model.OrderMenuDTO
import com.vodovoz.app.data.vodovoz_service.model.PopularCategoriesDTO
import com.vodovoz.app.data.vodovoz_service.model.PopupWindowDTO
import com.vodovoz.app.data.vodovoz_service.model.PresentDTO
import com.vodovoz.app.data.vodovoz_service.model.ProductCommentsDTO
import com.vodovoz.app.data.vodovoz_service.model.ProductsSectionDTO
import com.vodovoz.app.data.vodovoz_service.model.PromotionDetailsDTO
import com.vodovoz.app.data.vodovoz_service.model.PromotionsDTO
import com.vodovoz.app.data.vodovoz_service.model.RAZDEL_DTO
import com.vodovoz.app.data.vodovoz_service.model.StoriesDTO
import com.vodovoz.app.data.vodovoz_service.model.SuperTopAndBottomSectionsDTO
import com.vodovoz.app.data.vodovoz_service.model.VodovozResponseDTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.ProductDetailsDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface VodovozService {


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

    @GET("details/analog.php?id=105622")
    suspend fun getProductAnalogs(
        @Query("id") productId: Long,
        @Query("sort") sort: String = "",
        @Query("ascdesc") order: String = "",
    ): Response<VodovozResponseDTO<ProductsSectionDTO>>


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
     *
     */
    @GET("glavnaya/slayders/index.php?action=slayder")
    suspend fun getBanners(): Response<VodovozResponseDTO<List<BannerDTO>>>

    @GET("glavnaya/stories/index.php?iblock_id=12&action=stories&platforma=android")
    suspend fun getStories(): Response<VodovozResponseDTO<StoriesDTO>>

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

    //todo - change return type
    @GET("glavnaya/viewedproduct/index.php?action=viewed")
    suspend fun getViewedProducts(
        @Query("userid") userId: Long,
    ): Response<Any>

    @GET("glavnaya/novinki.php?new=specpredlosh")
    suspend fun getHurryUpBuyProducts(): Response<VodovozResponseDTO<RAZDEL_DTO>>

    @GET("glavnaya/super_top.php?action=topglav")
    suspend fun getSuperTop(): Response<VodovozResponseDTO<SuperTopAndBottomSectionsDTO>>

    @GET("glavnaya/okno.php?action=okno")
    suspend fun getPopupWindowInfo(
        @Query("userid") userId: Long,
    ): Response<VodovozResponseDTO<PopupWindowDTO>>

}