package com.vodovoz.app.feature.cart.model

enum class ProductRestrictionUi(val code: Int) {
    NONE(0),
    NO_FAVORITES(1),
    NO_QUANTITY(2),
    NO_FAVORITES_QUANTITY(3),
    NO_DELETE(4),
    FULL_RESTRICTION(5);

    companion object {
        fun fromCode(code: Int): ProductRestrictionUi =
            entries.find { it.code == code } ?: NONE
    }
}