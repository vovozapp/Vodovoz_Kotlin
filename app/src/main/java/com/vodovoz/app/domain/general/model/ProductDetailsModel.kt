package com.vodovoz.app.domain.general.model


data class ProductDetailsScreenModel(
    val productDetails: ProductDetailsModel,
    val buttons: ProductDetailsButtonsModel,
    val moreProducts: ProductDetailsMoreProducts,
    val comments: List<CommentModel>,
    val tabs: List<ProductDetailsTabModel>,
)

data class ProductDetailsTabModel(
    val title: String,
    val dataId: String,
)

data class CommentModel(
    val userName: String,
    val userPhoto: String,
    val text: String,
    val dateText: String,
    val rating: Int,
    val purchased: String,
)


data class ProductDetailsMoreProducts(
    val sectionAccessory: SectionModel<ProductModel>,
    val sectionSimilar: SectionModel<ProductModel>,
)

data class ProductDetailsModel(
    val id: Long,
    val name: String,

    val blockBrandCategory: BrandCategoryBlockModel,
    val information: ContentBlockModel<String>?,

    val detailInfo: ContentBlockModel<String>,
    val characteristics: ContentBlockModel<List<CharacteristicsBlockModel>>,
    val documents: ContentBlockModel<List<DocumentModel>>,

    val detailPicture: String,
    val pictures: List<String>,
    val sectionTags: SectionModel<String>,
    val isFavorite: Boolean,
    val isAvailable: Boolean,
    val productQuantity: Int,
    val labels: List<LabelModel>,
    val rating: Float,
    val deposit: DepositModel?,

    val shareUrl: String,
    val shareUrlText: String,

    val youtubeVideo: ProductVideoModel?,
    val rutubeVideo: ProductVideoModel?,

    val coefficient: Float,
    val pricePerUnit: String?,
    val articleNumber: String,

    val firstPrice: PriceModel,
    val prices: List<PriceModel>,

    val commentsCount: Int
)

data class ProductVideoModel(
    val previewImage: String,
    val code: String
)

data class DocumentModel(
    val type: String,
    val size: Float,
    val sizeText: String,
    val iconUrl: String,
    val description: String,
    val src: String,
)

data class DepositModel(
    val price: Float,
    val description: ContentBlockModel<String>,
)

data class ProductDetailsButtonsModel(
    val blockButton: ButtonBlockModel?,
    val blockDesignButton: ButtonDesignBlockModel?,

    val multiBuyButton: ColorfulButtonModel?,
    val analogButton: ColorfulButtonModel?,
    val preOrderButton: ColorfulButtonModel?,
)

data class BuyButtonModel(
    val textColor: String,
    val backgroundColor: String,
    val title: String,
    val productId: String,
    val moreProductId: String,
)

data class ButtonBlockModel(
    val button: ColorfulButtonModel,
    val data: BlockPromoDataModel,
    val buyButton: BuyButtonModel,
)

data class ButtonDesignBlockModel(
    val block: DesignBlockModel,
    val data: BlockPromoDataModel,
    val buyButton: BuyButtonModel,
)

data class BlockPromoDataModel(
    val title: String,
    val description: String,
    val productQuantityText: String,
    val product: PromoProductModel,
)

data class PromoProductModel(
    val name: String,
    val image: String,
    val price: OldNewPriceModel,
)

data class OldNewPriceModel(
    val new: String,
    val old: String,
)

data class DesignBlockModel(
    val title: String,
    val image: String,
    val background: String,
    val textColor: String,
    val borderColor: String,
    val button: ColorfulButtonModel,
)


data class CharacteristicsBlockModel(
    val id: Long,
    val code: String,
    val name: String,
    val sort: String,
    val characteristics: List<CharacteristicModel>,
)

data class CharacteristicModel(
    val id: Int,
    val code: String,
    val name: String,
    val value: String,
    val hint: String?,
)


data class BrandCategoryBlockModel(
    val brand: BrandCategoryItemModel?,
    val category: BrandCategoryItemModel?,
)

data class BrandCategoryItemModel(
    val title: String,
    val data: BrandCategoryItemDataModel,
)

data class BrandCategoryItemDataModel(
    val id: Int,
    val name: String,
    val detailPicture: String,
)