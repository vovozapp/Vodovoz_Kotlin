package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.di.toFullUrl
import com.vodovoz.app.data.vodovoz_service.model.cart.CART_KNOPKA_DTO
import com.vodovoz.app.data.vodovoz_service.model.cart.CartDetailsDTO
import com.vodovoz.app.data.vodovoz_service.model.cart.ITOG_DTO
import com.vodovoz.app.data.vodovoz_service.model.cart.KNOPKA_PROMOKOD_DTO
import com.vodovoz.app.data.vodovoz_service.model.cart.KORZINA_PRODUCT_DTO
import com.vodovoz.app.data.vodovoz_service.model.cart.OKNO_PODAROK_DTO
import com.vodovoz.app.data.vodovoz_service.model.cart.OKNO_PROMOKOD_DTO
import com.vodovoz.app.data.vodovoz_service.model.cart.PODAROK_DTO
import com.vodovoz.app.data.vodovoz_service.model.cart.PODAROK_KNOPKA_DTO
import com.vodovoz.app.data.vodovoz_service.model.cart.PRODUCT_PRODAROK_DTO
import com.vodovoz.app.domain.general.model.ColorfulButtonModel
import com.vodovoz.app.domain.general.model.cart.CartButtonModel
import com.vodovoz.app.domain.general.model.cart.CartDetailsModel
import com.vodovoz.app.domain.general.model.cart.CartItemModel
import com.vodovoz.app.domain.general.model.cart.CartOrderSummaryModel
import com.vodovoz.app.domain.general.model.cart.CartPresentItemModel
import com.vodovoz.app.domain.general.model.cart.CartPresentModel
import com.vodovoz.app.domain.general.model.cart.CartPresentPopupWindowModel
import com.vodovoz.app.domain.general.model.cart.CartPromoButtonModel
import com.vodovoz.app.domain.general.model.cart.CartPromoPopupWindowModel

fun CartDetailsDTO.toDomain(): CartDetailsModel {
    return CartDetailsModel(
        title = TITLE ?: "",
        countText = COUNT ?: "",
        items = KORZINA?.mapToDomain() ?: emptyList(),
        present = PODAROK?.toDomain(),
        bottlesButton = KNOPKI?.BYTYLI?.toDomain(),
        promotionalCodeButton = KNOPKI?.PROMOKOD?.toDomain(),
        presentButton = KNOPKI?.PODARKI?.toDomain(),
        orderSummary = ITOG?.toDomain()
            ?: throw IllegalArgumentException("Cart order summary can't be null")
    )
}

fun ITOG_DTO.toDomain(): CartOrderSummaryModel {
    return CartOrderSummaryModel(
        finalPriceText = finalPriceText ?: "",
        productsPriceText = productsPriceText ?: "",
        discountText = discountText ?: "",
        depositText = depositText ?: "",
        presentText = ""
    )
}

fun KNOPKA_PROMOKOD_DTO.toDomain(): CartPromoButtonModel {
    return CartPromoButtonModel(
        title = TITLE ?: "",
        textColor = VALUE?.TEXT?.COLOR ?: "",
        text = VALUE?.TEXT?.TITLE ?: "",
        coupon = VALUE?.COUPON ?: "",
        image = IMAGE?.toFullUrl() ?: "",
        id = ID ?: "",
        popupWindow = OKNO?.toDomain() ?: CartPromoPopupWindowModel.Empty
    )
}

fun OKNO_PROMOKOD_DTO.toDomain(): CartPromoPopupWindowModel {
    return CartPromoPopupWindowModel(
        title = TITLE ?: "",
        fieldHint = TEXT_V_POLE ?: "",
        buttonName = KNOPKA?.TITLE ?: "",
        errorText = VALUE?.OSHIBKA
    )
}


fun CART_KNOPKA_DTO.toDomain(): CartButtonModel {
    return CartButtonModel(
        id = ID ?: "",
        image = IMAGE?.toFullUrl() ?: "",
        name = TITLE ?: ""
    )
}

fun PODAROK_DTO.toDomain(): CartPresentModel {
    return CartPresentModel(
        id = ID ?: -1,
        title = TITLE ?: "",
        description = OPIS ?: "",
        image = KARTINKA?.toFullUrl() ?: "",
        leftToGift = MAXSYMMA ?: OPIS?.filter { it.isDigit() }?.toIntOrNull() ?: 0,
        button = KNOPKA?.toDomain(),
        popupWindow = OKNOPODAROK?.toDomain()
    )
}

fun OKNO_PODAROK_DTO.toDomain(): CartPresentPopupWindowModel {
    return CartPresentPopupWindowModel(
        items = PODAROK?.mapToDomain() ?: emptyList(),
        button = KNOPKA?.toDomain() ?: ColorfulButtonModel.Empty
    )
}

@JvmName("mapToCartPresentItemModeList")
fun List<PRODUCT_PRODAROK_DTO>.mapToDomain(): List<CartPresentItemModel> {
    return mapNotNull { it.toDomain() }
}

fun PRODUCT_PRODAROK_DTO.toDomain(): CartPresentItemModel? {
    return CartPresentItemModel(
        id = ID ?: return null,
        name = NAME ?: "",
        image = DETAIL_PICTURE?.toFullUrl() ?: ""
    )
}

fun PODAROK_KNOPKA_DTO.toDomain(): ColorfulButtonModel {
    return ColorfulButtonModel(
        name = TEXT ?: "",
        backgroundColor = BACKGROUND ?: "",
        textColor = COLOR ?: "",
        id = ID ?: ""
    )
}

fun KORZINA_PRODUCT_DTO.toDomain(): CartItemModel? {
    return CartItemModel(
        id = ID ?: return null,
        productId = PODROBNO?.ID ?: return null,
        priceText = PRICE_FORMATED ?: "",
        productName = PODROBNO.NAME ?: "",
        isFavorite = PODROBNO.FAVORITE ?: false,
        isHit = false, //PODROBNO.HIT ?:
        depositText = PODROBNO.PROPERTY_ZALOG_VALUE ?: "",
        articleText = PODROBNO.CML2_ARTICLE ?: "",
        quantity = QUANTITY?.toDoubleOrNull()?.toInt() ?: 0,
        basePrice = BASE_PRICE ?: 0f,
        currentPrice = PRICE ?: 0f,
        canBuy = CAN_BUY == "Y",
        discountPrice = DISCOUNT_PRICE ?: 0f,
        discountPercentsText = DISCOUNT_PRICE_PERCENT ?: "",
        image = PODROBNO.DETAIL_PICTURE?.toFullUrl() ?: "",
        leftItems = PODROBNO.CATALOG_QUANTITY ?: 0,
        label = PODROBNO.NALICHIE_MORE?.toDomain(),
        hasDiscount = DISCOUNTS_APPLY ?: false,
        restrictionsCode = PODROBNO.ZAPRET_FISHKAM ?: 0
    )
}

fun List<KORZINA_PRODUCT_DTO>.mapToDomain(): List<CartItemModel> {
    return mapNotNull { it.toDomain() }
}