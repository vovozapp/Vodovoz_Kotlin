package com.vodovoz.app.data.model.common

import com.vodovoz.app.data.parser.common.safeInt
import com.vodovoz.app.data.parser.common.safeString

data class SectionDataEntity(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val promotionId: Int,
    val cookieLink: String,
)
