package com.vodovoz.app.domain.general.model

data class CategoryModel(
    val id: Int,
    val name: String,
    val depthLevel: Int? = null
)