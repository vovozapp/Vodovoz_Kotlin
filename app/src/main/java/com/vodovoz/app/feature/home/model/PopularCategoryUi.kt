package com.vodovoz.app.feature.home.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.model.CategoryModel
import com.vodovoz.app.domain.general.model.PopularCategoryModel
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class PopularCategoryUi(
    val image: String,
    val name: String,
    val id: Long,
) : Parcelable {
    companion object {
        val Empty = PopularCategoryUi("", "", -1)
    }
}

fun PopularCategoryModel.toUi(): PopularCategoryUi {
    return PopularCategoryUi(
        image = picture,
        name = name,
        id = id
    )
}

@Parcelize
@Immutable
data class CategoryUi(
    val name: String,
    val id: Int,
    val depthLevel: Int? = null
) : Parcelable {
    companion object {
        val Empty = CategoryUi("", -1, null)
    }
}

fun CategoryModel.toUi(): CategoryUi {
    return CategoryUi(name = name, id = id, depthLevel = depthLevel)
}

