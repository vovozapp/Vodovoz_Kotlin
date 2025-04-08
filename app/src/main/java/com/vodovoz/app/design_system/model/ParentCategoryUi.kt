package com.vodovoz.app.design_system.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.model.DataAllAction
import com.vodovoz.app.domain.general.model.ParentCategoryModel
import com.vodovoz.app.feature.home.model.CategoryUi
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class ParentCategoryUi(
    val id: Long,
    val name: String,
    val picture: String,
    val action: DataAllAction?,
    val childCategories: List<ParentCategoryUi>,
) : Parcelable {

    companion object {
        val Empty = ParentCategoryUi(-1, "", "", DataAllAction.Unknown, emptyList())
    }

}

fun ParentCategoryUi.allCategories(): List<ParentCategoryUi> {
    return listOf(this) + childCategories.flatMap { category ->
        category.allCategories()
    }
}

fun ParentCategoryUi.toCategory(): CategoryUi {
    return CategoryUi(name, id.toInt())
}

fun ParentCategoryModel.toUi(): ParentCategoryUi {
    return ParentCategoryUi(
        id = id,
        name = name,
        picture = picture,
        action = action,
        childCategories = childCategories.map { it.toUi() }
    )
}
