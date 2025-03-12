package com.vodovoz.app.design_system.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.vodovoz.app.domain.general.model.BlockPromoDataModel
import com.vodovoz.app.domain.general.model.BrandCategoryBlockModel
import com.vodovoz.app.domain.general.model.BrandCategoryItemDataModel
import com.vodovoz.app.domain.general.model.BrandCategoryItemModel
import com.vodovoz.app.domain.general.model.ButtonBlockModel
import com.vodovoz.app.domain.general.model.ButtonDesignBlockModel
import com.vodovoz.app.domain.general.model.BuyButtonModel
import com.vodovoz.app.domain.general.model.CharacteristicModel
import com.vodovoz.app.domain.general.model.CharacteristicsBlockModel
import com.vodovoz.app.domain.general.model.CommentModel
import com.vodovoz.app.domain.general.model.ContentBlockModel
import com.vodovoz.app.domain.general.model.DepositModel
import com.vodovoz.app.domain.general.model.DesignBlockModel
import com.vodovoz.app.domain.general.model.DocumentModel
import com.vodovoz.app.domain.general.model.OldNewPriceModel
import com.vodovoz.app.domain.general.model.PriceModel
import com.vodovoz.app.domain.general.model.ProductDetailsButtonsModel
import com.vodovoz.app.domain.general.model.ProductDetailsModel
import com.vodovoz.app.domain.general.model.ProductDetailsTabModel
import com.vodovoz.app.domain.general.model.PromoProductModel
import com.vodovoz.app.util.fromHexOrTransparent
import kotlinx.parcelize.Parcelize


fun ProductDetailsTabModel.toUi(): ProductDetailsTabUi {
    return ProductDetailsTabUi(
        title = title,
        dataId = dataId
    )
}

@Immutable
data class ProductDetailsTabUi(
    val title: String,
    val dataId: String,
)

fun ProductDetailsButtonsModel.toUi(): ProductDetailsButtonsUi {
    return ProductDetailsButtonsUi(
        blockButton = blockButton?.toUi(),
        blockDesignButton = blockDesignButton?.toUi(),
        multiBuyButton = multiBuyButton?.toUi(),
        analogButton = analogButton?.toUi(),
        preOrderButton = analogButton?.toUi()
    )
}

data class ProductDetailsButtonsUi(
    val blockButton: ButtonBlockUi?,
    val blockDesignButton: ButtonDesignBlockUi?,

    val multiBuyButton: ColorfulButtonUi?,
    val analogButton: ColorfulButtonUi?,
    val preOrderButton: ColorfulButtonUi?,
) {
    companion object {
        val Empty = ProductDetailsButtonsUi(
            null, null, null, null, null
        )
    }
}

@Immutable
data class ProductDetailsUi(
    val id: Long,
    val name: String,

    val blockBrandCategory: BrandCategoryBlockUi,
    val information: ContentBlockUi<String>?,

    val detailInfo: ContentBlockUi<String>,
    val characteristics: ContentBlockUi<List<CharacteristicsBlockUi>>,
    val documents: ContentBlockUi<List<DocumentUi>>,

    val detailPicture: String,
    val pictures: List<String>,
    val sectionTags: SectionUi<String>,
    val isFavorite: Boolean,
    val isAvailable: Boolean,
    val productQuantity: Int,
    val labels: List<LabelWithColorUi>,
    val rating: Float,
    val deposit: DepositUi?,


    val shareUrl: String,
    val shareUrlText: String,

    val youtubeUrl: String?,
    val rutubeUrl: String?,

    val coefficient: Float,
    val pricePerUnit: String?,
    val barCode: String,

    val firstPrice: PriceUi,
    val prices: List<PriceUi>,
    val commentsCount: Int,
) {
    companion object {
        val Empty: ProductDetailsUi = ProductDetailsUi(
            id = 0L,
            name = "",

            blockBrandCategory = BrandCategoryBlockUi(
                brand = null,
                category = null
            ),
            information = null,

            detailInfo = ContentBlockUi("", "", ""),
            characteristics = ContentBlockUi("", emptyList(), ""),
            documents = ContentBlockUi("", emptyList(), ""),

            detailPicture = "",
            pictures = emptyList(),
            sectionTags = SectionUi.empty(),
            isFavorite = false,
            isAvailable = false,
            productQuantity = 0,
            labels = emptyList(),
            rating = 0f,
            deposit = null,

            shareUrl = "",
            shareUrlText = "",

            youtubeUrl = null,
            rutubeUrl = null,

            coefficient = 0f,
            pricePerUnit = null,
            barCode = "",

            firstPrice = PriceUi(0f, 0f, 0, 0),
            prices = emptyList(),
            commentsCount = 0
        )

    }
}

fun ProductDetailsModel.toUi(): ProductDetailsUi {
    return ProductDetailsUi(
        id = id,
        name = name,

        blockBrandCategory = blockBrandCategory.toUi(),
        information = information?.toUi { description -> description },

        detailInfo = detailInfo.toUi { s -> s },
        characteristics = characteristics.toUi { value -> value.mapToUi() },
        documents = documents.toUi { value -> value.mapToUi() },

        detailPicture = detailPicture,
        pictures = pictures,
        sectionTags = sectionTags.toUi { tag -> tag },
        isFavorite = isFavorite,
        isAvailable = isAvailable,
        productQuantity = productQuantity,
        labels = labels.mapNotNull { label -> label.toUi() },
        rating = rating,
        deposit = deposit?.toUi(),

        shareUrl = shareUrl,
        shareUrlText = shareUrlText,

        youtubeUrl = youtubeUrl,
        rutubeUrl = rutubeUrl,

        coefficient = coefficient,
        pricePerUnit = pricePerUnit,
        barCode = barCode,

        firstPrice = firstPrice.toUi(),
        prices = prices.map { price -> price.toUi() },
        commentsCount = commentsCount
    )

}

@JvmName("mapToCharacteristicsBlockUi")
fun List<CharacteristicsBlockModel>.mapToUi(): List<CharacteristicsBlockUi> {
    return map { it.toUi() }
}

fun CharacteristicsBlockModel.toUi(): CharacteristicsBlockUi {
    return CharacteristicsBlockUi(
        id, code, name, sort, characteristics = characteristics.map { it.toUi() }
    )
}

fun CharacteristicModel.toUi(): CharacteristicUi {
    return CharacteristicUi(id, code, name, value, hint)
}

@JvmName("mapToDocumentUi")
fun List<DocumentModel>.mapToUi(): List<DocumentUi> {
    return map { it.toUi() }
}

fun DocumentModel.toUi(): DocumentUi {
    return DocumentUi(type, size, sizeText, iconUrl, description, src)
}

@Immutable
@Parcelize
data class DocumentUi(
    val type: String,
    val size: Float,
    val sizeText: String,
    val iconUrl: String,
    val description: String,
    val src: String,
) : Parcelable

data class PriceUi(
    val price: Float,
    val oldPrice: Float,
    val quantityFrom: Int,
    val quantityTo: Int,
)

fun PriceModel.toUi(): PriceUi {
    return PriceUi(price, oldPrice, quantityFrom, quantityTo)
}

@Immutable
data class BuyButtonUi(
    val textColor: Color,
    val backgroundColor: Color,
    val title: String,
    val productId: String,
    val moreProductId: String,
)

fun BuyButtonModel.toUi(): BuyButtonUi {
    return BuyButtonUi(
        Color.fromHexOrTransparent(textColor),
        Color.fromHexOrTransparent(backgroundColor),
        title,
        productId,
        moreProductId
    )
}

data class DepositUi(
    val price: Float,
    val description: ContentBlockUi<String>,
)

fun DepositModel.toUi(): DepositUi {
    return DepositUi(
        price = price,
        description = description.toUi { s -> s }
    )
}

fun ButtonBlockModel.toUi(): ButtonBlockUi {
    return ButtonBlockUi(
        button = button.toUi(),
        data = data.toUi(),
        buyButton = buyButton.toUi()
    )
}

fun ButtonDesignBlockModel.toUi(): ButtonDesignBlockUi {
    return ButtonDesignBlockUi(
        block = block.toUi(),
        data = data.toUi(),
        buyButton = buyButton.toUi()
    )
}

fun BlockPromoDataModel.toUi(): BlockPromoDataUi {
    return BlockPromoDataUi(
        title = title,
        description = description,
        productQuantityText = productQuantityText,
        product = product.toUi()
    )
}

fun PromoProductModel.toUi(): PromoProductUi {
    return PromoProductUi(
        name = name,
        image = image,
        price = price.toUi()
    )
}

fun OldNewPriceModel.toUi(): OldNewPriceUi {
    return OldNewPriceUi(
        new = new,
        old = old
    )
}

fun DesignBlockModel.toUi(): DesignBlockUi {
    return DesignBlockUi(
        title = title,
        image = image,
        background = Color.fromHexOrTransparent(background),
        textColor = Color.fromHexOrTransparent(textColor),
        borderColor = Color.fromHexOrTransparent(borderColor),
        button = button.toUi()
    )
}

@Immutable
data class ButtonBlockUi(
    val button: ColorfulButtonUi,
    val data: BlockPromoDataUi,
    val buyButton: BuyButtonUi,
)

@Immutable
data class ButtonDesignBlockUi(
    val block: DesignBlockUi,
    val data: BlockPromoDataUi,
    val buyButton: BuyButtonUi,
)

@Immutable
data class BlockPromoDataUi(
    val title: String,
    val description: String,
    val productQuantityText: String,
    val product: PromoProductUi,
)

@Immutable
data class PromoProductUi(
    val name: String,
    val image: String,
    val price: OldNewPriceUi,
)

@Immutable
data class OldNewPriceUi(
    val new: String,
    val old: String,
)

@Immutable
data class DesignBlockUi(
    val title: String,
    val image: String,
    val background: Color,
    val textColor: Color,
    val borderColor: Color,
    val button: ColorfulButtonUi,
)


@Immutable
data class CharacteristicsBlockUi(
    val id: Long,
    val code: String,
    val name: String,
    val sort: String,
    val characteristics: List<CharacteristicUi>,
) {
    companion object {
        val Empty = CharacteristicsBlockUi(0, "", "", "", emptyList())
    }
}

@Immutable
data class CharacteristicUi(
    val id: Int,
    val code: String,
    val name: String,
    val value: String,
    val hint: String?,
)

@Immutable
data class ContentBlockUi<T>(
    val title: String,
    val content: T,
    val id: String,
)

fun <T, R> ContentBlockModel<T>.toUi(mapper: (T) -> R): ContentBlockUi<R> {
    return ContentBlockUi(
        title = title,
        content = mapper(content),
        id = id
    )
}

@Immutable
data class BrandCategoryBlockUi(
    val brand: BrandCategoryItemUi?,
    val category: BrandCategoryItemUi?,
)

@Immutable
data class BrandCategoryItemUi(
    val title: String,
    val data: BrandCategoryItemDataUi,
)

@Immutable
data class BrandCategoryItemDataUi(
    val id: Int,
    val name: String,
    val detailPicture: String,
)

fun BrandCategoryBlockModel.toUi(): BrandCategoryBlockUi {
    return BrandCategoryBlockUi(
        brand = brand?.toUi(),
        category = category?.toUi()
    )
}

fun BrandCategoryItemModel.toUi(): BrandCategoryItemUi {
    return BrandCategoryItemUi(
        title = title,
        data = data.toUi()
    )
}

fun BrandCategoryItemDataModel.toUi(): BrandCategoryItemDataUi {
    return BrandCategoryItemDataUi(
        id = id,
        name = name,
        detailPicture = detailPicture
    )
}

@Immutable
data class CommentUi(
    val userName: String,
    val userPhoto: String,
    val text: String,
    val dateText: String,
    val rating: Int,
    val purchased: String,
)

fun List<CommentModel>.mapToUi(): List<CommentUi> {
    return mapNotNull { commentModel -> commentModel.toUi() }
}

fun CommentModel.toUi(): CommentUi {
    return CommentUi(userName, userPhoto, text, dateText, rating, purchased)
}

