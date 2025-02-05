package com.vodovoz.app.domain.general.respository

import androidx.paging.PagingData
import com.vodovoz.app.domain.general.model.BannerModel
import com.vodovoz.app.domain.general.model.OrderWithMenuModel
import com.vodovoz.app.domain.general.model.PopularCategoryModel
import com.vodovoz.app.domain.general.model.ProductModel
import com.vodovoz.app.domain.general.model.ProductsTitle
import com.vodovoz.app.domain.general.model.PromotionDetailsModel
import com.vodovoz.app.domain.general.model.PromotionModel
import com.vodovoz.app.domain.general.model.SectionModel
import com.vodovoz.app.domain.general.model.SectionPromotionsWithFiltersModel
import com.vodovoz.app.domain.general.model.StoryModel
import com.vodovoz.app.domain.general.model.TopAndBottomSectionsModel
import kotlinx.coroutines.flow.Flow

interface VodovozServiceRepository {

    fun getStories(): Flow<Result<List<StoryModel>>>

    fun getBanners(): Flow<Result<List<BannerModel>>>

    fun getPromotions(): Flow<Result<SectionPromotionsWithFiltersModel>>

    fun getPromotionDetails(
        promotionId: Int,
    ): Flow<Result<Pair<ProductsTitle, PromotionDetailsModel>>>

    fun getPromotionDetailsProductsPaged(
        promotionId: Int,
        page: Int = 1,
        limit: Int = 5,
    ): Flow<PagingData<ProductModel>>

    fun getPromotionsWithSections(
        page: Int = 1,
        limit: Int = 10,
    ): Flow<Result<SectionPromotionsWithFiltersModel>>

    fun getPromotionsPaged(
        page: Int = 1,
        limit: Int = 10,
    ): Flow<PagingData<PromotionModel>>

    fun getOrderMenu(
        userId: Long? = null,
    ): Flow<Result<OrderWithMenuModel>>

    fun getPopularCategories(): Flow<Result<SectionModel<PopularCategoryModel>>>

    fun getNewProducts(): Flow<Result<SectionModel<ProductModel>>>

    fun getHurryUpBuyProducts(): Flow<Result<SectionModel<ProductModel>>>

    fun getSuperTop(): Flow<Result<TopAndBottomSectionsModel>>

}