package com.vodovoz.app.feature.productdetail.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.model.PresentInfoModel

@Immutable
data class PresentInfoUi(
    val html: String,
) {
    companion object {
        val Empty = PresentInfoUi("")
    }
}

fun PresentInfoModel.toUi(): PresentInfoUi {
    return PresentInfoUi(html)
}
