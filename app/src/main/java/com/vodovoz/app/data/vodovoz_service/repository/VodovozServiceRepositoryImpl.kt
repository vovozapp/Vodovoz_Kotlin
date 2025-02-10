package com.vodovoz.app.data.vodovoz_service.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.data.vodovoz_service.VodovozService
import com.vodovoz.app.data.vodovoz_service.mappers.executeRequest
import com.vodovoz.app.data.vodovoz_service.mappers.toDomain
import com.vodovoz.app.domain.general.VodovozPagingSource
import com.vodovoz.app.domain.general.model.BannerModel
import com.vodovoz.app.domain.general.model.OrderWithMenuModel
import com.vodovoz.app.domain.general.model.PopularCategoryModel
import com.vodovoz.app.domain.general.model.PopupWindowInfoModel
import com.vodovoz.app.domain.general.model.ProductDetailsScreenModel
import com.vodovoz.app.domain.general.model.ProductModel
import com.vodovoz.app.domain.general.model.ProductsTitle
import com.vodovoz.app.domain.general.model.PromotionDetailsModel
import com.vodovoz.app.domain.general.model.PromotionModel
import com.vodovoz.app.domain.general.model.SectionModel
import com.vodovoz.app.domain.general.model.SectionPromotionsWithFiltersModel
import com.vodovoz.app.domain.general.model.StoryModel
import com.vodovoz.app.domain.general.model.TopAndBottomSectionsModel
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class VodovozServiceRepositoryImpl @Inject constructor(
    private val vodovozService: VodovozService,
    private val accountManager: AccountManager,
) : VodovozServiceRepository {
    override fun getProductDetails(productId: Long): Flow<Result<ProductDetailsScreenModel>> =
        executeRequest(
            request = {
                vodovozService.getProductDetails(productId)
            },
            mapper = { responseDto ->
                responseDto.data?.toDomain() ?: throw IllegalArgumentException("ProductDetails cannot be null")
            }
        )


    override fun getPopupWindowInfo(): Flow<Result<PopupWindowInfoModel>> = executeRequest(
        request = {
            val userId = accountManager.fetchAccountId()
                ?: throw IllegalStateException("User is not authenticated")
            vodovozService.getPopupWindowInfo(userId)
        },
        mapper = { responseDTO ->
            responseDTO.data?.toDomain()!!
        }
    )

    override fun getStories(): Flow<Result<List<StoryModel>>> = executeRequest(
        request = {
            vodovozService.getStories()
        },
        mapper = { responseDTO ->
            responseDTO.data?.toDomain()!!
        }
    )

    override fun getBanners(): Flow<Result<List<BannerModel>>> = executeRequest(
        request = {
            vodovozService.getBanners()
        },
        mapper = { promotionsDTOVodovozResponseDTO ->
            promotionsDTOVodovozResponseDTO.data?.toDomain()!!
        }
    )

    override fun getPromotions(): Flow<Result<SectionPromotionsWithFiltersModel>> = executeRequest(
        request = {
            vodovozService.getPromotions()
        },
        mapper = { promotionsDTOVodovozResponseDTO ->
            promotionsDTOVodovozResponseDTO.data?.toDomain()!!
        }
    )

    override fun getPromotionDetails(promotionId: Int): Flow<Result<Pair<ProductsTitle, PromotionDetailsModel>>> =
        executeRequest(
            request = {
                vodovozService.getPromotionDetails(promotionId)
            },
            mapper = { response ->
                ProductsTitle(
                    response.data?.TOVAR?.NAMETOVAR ?: ""
                ) to response.data?.AKCIYA?.toDomain()!!
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
                            mapper = { promotionDetailsDTOVodovozResponseDTO ->
                                promotionDetailsDTOVodovozResponseDTO.data?.TOVAR?.DATA?.toDomain()
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
        mapper = { response ->
            response.data!!.toDomain()
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
                            mapper = { promotionsDTOVodovozResponseDTO ->
                                promotionsDTOVodovozResponseDTO.data?.toDomain()?.promotions
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
        mapper = {
            it.data?.toDomain()!!
        }
    )


    override fun getPopularCategories(): Flow<Result<SectionModel<PopularCategoryModel>>> =
        executeRequest(
            request = {
                vodovozService.getPopularSections()
            },
            mapper = { popularCategoriesDTOVodovozResponseDTO ->
                popularCategoriesDTOVodovozResponseDTO.data?.toDomain()!!
            }
        )

    override fun getNewProducts(): Flow<Result<SectionModel<ProductModel>>> = executeRequest(
        request = {
            vodovozService.getNewProducts()
        },
        mapper = { razdelDTO ->
            razdelDTO.data?.toDomain()!!
        }
    )


    override fun getHurryUpBuyProducts(): Flow<Result<SectionModel<ProductModel>>> = executeRequest(
        request = {
            vodovozService.getHurryUpBuyProducts()
        },
        mapper = { razdelDTO ->
            razdelDTO.data?.toDomain()!!
        }
    )

    override fun getSuperTop(): Flow<Result<TopAndBottomSectionsModel>> = executeRequest(
        request = {
            vodovozService.getSuperTop()
        },
        mapper = { topAndBottomDTO ->
            topAndBottomDTO.data!!.toDomain()!!
        }
    )
}
