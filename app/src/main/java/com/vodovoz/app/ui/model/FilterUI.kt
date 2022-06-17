package com.vodovoz.app.ui.model

import java.io.Serializable

class FilterUI(
    val code: String,
    val name: String,
    var filterValueList: MutableList<FilterValueUI> = mutableListOf()
) : Serializable