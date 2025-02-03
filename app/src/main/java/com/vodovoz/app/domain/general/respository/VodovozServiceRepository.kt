package com.vodovoz.app.domain.general.respository

import com.vodovoz.app.data.model.common.BannerEntity
import com.vodovoz.app.domain.general.model.OrderWithMenuModel
import com.vodovoz.app.domain.general.model.PopularCategoryModel
import com.vodovoz.app.domain.general.model.ProductModel
import com.vodovoz.app.domain.general.model.PromotionModel
import com.vodovoz.app.domain.general.model.TopAndBottomSectionsModel
import kotlinx.coroutines.flow.Flow

interface VodovozServiceRepository {

    fun getSlider(): Flow<Result<List<BannerEntity>>>

    fun getPromotions(): Flow<Result<List<PromotionModel>>>

    fun getOrderMenu(
        userId: Long? = null
    ): Flow<Result<OrderWithMenuModel>>

    fun getPopularSections(): Flow<Result<List<PopularCategoryModel>>>

    fun getNewProducts(): Flow<Result<List<ProductModel>>>

    fun getHurryUpBuyProducts(): Flow<Result<List<ProductModel>>>

    fun getSuperTop(): Flow<Result<TopAndBottomSectionsModel>>

}