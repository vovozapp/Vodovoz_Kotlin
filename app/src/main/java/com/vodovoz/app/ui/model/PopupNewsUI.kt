package com.vodovoz.app.ui.model

import android.os.Parcelable
import com.vodovoz.app.data.model.common.ActionEntity
import kotlinx.parcelize.Parcelize

@Parcelize
class PopupNewsUI(
    val name: String? = null,
    val detailText: String? = null,
    val detailPicture: String? = null,
    val actionEntity: ActionEntity? = null,
    val androidVersion: String? = null,
) : Parcelable