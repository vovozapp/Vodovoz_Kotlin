package com.vodovoz.app.data.model.common

data class SectionsEntity(
    val color: String? = null,
    val parentSectionDataEntityList: List<ParentSectionDataEntity>,
)
