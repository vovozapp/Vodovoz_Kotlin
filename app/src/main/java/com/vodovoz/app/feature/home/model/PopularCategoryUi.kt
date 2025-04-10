package com.vodovoz.app.feature.home.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.vodovoz.app.design_system.model.ParentCategoryUi
import com.vodovoz.app.domain.general.model.CategoryModel
import com.vodovoz.app.domain.general.model.DataAllAction
import com.vodovoz.app.domain.general.model.PopularCategoryModel
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class PopularCategoryUi(
    val image: String,
    val name: String,
    val id: Long,
    val action: DataAllAction?,
) : Parcelable {
    companion object {
        val Empty = PopularCategoryUi("", "", -1, DataAllAction.Unknown)
    }
}

fun PopularCategoryModel.toUi(): PopularCategoryUi {
    return PopularCategoryUi(
        image = picture,
        name = name,
        id = id,
        action = action
    )
}

@Parcelize
@Immutable
data class CategoryUi(
    val name: String,
    val id: Int,
    val depthLevel: Int? = null,
) : Parcelable {
    companion object {
        val Empty = CategoryUi("", -1, null)
    }
}

fun CategoryUi.toParentCategory(): ParentCategoryUi {
    return ParentCategoryUi(
        id = id.toLong(),
        name = name,
        picture = "",
        null,
        -1,
        emptyList()
    )
}

fun CategoryModel.toUi(): CategoryUi {
    return CategoryUi(name = name, id = id, depthLevel = depthLevel)
}

