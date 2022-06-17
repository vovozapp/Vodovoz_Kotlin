package com.vodovoz.app.ui.model

import java.io.Serializable

class CategoryUI(
    val id: Long? = null,
    val name: String,
    val productAmount: String? = null,
    val detailPicture: String? = null,
    var isOpen: Boolean = false,
    val primaryFilterName: String? = null,
    val primaryFilterValueList: List<FilterValueUI> = listOf(),
    val categoryUIList: List<CategoryUI> = listOf()
) : Serializable