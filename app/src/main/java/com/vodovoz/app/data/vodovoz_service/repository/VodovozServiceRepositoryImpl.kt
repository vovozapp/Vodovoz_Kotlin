package com.vodovoz.app.data.vodovoz_service.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.squareup.moshi.Moshi
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.core.network.messageWithCode
import com.vodovoz.app.data.vodovoz_service.VodovozService
import com.vodovoz.app.data.vodovoz_service.mappers.executeRequest
import com.vodovoz.app.data.vodovoz_service.mappers.mapToDomain
import com.vodovoz.app.data.vodovoz_service.mappers.toDomain
import com.vodovoz.app.data.vodovoz_service.model.ErrorMessageResponseDTO
import com.vodovoz.app.data.vodovoz_service.model.PreOrderResponseDTO
import com.vodovoz.app.domain.general.VodovozPagingSource
import com.vodovoz.app.domain.general.model.BannerModel
import com.vodovoz.app.domain.general.model.CatalogDetailsModel
import com.vodovoz.app.domain.general.model.CommentModel
import com.vodovoz.app.domain.general.model.EmptyResultException
import com.vodovoz.app.domain.general.model.FavoriteNotFoundException
import com.vodovoz.app.domain.general.model.FieldModel
import com.vodovoz.app.domain.general.model.OrderWithMenuModel
import com.vodovoz.app.domain.general.model.PopularCategoryModel
import com.vodovoz.app.domain.general.model.PopupWindowInfoModel
import com.vodovoz.app.domain.general.model.PreOrderSectionModel
import com.vodovoz.app.domain.general.model.ProductCommentsInfoModel
import com.vodovoz.app.domain.general.model.ProductDetailsScreenModel
import com.vodovoz.app.domain.general.model.ProductModel
import com.vodovoz.app.domain.general.model.ProductsSectionModel
import com.vodovoz.app.domain.general.model.ProductsTitle
import com.vodovoz.app.domain.general.model.PromotionDetailsModel
import com.vodovoz.app.domain.general.model.PromotionModel
import com.vodovoz.app.domain.general.model.PromotionsSectionModel
import com.vodovoz.app.domain.general.model.RequestException
import com.vodovoz.app.domain.general.model.SearchRecommendationsModel
import com.vodovoz.app.domain.general.model.SectionModel
import com.vodovoz.app.domain.general.model.SiteStateModel
import com.vodovoz.app.domain.general.model.SortModel
import com.vodovoz.app.domain.general.model.StoryModel
import com.vodovoz.app.domain.general.model.TopAndBottomSectionsModel
import com.vodovoz.app.domain.general.model.ValidationException
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject


class VodovozServiceRepositoryImpl @Inject constructor(
    private val vodovozService: VodovozService,
    private val accountManager: AccountManager,
) : VodovozServiceRepository {

    override fun getCatalogDetails(): Flow<Result<CatalogDetailsModel>> {
        return executeRequest(
            request = {
                vodovozService.getCatalogDetails()
            },
            mapper = { response ->
                response.data?.toDomain()!!
            }
        )
    }


    override fun getSearchProductsPaged(
        query: String,
        categoryId: Int,
        sort: SortModel,
    ): Flow<PagingData<ProductModel>> {
        return Pager(
            config = PagingConfig(pageSize = 5, initialLoadSize = 5),
            pagingSourceFactory = {
                VodovozPagingSource(
                    request = { page, _ ->
                        vodovozService.getSearchProducts(
                            query = query,
                            page = page,
                            categoryId = if (categoryId == -1) null else categoryId,
                            sort = sort.value,
                            order = sort.order
                        )
                    },
                    mapper = { response ->
                        response.data?.TOVAR?.mapNotNull { product -> product.toDomain() }
                            ?: throw IllegalArgumentException("Paged search products can't be null")
                    }
                )
            }
        ).flow
    }

    override fun getSearchProducts(query: String): Flow<Result<ProductsSectionModel>> {
        return executeRequest(
            request = {
                vodovozService.getSearchProducts(query = query)
            },
            mapper = { response ->
                response.data?.toDomain()!!
            }
        )
    }

    override fun getSearchRecommendations(): Flow<Result<SearchRecommendationsModel>> {
        return executeRequest(
            request = {
                vodovozService.getSearchRecommendations()
            },
            mapper = { responseDTO ->
                responseDTO.data?.toDomain()!!
            }
        )
    }

    override fun getMiniSearchRecommendations(query: String): Flow<Result<SearchRecommendationsModel>> {
        return executeRequest(
            request = {
                vodovozService.getMiniSearchRecommendations(query)
            },
            mapper = { responseDTO ->
                responseDTO.data?.toDomain()!!
            },
            onFail = { response ->
                val moshi = Moshi.Builder().build()
                val adapter = moshi.adapter(ErrorMessageResponseDTO::class.java)
                val body = (response.errorBody() ?: response.raw().body)?.string() ?: ""
                val errorMessageResponseDTO= adapter.fromJson(body)

                val throwable = when (response.code()) {
                    404 -> EmptyResultException(
                        htmlText = errorMessageResponseDTO?.message ?: "",
                        message = response.messageWithCode()
                    )
                    else -> RequestException(response.messageWithCode())
                }
                Result.failure(throwable)
            }
        )
    }

    override fun getSiteState(): Flow<Result<SiteStateModel>> {
        return executeRequest(
            request = {
                vodovozService.getSiteState()
            },
            mapper = { response ->
                response.toDomain()
            }
        )
    }

    override fun getPreorderFields(productId: Long): Flow<Result<PreOrderSectionModel>> {
        return executeRequest(
            request = {
                val userId = accountManager.fetchAccountId() ?: -1L
                vodovozService.getPreOrderFields(userId, productId)
            },
            mapper = {
                it.data?.toDomain() ?: throw IllegalArgumentException("PreorderDTO can't be null")
            }
        )
    }

    override fun sendPreorder(productId: Long, fields: List<FieldModel>): Flow<Result<String>> {
        return executeRequest(
            request = {
                val userId = accountManager.fetchAccountId() ?: -1L
                val queries =
                    fields.filter { it.value.isNotEmpty() }.associate { it.id to it.value }
                vodovozService.sendPreorder(userId, productId, queries)
            },
            mapper = { response -> response.message ?: "" },
            onFail = { response ->
                val moshi = Moshi.Builder().build()
                val adapter = moshi.adapter(PreOrderResponseDTO::class.java)
                val body = (response.errorBody() ?: response.raw().body)?.string() ?: ""
                val responseBody = adapter.fromJson(body)

                Result.failure(ValidationException(responseBody?.message ?: ""))
            }
        )
    }

    override fun getFavoriteProducts(): Flow<Result<ProductsSectionModel>> = executeRequest(
        request = {
            val userId = accountManager.fetchAccountId() ?: -1L
            vodovozService.getFavoriteProducts(userId)
        },
        mapper = { responseDTO ->
            responseDTO.data?.toDomain()
                ?: throw IllegalArgumentException("Favorite products can't be null")
        },
        onFail = { response ->
            val exception = when (response.code()) {
                404 -> FavoriteNotFoundException(response.messageWithCode())
                else -> RequestException(response.messageWithCode())
            }
            Result.failure(exception)
        }
    )

    override fun getFavoriteProductsPaged(
        categoryId: Int,
        sort: SortModel,
    ): Flow<PagingData<ProductModel>> {
        return Pager(
            config = PagingConfig(pageSize = 4, initialLoadSize = 4),
            pagingSourceFactory = {
                VodovozPagingSource(
                    request = { page, _ ->
                        val userId = accountManager.fetchAccountId() ?: -1L
                        vodovozService.getFavoriteProducts(userId, page)
                    },
                    mapper = { response ->
                        response.data?.DATA?.mapNotNull { it.toDomain() }
                            ?: throw IllegalArgumentException("Paged favorite products can't be null")
                    }
                )
            }
        ).flow
    }


    override suspend fun addProductToCart(productId: Long, quantity: Int): Flow<Result<String>> =
        executeRequest(
            request = { vodovozService.addProductToCart(productId, quantity) },
            mapper = { response -> response.data ?: "" }
        )

    override suspend fun addMultipleProductsToCart(productIdsWithQuantity: String): Flow<Result<String>> =
        executeRequest(
            request = { vodovozService.addMultipleProductsToCart(productIdsWithQuantity) },
            mapper = { response -> response.data ?: "" }
        )

    override suspend fun removeProductFromCart(productId: Long): Flow<Result<String>> =
        executeRequest(
            request = { vodovozService.removeProductFromCart(productId) },
            mapper = { response -> response.data ?: "" }
        )

    override suspend fun updateProductInCart(productId: Long, quantity: Int): Flow<Result<String>> =
        executeRequest(
            request = { vodovozService.updateProductInCart(productId, quantity) },
            mapper = { response -> response.data ?: "" }
        )

    override suspend fun clearCart(): Flow<Result<String>> = executeRequest(
        request = { vodovozService.clearCart() },
        mapper = { response -> response.data ?: "" }
    )

    override fun getProductAnalogs(
        productId: Long,
        sort: SortModel,
    ): Flow<Result<ProductsSectionModel>> = executeRequest(
        request = {
            vodovozService.getProductAnalogs(productId, sort.value, sort.order)
        },
        mapper = { response ->
            response.data?.toDomain()
                ?: throw IllegalArgumentException("ProductAnalogsDTO can't be null")
        }
    )

    override fun getProductCommentsInfo(productId: Long): Flow<Result<ProductCommentsInfoModel>> {
        return executeRequest(
            request = {
                vodovozService.getComments(productId, 1)
            },
            mapper = { response ->
                response.data?.toDomain()
                    ?: throw IllegalArgumentException("ProductCommentsDTO can't be null")
            }
        )
    }


    override fun getProductCommentsPaged(
        productId: Long,
        sort: SortModel,
    ): Flow<PagingData<CommentModel>> {
        return Pager(
            config = PagingConfig(pageSize = 10, initialLoadSize = 10),
            pagingSourceFactory = {
                VodovozPagingSource(
                    request = { page, _ ->
                        vodovozService.getComments(
                            productId = productId,
                            page = page,
                            sort = sort.value,
                            order = sort.order
                        )
                    },
                    mapper = { response ->
                        response.data?.COMMENTS?.mapNotNull { it?.toDomain() } ?: emptyList()
                    }
                )
            }
        ).flow
    }

    override fun getProductDetails(productId: Long): Flow<Result<ProductDetailsScreenModel>> =
        executeRequest(
            request = {
                vodovozService.getProductDetails(productId)
            },
            mapper = { responseDto ->
                responseDto.data?.toDomain()
                    ?: throw IllegalArgumentException("ProductDetails cannot be null")
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

    override fun getPromotions(): Flow<Result<PromotionsSectionModel>> = executeRequest(
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
        limit: Int,
    ): Flow<PagingData<ProductModel>> {
        return Pager(
            config = PagingConfig(pageSize = limit, initialLoadSize = limit),
            pagingSourceFactory = {
                VodovozPagingSource(
                    request = { page, limit ->
                        vodovozService.getPromotionDetails(promotionId, page, limit)
                    },
                    mapper = { promotionDetailsDTOVodovozResponseDTO ->
                        promotionDetailsDTOVodovozResponseDTO.data?.TOVAR?.DATA?.mapToDomain()
                            ?: emptyList()
                    }
                )
            }
        ).flow
    }


    override fun getPromotionsWithSections(): Flow<Result<PromotionsSectionModel>> =
        executeRequest(
            request = { vodovozService.getPromotionsWithSections() },
            mapper = { response ->
                response.data!!.toDomain()
            },
        )

    override fun getPromotionsPaged(limit: Int): Flow<PagingData<PromotionModel>> {
        return Pager(
            config = PagingConfig(limit, initialLoadSize = limit),
            pagingSourceFactory = {
                VodovozPagingSource(
                    request = { page, limit ->
                        vodovozService.getPromotionsWithSections(page, limit)
                    },
                    mapper = { promotionsDTOVodovozResponseDTO ->
                        promotionsDTOVodovozResponseDTO.data?.toDomain()?.promotions ?: emptyList()
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

    override fun getAllNewProducts(): Flow<Result<ProductsSectionModel>> = executeRequest(
        request = {
            vodovozService.getAllNewProducts()
        },
        mapper = { response ->
            response.data?.toDomain()
                ?: throw IllegalArgumentException("NewProductsDTO can't be null")
        }
    )

    override fun getAllNewProductsPaged(
        categoryId: Int,
        sort: SortModel,
    ): Flow<PagingData<ProductModel>> {
        return Pager(
            config = PagingConfig(4, 4),
            pagingSourceFactory = {
                VodovozPagingSource(
                    request = { page, _ ->
                        vodovozService.getAllNewProducts(
                            page = page,
                            categoryId = categoryId,
                            sort = sort.value,
                            order = sort.order
                        )
                    },
                    mapper = { response ->
                        response.data?.DATA?.mapToDomain() ?: emptyList()
                    }
                )
            }
        ).flow
    }


    override fun getHurryUpBuyProducts(): Flow<Result<SectionModel<ProductModel>>> = executeRequest(
        request = {
            vodovozService.getHurryUpBuyProducts()
        },
        mapper = { razdelDTO ->
            razdelDTO.data?.toDomain()!!
        }
    )

    override suspend fun getAllHurryUpBuyProducts(): Flow<Result<ProductsSectionModel>> =
        executeRequest(
            request = {
                vodovozService.getAllHurryUpBuyProducts()
            },
            mapper = { responseDto ->
                responseDto.data?.toDomain()
                    ?: throw IllegalArgumentException("ProductSectionDTO can't be null")
            }
        )

    override fun getAllHurryUpBuyProductsPaged(
        categoryId: Int,
        sort: SortModel,
    ): Flow<PagingData<ProductModel>> {
        return Pager(
            config = PagingConfig(4, 4),
            pagingSourceFactory = {
                VodovozPagingSource(
                    request = { page, _ ->
                        vodovozService.getAllHurryUpBuyProducts(
                            page = page,
                            categoryId = categoryId,
                            sort = sort.value,
                            order = sort.order
                        )
                    },
                    mapper = { response ->
                        response.data?.DATA?.mapToDomain() ?: emptyList()
                    }
                )
            }
        ).flow
    }

    override fun getSuperTop(): Flow<Result<TopAndBottomSectionsModel>> = executeRequest(
        request = {
            vodovozService.getSuperTop()
        },
        mapper = { topAndBottomDTO ->
            topAndBottomDTO.data?.toDomain()
                ?: throw IllegalArgumentException("SuperTop can't be null")
        }
    )


    override fun getAllSuperTop(id: Int): Flow<Result<ProductsSectionModel>> = executeRequest(
        request = {
            vodovozService.getAllSuperTop(id.toLong())
        },
        mapper = { superTopResponse ->
            superTopResponse.data?.toDomain()
                ?: throw IllegalArgumentException("AllSuperTop can't be null")
        }
    )

    override fun getAllSuperTopPaged(
        id: Int,
        categoryId: Int,
        sort: SortModel,
    ): Flow<PagingData<ProductModel>> {
        return Pager(
            config = PagingConfig(4, 4),
            pagingSourceFactory = {
                VodovozPagingSource(
                    request = { page, _ ->
                        vodovozService.getAllSuperTop(
                            id = id.toLong(),
                            page = page,
                            categoryId = categoryId,
                            sort = sort.value,
                            order = sort.order
                        )
                    },
                    mapper = { response ->
                        response.data?.DATA?.mapToDomain() ?: emptyList()
                    }
                )
            }
        ).flow
    }

    override fun getViewedProducts(): Flow<Result<SectionModel<ProductModel>>> {
        return executeRequest(
            request = {
                val userId = accountManager.fetchAccountId() ?: -1
                vodovozService.getViewedProducts(userId)
            },
            mapper = {
                it.data?.toDomain()
                    ?: throw IllegalArgumentException("Viewed products can't be null")
            }
        )
    }
}
