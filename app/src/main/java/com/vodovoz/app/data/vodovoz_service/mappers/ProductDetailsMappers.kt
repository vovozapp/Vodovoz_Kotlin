package com.vodovoz.app.data.vodovoz_service.mappers

import android.text.Html
import androidx.core.text.HtmlCompat
import com.vodovoz.app.data.vodovoz_service.di.toFullUrl
import com.vodovoz.app.data.vodovoz_service.model.product_details.BLOCK_KNOPKA_DATA_DTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.BLOCK_KNOPKA_VALUE_DTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.BLOCK_RAZDEL_DTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.BLOCK_RAZDEL_INFO_DATA_DTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.BLOCK_RAZDEL_INFO_DTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.BLOCK_TOVAR_DTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.BLOCK_U_BLOCK_KNOPKA_DIZAIN_DTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.BLOK_KNOPKA_DIZAIN_DTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.BLOK_KNOPKA_DTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.COMMENT_DTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.DETAILTEXT_DTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.DOCUMENTS_DTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.DOCUMENT_DTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.HARAKTERISTIKI_DTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.HARAKTERISTIK_BIND_DTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.INFORMATIONS_DTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.KNOPKA_ANALOG_DTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.KNOPKA_DESHEVLE_DTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.KNOPKA_KUPIT_DTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.PRICE_DTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.PRODUCT_DETAIL_HARAKTERISTIKI_DTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.ProductDetailsDTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.TAGS_DTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.TOVAR_DETAIL_DTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.TOVAR_DETAIL_TEXT_DTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.ZALOG_DTO
import com.vodovoz.app.domain.general.model.BlockPromoDataModel
import com.vodovoz.app.domain.general.model.BrandCategoryBlockModel
import com.vodovoz.app.domain.general.model.BrandCategoryItemDataModel
import com.vodovoz.app.domain.general.model.BrandCategoryItemModel
import com.vodovoz.app.domain.general.model.ButtonBlockModel
import com.vodovoz.app.domain.general.model.ButtonDesignBlockModel
import com.vodovoz.app.domain.general.model.BuyButtonModel
import com.vodovoz.app.domain.general.model.CharacteristicModel
import com.vodovoz.app.domain.general.model.CharacteristicsBlockModel
import com.vodovoz.app.domain.general.model.ColorfulButtonModel
import com.vodovoz.app.domain.general.model.CommentModel
import com.vodovoz.app.domain.general.model.ContentBlockModel
import com.vodovoz.app.domain.general.model.DepositModel
import com.vodovoz.app.domain.general.model.DesignBlockModel
import com.vodovoz.app.domain.general.model.DocumentModel
import com.vodovoz.app.domain.general.model.OldNewPriceModel
import com.vodovoz.app.domain.general.model.ProductDetailsButtonsModel
import com.vodovoz.app.domain.general.model.ProductDetailsModel
import com.vodovoz.app.domain.general.model.ProductDetailsMoreProducts
import com.vodovoz.app.domain.general.model.ProductDetailsScreenModel
import com.vodovoz.app.domain.general.model.ProductDetailsTabModel
import com.vodovoz.app.domain.general.model.PromoProductModel
import com.vodovoz.app.domain.general.model.SectionModel
import com.vodovoz.app.domain.general.model.WebsiteErrorException

private fun BLOCK_RAZDEL_DTO.toDomain(): BrandCategoryBlockModel {
    return BrandCategoryBlockModel(
        brand = BRAND?.toDomain(),
        category = RAZDEL?.toDomain()
    )
}

private fun BLOCK_RAZDEL_INFO_DATA_DTO.toDomain(): BrandCategoryItemDataModel? {
    return BrandCategoryItemDataModel(
        id = ID ?: return null,
        name = NAME ?: return null,
        detailPicture = DETAIL_PICTURE?.toFullUrl() ?: return null
    )
}

private fun BLOCK_RAZDEL_INFO_DTO.toDomain(): BrandCategoryItemModel? {
    return BrandCategoryItemModel(
        title = TITLE ?: return null,
        data = DATA?.toDomain() ?: return null
    )
}

private fun TOVAR_DETAIL_DTO.toDomain(
    shareUrlText: String,
    commentsCount: Int,
): ProductDetailsModel {
    if (ACTIVE != "Y") throw WebsiteErrorException("Site don't work")

    val detailPicture = DETAIL_PICTURE?.toFullUrl()

    return ProductDetailsModel(
        id = ID ?: throw IllegalArgumentException("ID cannot be null"),
        name = NAME ?: throw IllegalArgumentException("Product name cannot be null"),
        blockBrandCategory = BLOCKRAZDEL?.toDomain() ?: BrandCategoryBlockModel(null, null),
        information = INFORMATIONS?.toDomain(),
        detailInfo = DETAIL_TEXT?.toDomain()
            ?: throw IllegalArgumentException("Detail text cannot be null"),
        characteristics = HARAKTERISTIKI?.toDomain()
            ?: throw IllegalArgumentException("Characteristics cannot be null"),
        documents = DOCUMENTS?.toDomain()
            ?: throw IllegalArgumentException("Documents cannot be null"),
        detailPicture = detailPicture
            ?: throw IllegalArgumentException("Detail picture cannot be null"),
        pictures = ((MORE_PHOTO?.map { img -> img.toFullUrl() }
            ?: emptyList()) + detailPicture).distinct().reversed(),
        sectionTags = TAGS?.toDomain() ?: SectionModel.empty(),
        isFavorite = FAVORITE ?: false,
        isAvailable = (KOLLTOVAR ?: -1) > 0,
        productQuantity = KOLLTOVAR ?: 0,
        labels = NALICHIE?.mapToDomain() ?: emptyList(),
        rating = PROPERTY_RATING_VALUE?.toFloat() ?: 0f,
        deposit = ZALOG?.toDomain(),
        shareUrl = DETAIL_PAGE_URL?.toFullUrl() ?: "",
        shareUrlText = shareUrlText,
        youtubeUrl = RUTUBE_VIDEO?.firstOrNull()?.IMAGE,
        rutubeUrl = RUTUBE_VIDEO?.firstOrNull()?.IMAGE,
        coefficient = KOFFICIENT ?: 1f,
        pricePerUnit = DOPTSENA_ZA_EDINICY,
        barCode = BAR_CODE ?: "",
        firstPrice = EXTENDEDPRICE?.firstOrNull()?.toDomain()
            ?: throw IllegalArgumentException("First extended price cannot be null"),
        prices = EXTENDEDPRICE.mapNotNull { extendedPriceDto -> extendedPriceDto.toDomain() },
        commentsCount = commentsCount
    )
}

fun DOCUMENTS_DTO.toDomain(): ContentBlockModel<List<DocumentModel>> {
    return ContentBlockModel(
        title = TITLE ?: "",
        content = DATA?.mapToDomain() ?: emptyList(),
        id = ID ?: "documents"
    )
}

fun List<DOCUMENT_DTO>.mapToDomain(): List<DocumentModel> {
    return mapNotNull { documentDto -> documentDto.toDomain() }
}

fun DOCUMENT_DTO.toDomain(): DocumentModel? {
    return DocumentModel(
        type = TYPE ?: "",
        size = FILE_SIZE?.toFloat() ?: return null,
        sizeText = FILE_SIZE_FORMAT ?: return null,
        iconUrl = IKONKA?.toFullUrl() ?: "",
        description = DESCRIPTION ?: "",
        src = SRC?.toFullUrl() ?: ""
    )
}

fun PRODUCT_DETAIL_HARAKTERISTIKI_DTO.toDomain(): ContentBlockModel<List<CharacteristicsBlockModel>> {
    return ContentBlockModel(
        title = TITLE ?: "",
        content = DATA?.mapNotNull { harakteristikDto -> harakteristikDto.toDomain() }
            ?: emptyList(),
        id = ID ?: "xarakteristik"
    )
}

fun HARAKTERISTIKI_DTO.toDomain(): CharacteristicsBlockModel? {
    return CharacteristicsBlockModel(
        id = ID?.toLong() ?: return null,
        code = CODE ?: "",
        name = NAME ?: "",
        sort = SORT ?: "",
        characteristics = BINDS?.mapNotNull { bindDto -> bindDto?.toDomain() } ?: emptyList()
    )
}

fun HARAKTERISTIK_BIND_DTO.toDomain(): CharacteristicModel? {
    return CharacteristicModel(
        id = ID ?: return null,
        code = CODE ?: "",
        name = NAME ?: return null,
        value = VALUE ?: "",
        hint = HINT?.ifBlank { null }
    )
}

fun TAGS_DTO.toDomain(): SectionModel<String> {
    return SectionModel(
        title = TITLE ?: "",
        items = TAGS?.mapNotNull { tag -> tag } ?: emptyList(),
        button = null
    )
}

fun ZALOG_DTO.toDomain(): DepositModel? {
    val description = OPISANIE
    return DepositModel(
        price = PRICE?.toFloat() ?: return null,
        description = ContentBlockModel(
            title = description?.TEXT ?: "",
            content = description?.DOPOPISANIE ?: "",
            id = ""
        ),
    )
}

fun INFORMATIONS_DTO.toDomain(): ContentBlockModel<String> {
    return ContentBlockModel(
        title = TITLE ?: "",
        content = ZNACHENIE ?: "",
        id = ""
    )
}

fun TOVAR_DETAIL_TEXT_DTO.toDomain(): ContentBlockModel<String> {
    return ContentBlockModel(
        title = TITLE ?: "",
        content = OPISANIE ?: "",
        id = ID ?: "detailtext"
    )
}

private fun KNOPKA_DESHEVLE_DTO.toDomain(): ColorfulButtonModel {
    return ColorfulButtonModel(
        name = NAME ?: "",
        backgroundColor = BACKGROUND ?: "",
        textColor = TEXTCOLOR ?: "",
    )
}

private fun KNOPKA_ANALOG_DTO.toDomain(): ColorfulButtonModel {
    return ColorfulButtonModel(
        name = NAME ?: "",
        backgroundColor = BACKGROUND ?: "",
        textColor = TEXTCOLOR ?: "",
        id = ID ?: ""
    )
}

private fun KNOPKA_KUPIT_DTO.toDomain(): BuyButtonModel {
    return BuyButtonModel(
        textColor = COLORTEXT ?: "",
        backgroundColor = BACKGROUND ?: "",
        title = TITLE ?: "",
        productId = IDTOVAR ?: "",
        moreProductId = DOPTOVAR ?: ""
    )
}

private fun BLOK_KNOPKA_DTO.toDomain(): ButtonBlockModel? {
    return ButtonBlockModel(
        button = KNOPKA?.toDomain() ?: return null,
        data = DATA?.toDomain() ?: return null,
        buyButton = KNOPKA_KUPIT?.toDomain() ?: return null
    )
}

private fun PRICE_DTO.toDomain(): OldNewPriceModel {
    return OldNewPriceModel(
        old = OLD ?: "",
        new = NEW ?: ""
    )
}

private fun BLOCK_TOVAR_DTO.toDomain(): PromoProductModel? {
    return PromoProductModel(
        name = NAME ?: "",
        price = PRICE?.toDomain() ?: return null,
        image = KARTINKA?.toFullUrl() ?: ""
    )
}

private fun BLOCK_KNOPKA_DATA_DTO.toDomain(): BlockPromoDataModel? {
    return BlockPromoDataModel(
        title = TITLE ?: "",
        description = OPISANIE ?: "",
        product = TOVAR?.toDomain() ?: return null,
        productQuantityText = KOLLTOVAR ?: ""
    )
}

private fun BLOCK_KNOPKA_VALUE_DTO.toDomain(): ColorfulButtonModel {
    return ColorfulButtonModel(
        name = TITLE ?: "",
        backgroundColor = BACKGROUND ?: "",
        textColor = COLORTEXT ?: "",
    )
}

fun ProductDetailsDTO.toDomain(): ProductDetailsScreenModel {

    val moreButtons = TOVAR?.DOPKNOPKI
    val moreProducts = BLOCTOVAR
    val similar = moreProducts?.POHOSHIE
    val accessory = moreProducts?.AKSESSYAR
    val commentsCount = COMMENTS?.COMMEN_COUNT ?: 0

    return ProductDetailsScreenModel(
        productDetails = TOVAR?.toDomain(
            shareUrlText = PODILITSYA?.detail_page_url ?: "",
            commentsCount = commentsCount
        )
            ?: throw NoSuchElementException("Product details not found."),
        buttons = ProductDetailsButtonsModel(
            blockButton = moreButtons?.BLOK_KNOPKA?.toDomain(),
            blockDesignButton = moreButtons?.BLOK_KNOPKA_DIZAIN?.toDomain(),
            multiBuyButton = KNOPKI?.DESHEVLE?.toDomain(),
            analogButton = KNOPKI?.ANALOG?.toDomain(),
            preOrderButton = KNOPKI?.ZAKAZAT?.toDomain()
        ),
        moreProducts = ProductDetailsMoreProducts(
            sectionSimilar = SectionModel(
                title = similar?.NAME ?: "",
                items = similar?.REKOMEND?.mapNotNull { tovarDataDto -> tovarDataDto?.toDomain() }
                    ?: emptyList(),
                button = null
            ),
            sectionAccessory = SectionModel(
                title = accessory?.NAME ?: "",
                items = accessory?.REKOMEND?.mapNotNull { it?.toDomain() } ?: emptyList(),
                button = null
            )
        ),
        comments = COMMENTS?.COMMENTS?.mapNotNull { commentDto -> commentDto?.toDomain() }
            ?: emptyList(),
        tabs = DETAILTEXT?.mapNotNull { it.toDomain() } ?: emptyList()
    )
}

private fun DETAILTEXT_DTO.toDomain(): ProductDetailsTabModel? {
    return ProductDetailsTabModel(
        title = TITLE ?: return null,
        dataId = DATAID ?: return null
    )
}

fun COMMENT_DTO.toDomain(): CommentModel {
    return CommentModel(
        userName = NAME ?: "",
        userPhoto = USER_PHOTO?.toFullUrl() ?: "",
        text = HtmlCompat.fromHtml(TEXT ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY).toString(),
        dateText = DATA ?: "",
        rating = RATING ?: 0,
        purchased = HtmlCompat.fromHtml(KYPLEN ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
    )
}

private fun BLOK_KNOPKA_DIZAIN_DTO.toDomain(): ButtonDesignBlockModel? {
    return ButtonDesignBlockModel(
        block = BLOCK?.toDomain() ?: return null,
        data = DATA?.toDomain() ?: return null,
        buyButton = KNOPKA_KUPIT?.toDomain() ?: return null
    )
}

private fun BLOCK_U_BLOCK_KNOPKA_DIZAIN_DTO.toDomain(): DesignBlockModel? {
    return DesignBlockModel(
        title = TITLE ?: "",
        image = KARTINKA?.toFullUrl() ?: "",
        button = KNOPKA?.toDomain() ?: return null,
        background = BACKGROUND ?: "",
        textColor = TEXTCOLOR ?: "",
        borderColor = BORDER_COLOR ?: ""
    )
}
