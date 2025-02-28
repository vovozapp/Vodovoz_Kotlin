package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.model.MiniSearchRecommendationsDTO
import com.vodovoz.app.data.vodovoz_service.model.SearchRecommendationsDTO
import com.vodovoz.app.data.vodovoz_service.model.TOVARY_DTO
import com.vodovoz.app.data.vodovoz_service.model.product_details.TOVAR_SECTION_DTO
import com.vodovoz.app.domain.general.model.ProductModel
import com.vodovoz.app.domain.general.model.SearchRecommendationsModel
import com.vodovoz.app.domain.general.model.SectionModel

fun SearchRecommendationsDTO.toDomain(): SearchRecommendationsModel {
    return SearchRecommendationsModel(
        queries = SLOVA ?: emptyList(),
        section = REKOMEND?.toDomain() ?: throw IllegalArgumentException("SearchRecommendations section can't be null")
    )
}

fun MiniSearchRecommendationsDTO.toDomain(): SearchRecommendationsModel {
    return SearchRecommendationsModel(
        queries = SLOVA ?: emptyList(),
        section = TOVARY?.toDomain() ?: throw IllegalArgumentException("MiniSearchRecommendations section can't be null")
    )
}


fun TOVAR_SECTION_DTO.toDomain(): SectionModel<ProductModel> {
    return SectionModel(
        title = NAME ?: "",
        items = REKOMEND?.mapToDomain() ?: emptyList(),
        button = null
    )
}

fun TOVARY_DTO.toDomain(): SectionModel<ProductModel> {
    return SectionModel(
        title = NAME ?: "",
        items = TOVARY?.mapToDomain() ?: emptyList(),
        button = null
    )
}