package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.model.ProductCommentsDTO
import com.vodovoz.app.data.vodovoz_service.model.SORT_DTO
import com.vodovoz.app.domain.general.model.ProductCommentsInfoModel
import com.vodovoz.app.domain.general.model.SortModel

fun ProductCommentsDTO.toDomain(): ProductCommentsInfoModel {
    return ProductCommentsInfoModel(
        sorting = SORTIROVKA?.mapNotNull { sortDto -> sortDto?.toDomain() } ?: emptyList(),
        ratingText = RAITINGOSNOVA ?: "",
        commentsCount = COMMENT_COUNT ?: 0,
        commentsCountText = COMMENT_COUNT_TEXT ?: ""
    )
}

fun SORT_DTO.toDomain(): SortModel? {
    return SortModel(
        name = NAME ?: return null,
        value = ZNACHIE ?: return null,
        order = SORT ?: return null
    )
}
