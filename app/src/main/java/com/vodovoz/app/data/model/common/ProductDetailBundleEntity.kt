package com.vodovoz.app.data.model.common

class ProductDetailBundleEntity(
    val productDetailEntity: ProductDetailEntity,
    val serviceEntityList: List<ServiceEntity> = listOf(),
    val categoryEntity: CategoryEntity,
    val commentEntityList: List<CommentEntity> = listOf(),
    val searchWordList: List<String> = listOf(),
    val maybeLikeProductEntityList: List<ProductEntity> = listOf(),
    val promotionEntityList: List<PromotionEntity> = listOf(),
    val recommendProductEntityList: List<ProductEntity> = listOf(),
    val buyWithProductEntityList: List<ProductEntity> = listOf(),
)