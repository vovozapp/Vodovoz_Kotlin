package com.vodovoz.app.data.vodovoz_service.repository

import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.core.network.messageWithCode
import com.vodovoz.app.data.model.common.BannerEntity
import com.vodovoz.app.data.vodovoz_service.VodovozService
import com.vodovoz.app.data.vodovoz_service.mappers.executeRequest
import com.vodovoz.app.data.vodovoz_service.mappers.mapToDomain
import com.vodovoz.app.domain.general.model.OrderWithMenuModel
import com.vodovoz.app.domain.general.model.PopularCategoryModel
import com.vodovoz.app.domain.general.model.ProductModel
import com.vodovoz.app.domain.general.model.PromotionModel
import com.vodovoz.app.domain.general.model.PromotionsWithSectionsModel
import com.vodovoz.app.domain.general.model.RequestException
import com.vodovoz.app.domain.general.model.TopAndBottomSectionsModel
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.util.extensions.catchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class VodovozServiceRepositoryImpl @Inject constructor(
    private val vodovozService: VodovozService,
    private val accountManager: AccountManager,
) : VodovozServiceRepository {

    //todo - finish that
    override fun getSlider(): Flow<Result<List<BannerEntity>>> = flow<Result<List<BannerEntity>>> {
        val response = vodovozService.getSlider()
        if (response.isSuccessful) {
            response.body()?.data
        } else {
            throw RequestException(response.messageWithCode())
        }
    }.catchResult()

    override fun getPromotions(): Flow<Result<List<PromotionModel>>> = executeRequest(
        request = {
            vodovozService.getPromotions()
        },
        mapToResult = { promotionsDTOVodovozResponseDTO ->
            promotionsDTOVodovozResponseDTO.data?.DATA?.mapToDomain()!!
        }
    )

    override fun getPromotionsWithSections(
        page: Int,
        limit: Int,
    ): Flow<Result<PromotionsWithSectionsModel>> = executeRequest(
        request = { vodovozService.getPromotionsWithSections(page, limit) },
        mapToResult = { response ->
            response.data!!.mapToDomain()
        },
    )


    override fun getOrderMenu(userId: Long?): Flow<Result<OrderWithMenuModel>> = executeRequest(
        request = {
            vodovozService.getOrderMenu(userId ?: accountManager.fetchAccountId() ?: -1)
        },
        mapToResult = {
            it.data?.mapToDomain()!!
        }
    )


    override fun getPopularSections(): Flow<Result<List<PopularCategoryModel>>> = executeRequest(
        request = {
            vodovozService.getPopularSections()
        },
        mapToResult = { popularCategoriesDTOVodovozResponseDTO ->
            popularCategoriesDTOVodovozResponseDTO.data?.mapToDomain()!!
        }
    )

    override fun getNewProducts(): Flow<Result<List<ProductModel>>> = executeRequest(
        request = {
            vodovozService.getNewProducts()
        },
        mapToResult = { titleAndProductsDTOVodovozResponseDTO ->
            titleAndProductsDTOVodovozResponseDTO.data?.DATA?.mapToDomain()!!
        }
    )


    override fun getHurryUpBuyProducts(): Flow<Result<List<ProductModel>>> = executeRequest(
        request = {
            vodovozService.getHurryUpBuyProducts()
        },
        mapToResult = { titleAndProductsDTOVodovozResponseDTO ->
            titleAndProductsDTOVodovozResponseDTO.data?.DATA?.mapToDomain()!!
        }
    )

    override fun getSuperTop(): Flow<Result<TopAndBottomSectionsModel>> = executeRequest(
        request = {
            vodovozService.getSuperTop()
        },
        mapToResult = { topAndBottomDTO ->
            topAndBottomDTO.data!!.mapToDomain()!!
        }
    )
}
