package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.di.toFullUrl
import com.vodovoz.app.data.vodovoz_service.model.order_details.ABOUT_ORDER_ITEM_DTO
import com.vodovoz.app.data.vodovoz_service.model.order_details.ABOUT_ORDER_OKNO_DTO
import com.vodovoz.app.data.vodovoz_service.model.order_details.ORDER_DETAILS_ITOG_DTO
import com.vodovoz.app.data.vodovoz_service.model.order_details.ORDER_DETAILS_KNOPKA_DTO
import com.vodovoz.app.data.vodovoz_service.model.order_details.ORDER_DETAILS_TOVAR_DTO
import com.vodovoz.app.data.vodovoz_service.model.order_details.ORDER_PRODUCT_PODAROK_DTO
import com.vodovoz.app.data.vodovoz_service.model.order_details.ORDER_STATUS_DTO
import com.vodovoz.app.data.vodovoz_service.model.order_details.OrderDetailsDTO
import com.vodovoz.app.domain.general.model.order.AboutOrderItemModel
import com.vodovoz.app.domain.general.model.order.AboutOrderPopupWindowModel
import com.vodovoz.app.domain.general.model.order.OrderDetailsButtonModel
import com.vodovoz.app.domain.general.model.order.OrderDetailsModel
import com.vodovoz.app.domain.general.model.order.OrderDetailsSummaryModel
import com.vodovoz.app.domain.general.model.order.OrderProductModel
import com.vodovoz.app.domain.general.model.order.OrderProductPresentModel
import com.vodovoz.app.domain.general.model.order.OrderStatusModel

fun OrderDetailsDTO.toDomain(): OrderDetailsModel {
    return OrderDetailsModel(
        title = TITLE?.ZAGOLOVOK ?: "",
        subtitle = TITLE?.OPIS ?: "",
        //todo - put actual status
        currentStatus = OrderStatusModel("Статус"),
        statuses = BLOCK?.STATUSY?.mapToDomain() ?: emptyList(),
        topButtons = this.BLOCK?.KNOPKI?.mapToDomain() ?: emptyList(),
        products = TOVARY?.TOVAR?.mapToDomain() ?: emptyList(),
        productsTitle = TOVARY?.TITLE ?: "",
        bottomButtons = KNOPKI_NIZ?.mapToDomain() ?: emptyList(),
        orderSummary = ITOG?.toDomain() ?: throw IllegalArgumentException("Order details summary can't be null")
    )
}



fun ORDER_DETAILS_ITOG_DTO.toDomain(): OrderDetailsSummaryModel {
    return OrderDetailsSummaryModel(
        finalPriceText = finalPrice ?: "",
        productsPriceText = productsPrice ?: "",
        depositText = deposit ?: "",
        deliveryText = delivery ?: "",
        parkingText =parking ?: "",
    )
}

@JvmName("mapToOrderProductModelList")
fun List<ORDER_DETAILS_TOVAR_DTO>.mapToDomain(): List<OrderProductModel>{
    return mapNotNull { it.toDomain() }
}

fun ORDER_DETAILS_TOVAR_DTO.toDomain(): OrderProductModel? {
    return OrderProductModel(
        id = ID ?: return null,
        name = NAME ?: "",
        quantity = QUANTITY?.toDoubleOrNull()?.toInt() ?: 1,
        articleNumberText = CML2_ARTICLE ?: "",
        depositText = PROPERTY_ZALOG_VALUE,
        price = EXTENDED_PRICE?.minByOrNull { (it?.PRICE ?: Int.MAX_VALUE) }?.toDomain(),
        isShowcaseProduct = URL == true,
        image = DETAIL_PICTURE?.toFullUrl() ?: "",
        labels = NALICHIE_MORE?.mapToDomain() ?: emptyList(),
        present = PODAROK?.toDomain(),
        pricePerUnit = PROPERTY_TSENA_ZA_EDINITSU_TOVARA_VALUE.takeIf { (it ?: 0) > 0 },
        unitOfMeasurement = EDINICAIZMERENIYA,
        catalogQuantity = CATALOG_QUANTITY ?: 0,
        isFavorite = FAVORITE ?: false,
        restrictionCode = ZAPRET_FISHKAM ?: 0
    )
}

fun ORDER_PRODUCT_PODAROK_DTO.toDomain(): OrderProductPresentModel {
    return OrderProductPresentModel(
        title = TITLE ?: "",
        color = COLOR ?: ""
    )
}

@JvmName("mapToOrderDetailsButtonModelList")
fun List<ORDER_DETAILS_KNOPKA_DTO>.mapToDomain(): List<OrderDetailsButtonModel> {
    return mapNotNull { it.toDomain() }
}

fun ORDER_DETAILS_KNOPKA_DTO.toDomain(): OrderDetailsButtonModel {
    return OrderDetailsButtonModel(
        name = NAME ?: "",
        description = OPIS ?: "",
        id = ID ?: "",
        image = IMAGE?.toFullUrl() ?: "",
        popupWindow = OKNO?.toDomain(),
        backgroundColor = COLOR_BACKGROUND ?: "",
        textColor = COLOR_TEXT ?: "",
        url = URL?.toFullUrl() ?: "",
        browser = (BRAYZER == "Y").takeIf { useBrowser -> useBrowser },
        driverId = VODITEL
    )
}

fun ABOUT_ORDER_OKNO_DTO.toDomain(): AboutOrderPopupWindowModel {
    return AboutOrderPopupWindowModel(
        title = TITLE ?: "",
        items = DANNYE?.map { it.toDomain() } ?: emptyList()
    )
}

fun ABOUT_ORDER_ITEM_DTO.toDomain(): AboutOrderItemModel {
    return AboutOrderItemModel(
        image = IMAGE?.toFullUrl() ?: "",
        name = NAME ?: "",
        description = OPIS ?: ""
    )
}

fun List<ORDER_STATUS_DTO>.mapToDomain(): List<OrderStatusModel> {
    return mapNotNull {
        it.toDomain()
    }
}

fun ORDER_STATUS_DTO.toDomain(): OrderStatusModel? {
    return OrderStatusModel(this.NAME ?: return null)
}