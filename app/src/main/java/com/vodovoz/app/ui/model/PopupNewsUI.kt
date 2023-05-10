package com.vodovoz.app.ui.model

import com.vodovoz.app.data.model.common.ActionEntity
import java.io.Serializable

class PopupNewsUI(
    val name: String? = null,
    val detailText: String? = null,
    val detailPicture: String? = null,
    val actionEntity: ActionEntity? = null,
    val androidVersion: String? = null
): Serializable