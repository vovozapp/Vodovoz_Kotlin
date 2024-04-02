package com.vodovoz.app.ui.model.custom

data class BuyCertificateTypeUI(
    val type: Int = 0,
    val code: String = "",
    val name: String = "",
    val buyCertificatePropertyList: List<BuyCertificatePropertyUI>? = null,
    val isSelected: Boolean = false,
)
