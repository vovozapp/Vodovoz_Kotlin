package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.di.toVodovozImage
import com.vodovoz.app.data.vodovoz_service.model.EXTENDED_PRICE_DTO
import com.vodovoz.app.data.vodovoz_service.model.NALICHIE_MORE_DTO
import com.vodovoz.app.data.vodovoz_service.model.TOVAR_DATA_DTO
import com.vodovoz.app.domain.general.model.LabelModel
import com.vodovoz.app.domain.general.model.PriceModel
import com.vodovoz.app.domain.general.model.ProductModel

fun List<TOVAR_DATA_DTO?>.mapToDomain(): List<ProductModel> {
    return mapNotNull { productDTO ->
        productDTO?.mapToDomain()
    }
}

fun TOVAR_DATA_DTO.mapToDomain(): ProductModel? {
    return ProductModel(
        id = ID ?: return null,
        name = NAME ?: return null,
        isFavorite = FAVORITE ?: false,
        deposit = PROPERTY_ZALOG_VALUE ?: 0,
        rating = PROPERTY_RATING_VALUE?.toFloat() ?: return null,
        picture = DETAIL_PICTURE?.toVodovozImage() ?: return null,
        pricePerUnit = PROPERTY_TSENA_ZA_EDINITSU_TOVARA_VALUE,
        unitOfMeasurement = EDINICAIZMERENIYA,
        coefficient = KOFFICIENT?.toFloat() ?: 1f,
        quantity = CATALOG_QUANTITY ?: return null,
        firstPrice = EXTENDED_PRICE?.firstOrNull()?.mapToDomain() ?: return null,
        prices = EXTENDED_PRICE?.mapNotNull { it?.mapToDomain() } ?: return null,
        labels = NALICHIE_MORE?.mapToDomain() ?: emptyList()
    )
}

fun EXTENDED_PRICE_DTO.mapToDomain(): PriceModel? {
    return PriceModel(
        price = PRICE?.toFloat() ?: return null,
        oldPrice = OLD_PRICE?.toFloat() ?: return null,
        quantityFrom = QUANTITY_FROM ?: return null,
        quantityTo = QUANTITY_TO ?: return null
    )
}

@JvmName("mapLabelToDomain")
fun List<NALICHIE_MORE_DTO?>.mapToDomain(): List<LabelModel> {
    return mapNotNull { labelDTO ->
        labelDTO ?: return@mapNotNull null
        LabelModel(
            name = labelDTO.NAME ?: return@mapNotNull null,
            colorHex = labelDTO.CVET ?: return@mapNotNull null
        )
    }
}