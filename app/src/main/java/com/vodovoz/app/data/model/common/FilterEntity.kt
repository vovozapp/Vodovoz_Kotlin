package com.vodovoz.app.data.model.common

class FilterEntity(
    val code: String,
    val name: String,
    val type: String?,
    val values: Values?
)

class Values (
    val min: Float,
    val max: Float
)