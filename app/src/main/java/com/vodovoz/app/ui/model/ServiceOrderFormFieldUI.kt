package com.vodovoz.app.ui.model

class ServiceOrderFormFieldUI(
    val id: String = "",
    val name: String = "",
    val isRequired: Boolean = false,
    val defaultValue: String = "",
    var isError: Boolean = false,
    var value: String = ""
)