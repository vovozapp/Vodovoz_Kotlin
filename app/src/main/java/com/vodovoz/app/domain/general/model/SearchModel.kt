package com.vodovoz.app.domain.general.model

data class SearchRecommendationsModel(
    val queries: List<String>,
    val section: SectionModel<ProductModel>
)