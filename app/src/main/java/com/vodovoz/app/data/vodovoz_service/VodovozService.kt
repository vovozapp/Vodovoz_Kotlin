package com.vodovoz.app.data.vodovoz_service

import com.vodovoz.app.data.vodovoz_service.model.OrderMenuDTO
import com.vodovoz.app.data.vodovoz_service.model.PopularCategoriesDTO
import com.vodovoz.app.data.vodovoz_service.model.PromotionDetailsDTO
import com.vodovoz.app.data.vodovoz_service.model.PromotionsDTO
import com.vodovoz.app.data.vodovoz_service.model.SliderDTO
import com.vodovoz.app.data.vodovoz_service.model.SuperTopAndBottomSectionsDTO
import com.vodovoz.app.data.vodovoz_service.model.TitleAndProductsDTO
import com.vodovoz.app.data.vodovoz_service.model.VodovozResponseDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface VodovozService {

    @GET("glavnaya/akcii.php?action=akcii&limit=10")
    suspend fun getPromotions(): Response<VodovozResponseDTO<PromotionsDTO>>

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


    @GET("glavnaya/slayders/index.php?action=slayder")
    suspend fun getSlider(): Response<VodovozResponseDTO<List<SliderDTO>>>

    @GET("glavnaya/menushka.php?action=glavnaya")
    suspend fun getOrderMenu(
        @Query("userid") userId: Long,
    ): Response<VodovozResponseDTO<OrderMenuDTO>>

    @GET("glavnaya/razdel.php?action=popylrazdel")
    suspend fun getPopularSections(): Response<VodovozResponseDTO<PopularCategoriesDTO>>

    @GET("glavnaya/novinki.php?new=novinki")
    suspend fun getNewProducts(): Response<VodovozResponseDTO<TitleAndProductsDTO>>

    //todo - change return type
    @GET("glavnaya/viewedproduct/index.php?action=viewed")
    suspend fun getViewedProducts(
        @Query("userid") userId: Long,
    ): Response<Any>

    @GET("glavnaya/novinki.php?new=specpredlosh")
    suspend fun getHurryUpBuyProducts(): Response<VodovozResponseDTO<TitleAndProductsDTO>>

    @GET("glavnaya/super_top.php?action=topglav")
    suspend fun getSuperTop(): Response<VodovozResponseDTO<SuperTopAndBottomSectionsDTO>>

}