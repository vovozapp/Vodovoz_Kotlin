package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.di.toFullUrl
import com.vodovoz.app.data.vodovoz_service.model.AnalogsSectionDTO
import com.vodovoz.app.data.vodovoz_service.model.CATEGORY_DTO
import com.vodovoz.app.data.vodovoz_service.model.EXTENDED_PRICE_DTO
import com.vodovoz.app.data.vodovoz_service.model.NALICHIE_MORE_DTO
import com.vodovoz.app.data.vodovoz_service.model.PODELITCA_DTO
import com.vodovoz.app.data.vodovoz_service.model.ProductsSectionDTO
import com.vodovoz.app.data.vodovoz_service.model.TOVAR_DATA_DTO
import com.vodovoz.app.domain.general.model.CategoryModel
import com.vodovoz.app.domain.general.model.LabelModel
import com.vodovoz.app.domain.general.model.PriceModel
import com.vodovoz.app.domain.general.model.ProductModel
import com.vodovoz.app.domain.general.model.ProductsSectionModel
import com.vodovoz.app.domain.general.model.ShareModel
// https://vodovoz.net/newmobile_new/glavnaya/novinki.php?new=specpredlosh&detail=Y&android=1.5.55&nav=1&sect=-1&sort=&ascdesc=

// https://vodovoz.net/newmobile_new/glavnaya/novinki.php?new=novinki&detail=Y&nav=1&sect=-1&sort=&ascdesc=

fun ProductsSectionDTO.toDomain(): ProductsSectionModel {
    return ProductsSectionModel(
        title = TITLE ?: "",
        sortingTitle = SORTIROVKA?.NAMEGLAV ?: "",
        productsQuantityText = TOVARVSEGO ?: "",
        sorting = SORTIROVKA?.DANNIESORT?.mapNotNull { it?.toDomain() } ?: emptyList(),
        products = DATA?.mapToDomain() ?: emptyList(),
        categories = RAZDEL?.LISTRAZDEL?.mapNotNull { it?.toDomain() } ?: emptyList(),
        share = PODELITCA?.toDomain()
    )
}

fun CATEGORY_DTO.toDomain(): CategoryModel? {
    return CategoryModel(ID ?: return null, NAME ?: return null, DEPTH_LEVEL)
}

fun PODELITCA_DTO.toDomain(): ShareModel? {
    return ShareModel(
        detailPageUrlIOS?.url ?: return null,
        detailPageUrlIOS.name ?: return null
    )
}

fun AnalogsSectionDTO.toDomain(): ProductsSectionModel {
    val sorting =
        SORTIROVKA?.DANNIESORT?.mapNotNull { sortDto -> sortDto?.toDomain() } ?: emptyList()

    val products = TOVAR?.mapNotNull { tovarDto ->
        tovarDto.toDomain()
    } ?: emptyList()

    return ProductsSectionModel(
        title = TITLE ?: "",
        sortingTitle = SORTIROVKA?.NAMEGLAV ?: "",
        sorting = sorting,
        products = products,
        categories = emptyList(),
        productsQuantityText = ""
    )
}

fun List<TOVAR_DATA_DTO?>.mapToDomain(): List<ProductModel> {
    return mapNotNull { productDTO ->
        productDTO?.toDomain()
    }
}

fun TOVAR_DATA_DTO.toDomain(): ProductModel? {
    return ProductModel(
        id = ID ?: return null,
        name = NAME ?: return null,
        isFavorite = FAVORITE ?: false,
        deposit = PROPERTY_ZALOG_VALUE ?: 0,
        rating = PROPERTY_RATING_VALUE ?: return null,
        picture = DETAIL_PICTURE?.toFullUrl() ?: return null,
        pricePerUnit = PROPERTY_TSENA_ZA_EDINITSU_TOVARA_VALUE,
        unitOfMeasurement = EDINICAIZMERENIYA,
        coefficient = KOFFICIENT?.toFloat() ?: 1f,
        quantity = CATALOG_QUANTITY ?: return null,
        firstPrice = EXTENDED_PRICE?.firstOrNull()?.toDomain() ?: return null,
        prices = EXTENDED_PRICE.mapNotNull { it?.toDomain() } ?: return null,
        labels = NALICHIE_MORE?.mapToDomain() ?: emptyList(),
        cartQuantity = 0
    )
}

fun EXTENDED_PRICE_DTO.toDomain(): PriceModel? {
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