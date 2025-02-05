package com.vodovoz.app.data.vodovoz_service.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.core.network.messageWithCode
import com.vodovoz.app.data.model.common.BannerEntity
import com.vodovoz.app.data.vodovoz_service.VodovozService
import com.vodovoz.app.data.vodovoz_service.mappers.executeRequest
import com.vodovoz.app.data.vodovoz_service.mappers.mapToDomain
import com.vodovoz.app.domain.general.VodovozPagingSource
import com.vodovoz.app.domain.general.model.OrderWithMenuModel
import com.vodovoz.app.domain.general.model.PopularCategoryModel
import com.vodovoz.app.domain.general.model.ProductModel
import com.vodovoz.app.domain.general.model.ProductsTitle
import com.vodovoz.app.domain.general.model.PromotionDetailsModel
import com.vodovoz.app.domain.general.model.PromotionModel
import com.vodovoz.app.domain.general.model.SectionPromotionsWithFiltersModel
import com.vodovoz.app.domain.general.model.RequestException
import com.vodovoz.app.domain.general.model.SectionModel
import com.vodovoz.app.domain.general.model.TopAndBottomSectionsModel
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.util.extensions.catchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
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

    override fun getPromotions(): Flow<Result<SectionPromotionsWithFiltersModel>> = executeRequest(
        request = {
            vodovozService.getPromotions()
        },
        mapToResult = { promotionsDTOVodovozResponseDTO ->
            promotionsDTOVodovozResponseDTO.data?.mapToDomain()!!
        }
    )

    override fun getPromotionDetails(promotionId: Int): Flow<Result<Pair<ProductsTitle, PromotionDetailsModel>>> =
        executeRequest(
            request = {
                vodovozService.getPromotionDetails(promotionId)
            },
            mapToResult = { response ->
                ProductsTitle(
                    response.data?.TOVAR?.NAMETOVAR ?: ""
                ) to response.data?.AKCIYA?.mapToDomain()!!
            }
        )


    override fun getPromotionDetailsProductsPaged(
        promotionId: Int,
        page: Int,
        limit: Int,
    ): Flow<PagingData<ProductModel>> {
        return Pager(
            config = PagingConfig(limit, initialLoadSize = limit),
            pagingSourceFactory = {
                VodovozPagingSource(
                    request = { page, limit ->
                        executeRequest(
                            request = {
                                vodovozService.getPromotionDetails(
                                    promotionId,
                                    page,
                                    limit
                                )
                            },
                            mapToResult = { promotionDetailsDTOVodovozResponseDTO ->
                                promotionDetailsDTOVodovozResponseDTO.data?.TOVAR?.DATA?.mapToDomain()
                                    ?: emptyList()
                            }
                        ).firstOrNull() ?: Result.failure(Throwable())
                    }
                )
            }
        ).flow
    }


    override fun getPromotionsWithSections(
        page: Int,
        limit: Int,
    ): Flow<Result<SectionPromotionsWithFiltersModel>> = executeRequest(
        request = { vodovozService.getPromotionsWithSections(page, limit) },
        mapToResult = { response ->
            response.data!!.mapToDomain()
        },
    )

    override fun getPromotionsPaged(page: Int, limit: Int): Flow<PagingData<PromotionModel>> {
        return Pager(
            config = PagingConfig(limit, initialLoadSize = limit),
            pagingSourceFactory = {
                VodovozPagingSource(
                    request = { page, limit ->
                        executeRequest(
                            request = {
                                vodovozService.getPromotionsWithSections(page, limit)
                            },
                            mapToResult = { promotionsDTOVodovozResponseDTO ->
                                promotionsDTOVodovozResponseDTO.data?.mapToDomain()?.promotions
                                    ?: emptyList()
                            }
                        ).firstOrNull() ?: Result.failure(Throwable())
                    }
                )
            }
        ).flow
    }


    override fun getOrderMenu(userId: Long?): Flow<Result<OrderWithMenuModel>> = executeRequest(
        request = {
            vodovozService.getOrderMenu(userId ?: accountManager.fetchAccountId() ?: -1)
        },
        mapToResult = {
            it.data?.mapToDomain()!!
        }
    )


    override fun getPopularCategories(): Flow<Result<SectionModel<PopularCategoryModel>>> =
        executeRequest(
            request = {
                vodovozService.getPopularSections()
            },
            mapToResult = { popularCategoriesDTOVodovozResponseDTO ->
                popularCategoriesDTOVodovozResponseDTO.data?.mapToDomain()!!
            }
        )

    override fun getNewProducts(): Flow<Result<SectionModel<ProductModel>>> = executeRequest(
        request = {
            vodovozService.getNewProducts()
        },
        mapToResult = { razdelDTO ->
            razdelDTO.data?.mapToDomain()!!
        }
    )


    override fun getHurryUpBuyProducts(): Flow<Result<SectionModel<ProductModel>>> = executeRequest(
        request = {
            vodovozService.getHurryUpBuyProducts()
        },
        mapToResult = { razdelDTO ->
            razdelDTO.data?.mapToDomain()!!
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
