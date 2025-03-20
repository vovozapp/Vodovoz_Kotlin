package com.vodovoz.app.data.vodovoz_service.model.filters

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.google.gson.annotations.Expose

@Keep
@JsonClass(generateAdapter = true)
data class FiltersDTO(
    @Expose
    @Json(name = "CENAFILTER")
    val CENAFILTER: FiltersPriceDTO? = null,

    @Expose
    @Json(name = "DANNIE")
    val DANNIE: List<FilterDTO>? = null
)

@Keep
data class FiltersPriceDTO(
    @Expose
    @Json(name = "MIN")
    val MIN: Int? = null,

    @Expose
    @Json(name = "MAX")
    val MAX: Int? = null
)

@Keep
data class FilterDTO(
    @Expose
    @Json(name = "NAME")
    val NAME: String? = null,

    @Expose
    @Json(name = "CODE")
    val CODE: String? = null,

    @Expose
    @Json(name = "ZNACHEIE")
    val ZNACHEIE: FilterValuesDTO?
)

@Keep
data class FilterValuesDTO(
    @Expose
    @Json(name = "COUNT")
    val COUNT: Int?,
    @Expose
    @Json(name = "DATA")
    val DATA: List<String>?
)
