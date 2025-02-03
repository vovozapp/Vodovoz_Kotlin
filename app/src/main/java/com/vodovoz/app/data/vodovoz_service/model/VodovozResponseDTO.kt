package com.vodovoz.app.data.vodovoz_service.model

data class VodovozResponseDTO<T>(
    val status: String?,
    val message: String?,
    val data: T?
)
