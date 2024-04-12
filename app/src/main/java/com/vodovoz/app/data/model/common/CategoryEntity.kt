package com.vodovoz.app.data.model.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class CategoryEntity(
    val id: Long? = null,
    val name: String,
    val productAmount: String? = null,
    val shareUrl: String = "",
    val detailPicture: String? = null,
    val primaryFilterName: String? = null,
    val sortTypeList: SortTypeList? = null,
    val primaryFilterValueList: List<FilterValueEntity> = listOf(),
    val subCategoryEntityList: List<CategoryEntity> = listOf(),
    val filterCode: String = "",
    val actionEntity: ActionEntity? = null,
) : Parcelable