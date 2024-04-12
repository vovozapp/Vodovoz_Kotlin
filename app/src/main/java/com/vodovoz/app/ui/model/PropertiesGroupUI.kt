package com.vodovoz.app.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class PropertiesGroupUI(
    val name: String,
    val propertyUIList: List<PropertyUI>,
) : Parcelable