package com.vodovoz.app.data.model.common

import com.vodovoz.app.data.parser.response.comment.AllCommentsByProductResponseJsonParser

class ProductDetailsBundleEntity(
    val productDetailEntity: ProductDetailEntity,
    val serviceEntityList: List<ServiceEntity> = listOf(),
    val categoryEntity: CategoryEntity,
    val commentImages: List<AllCommentsByProductResponseJsonParser.CommentImageEntity>? = null,
    val commentEntityList: List<CommentEntity> = listOf(),
    val searchWordList: List<String> = listOf(),
    val maybeLikeProductEntityList: List<ProductEntity> = listOf(),
    val promotionsActionEntity: PromotionsActionEntity? = null,
    val recommendProductEntityList: List<ProductEntity> = listOf(),
    val buyWithProductEntityList: List<ProductEntity> = listOf(),
    val replacementProductsCategoryDetail: CategoryDetailEntity? = null
)