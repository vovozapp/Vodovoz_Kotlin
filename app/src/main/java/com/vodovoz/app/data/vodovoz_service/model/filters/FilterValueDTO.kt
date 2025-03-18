package com.vodovoz.app.data.vodovoz_service.model.filters

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.squareup.moshi.Json

@Keep
data class FilterValueDTO(
    @Expose
    @Json(name = "VALUE")
    val VALUE: String? = null,
    @Expose
    @Json(name = "ID")
    val ID: String? = null,
)
