package com.vodovoz.app.data.vodovoz_service.repository

import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.core.network.messageWithCode
import com.vodovoz.app.data.model.common.BannerEntity
import com.vodovoz.app.data.vodovoz_service.VodovozService
import com.vodovoz.app.data.vodovoz_service.mappers.mapToDomain
import com.vodovoz.app.domain.general.model.OrderWithMenuModel
import com.vodovoz.app.domain.general.model.PopularCategoryModel
import com.vodovoz.app.domain.general.model.ProductModel
import com.vodovoz.app.domain.general.model.PromotionModel
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

    override fun getPromotions(): Flow<Result<List<PromotionModel>>> = flow {
        val response = vodovozService.getPromotions()

        val body = response.body()
        if (response.isSuccessful) {
            val promotionModelList =
                body?.data?.DATA?.mapToDomain()
                    ?: throw RequestException(response.messageWithCode())
            emit(Result.success(promotionModelList))
        } else {
            throw RequestException(response.messageWithCode())
        }
    }.catchResult()

    override fun getOrderMenu(userId: Long?): Flow<Result<OrderWithMenuModel>> = flow {
        val response = vodovozService.getOrderMenu(userId ?: accountManager.fetchAccountId() ?: -1)

        if (response.isSuccessful) {
            val orderWithMenu = response.body()?.data?.mapToDomain() ?: throw RequestException(
                response.messageWithCode()
            )
            emit(Result.success(orderWithMenu))
        } else {
            throw RequestException(response.messageWithCode())
        }
    }.catchResult()

    override fun getPopularSections(): Flow<Result<List<PopularCategoryModel>>> = flow {
        val response = vodovozService.getPopularSections()

        if (response.isSuccessful) {
            val popularSections = response.body()?.data?.mapToDomain() ?: throw RequestException(
                response.messageWithCode()
            )
            emit(Result.success(popularSections))
        } else throw RequestException(response.messageWithCode())
    }.catchResult()

    override fun getNewProducts(): Flow<Result<List<ProductModel>>> = flow {
        val response = vodovozService.getNewProducts()

        if (response.isSuccessful) {
            val newProducts = response.body()?.data?.DATA?.mapToDomain() ?: throw RequestException(
                response.messageWithCode()
            )
            emit(Result.success(newProducts))
        } else throw RequestException(response.messageWithCode())
    }.catchResult()

    override fun getHurryUpBuyProducts(): Flow<Result<List<ProductModel>>> = flow {
        val response = vodovozService.getHurryUpBuyProducts()

        if (response.isSuccessful) {
            val hurryUpBuyProducts =
                response.body()?.data?.DATA?.mapToDomain() ?: throw RequestException(
                    response.messageWithCode()
                )
            emit(Result.success(hurryUpBuyProducts))
        } else {
            throw RequestException(response.messageWithCode())
        }
    }.catchResult()

    override fun getSuperTop(): Flow<Result<TopAndBottomSectionsModel>> = flow {
        val response = vodovozService.getSuperTop()

        if (response.isSuccessful) {
            val sections = response.body()?.data?.mapToDomain() ?: throw RequestException(
                response.messageWithCode()
            )
            emit(Result.success(sections))

        } else {
            throw RequestException(response.messageWithCode())
        }
    }.catchResult()

}