package com.vodovoz.app.data.model.common

data class BuyCertificatePropertyEntity(
    val code: String,
    val name: String,
    val required: String,
    val field: String? = null,
    val text: String,
    val value: String,
    val buyCertificateFields: List<BuyCertificateField>? = null,
    val showAmount: Boolean = false
)

