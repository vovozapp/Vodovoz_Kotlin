package com.vodovoz.app.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryUI(
    val id: Long? = null,
    val name: String,
    val shareUrl: String = "",
    val productAmount: String? = null,
    val detailPicture: String? = null,
    var isOpen: Boolean = false,
    val primaryFilterName: String? = null,
    var primaryFilterValueList: List<FilterValueUI> = listOf(),
    var categoryUIList: List<CategoryUI> = listOf()
) : Parcelable