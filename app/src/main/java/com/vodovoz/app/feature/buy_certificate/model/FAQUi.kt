package com.vodovoz.app.feature.buy_certificate.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.model.certificate.FAQModel
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class FAQUi(
    val name: String,
    val image: String,
    val items: List<FAQItemUi>,
): Parcelable {
    companion object {
        val Empty = FAQUi("", "", emptyList())
    }
}

fun FAQModel.toUi(): FAQUi {
    return FAQUi(
        name = name,
        image = image,
        items = items.mapToUi()
    )
}
