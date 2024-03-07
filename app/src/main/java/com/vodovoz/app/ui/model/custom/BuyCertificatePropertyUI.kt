package com.vodovoz.app.ui.model.custom

data class BuyCertificatePropertyUI(
    val code: String,
    val name: String,
    val required: Boolean,
    val field: String? = null,
    val text: String,
    val value: String,
    val buyCertificateFieldUIList: List<BuyCertificateFieldUI>? = null,
    val showAmount: Boolean = false,
    var count: Int = 1,
    var error: Boolean = false
)