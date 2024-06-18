package com.vodovoz.app.ui.model

data class ParentSectionDataUI(
    val title: String,
    val sectionDataEntityUIList: List<SectionDataUI>,
    val isSelected: Boolean = false,
    val position: Int? = null
)
