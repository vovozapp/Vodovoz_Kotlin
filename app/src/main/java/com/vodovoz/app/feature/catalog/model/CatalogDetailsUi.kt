package com.vodovoz.app.feature.catalog.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.vodovoz.app.design_system.model.BannerUi
import com.vodovoz.app.design_system.model.mapToUi
import com.vodovoz.app.domain.general.model.CatalogCategoryModel
import com.vodovoz.app.domain.general.model.CatalogDetailsModel
import com.vodovoz.app.domain.general.model.DataAllAction
import kotlinx.parcelize.Parcelize

@Immutable
data class CatalogDetailsUi(
    val banners: List<BannerUi>,
    val categories: List<CatalogCategoryUi>,
)

@Immutable
@Parcelize
data class CatalogCategoryUi(
    val id: Int,
    val name: String,
    val picture: String,
    val action: DataAllAction?,
    val childCategories: List<CatalogCategoryUi>,
) : Parcelable {

    companion object{
        val Empty = CatalogCategoryUi(-1, "", "", DataAllAction.Unknown, emptyList())
    }

}

fun CatalogCategoryModel.toUi(): CatalogCategoryUi {
    return CatalogCategoryUi(
        id = id,
        name = name,
        picture = picture,
        action = action,
        childCategories = childCategories.map { it.toUi() }
    )
}

fun CatalogDetailsModel.toUi(): CatalogDetailsUi {
    return CatalogDetailsUi(
        banners = banners.mapToUi(),
        categories = categories.map { it.toUi() }
    )
}


