package com.vodovoz.app.data.model.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class SortTypeList(
   val name: String = "",
   val sortTypeList: List<SortType> = listOf(),
): Parcelable
