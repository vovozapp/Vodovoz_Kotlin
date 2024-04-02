package com.vodovoz.app.data.model.features

import com.vodovoz.app.data.model.common.BuyCertificatePropertyEntity

data class BuyCertificateTypeEntity(
    val type: Int = 0,
    val code: String = "",
    val name: String = "",
    val buyCertificatePropertyEntityList: List<BuyCertificatePropertyEntity>? = null,
)
