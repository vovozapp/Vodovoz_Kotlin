package com.vodovoz.app.feature.home.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.model.PopularCategoryModel
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class PopularCategoryUi(
    val image: String,
    val name: String,
    val id: Long,
): Parcelable {
    companion object {
        val Empty = PopularCategoryUi("", "", -1L)
    }
}

fun PopularCategoryModel.toUi(): PopularCategoryUi {
    return PopularCategoryUi(
        image = picture,
        name = name,
        id = id
    )
}