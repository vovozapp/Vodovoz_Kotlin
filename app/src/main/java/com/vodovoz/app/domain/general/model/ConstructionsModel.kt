package com.vodovoz.app.domain.general.model

data class ContentBlockModel<T>(
    val title: String,
    val content: T,
    val id: String,
)