package com.vodovoz.app.feature.catalog.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.vodovoz.app.design_system.model.BannerUi
import com.vodovoz.app.design_system.model.ParentCategoryUi
import com.vodovoz.app.design_system.model.mapToUi
import com.vodovoz.app.design_system.model.toUi
import com.vodovoz.app.domain.general.model.ParentCategoryModel
import com.vodovoz.app.domain.general.model.CatalogDetailsModel
import com.vodovoz.app.domain.general.model.DataAllAction
import kotlinx.parcelize.Parcelize

@Immutable
data class CatalogDetailsUi(
    val banners: List<BannerUi>,
    val categories: List<ParentCategoryUi>,
)


fun CatalogDetailsModel.toUi(): CatalogDetailsUi {
    return CatalogDetailsUi(
        banners = banners.mapToUi(),
        categories = categories.map { it.toUi() }
    )
}


