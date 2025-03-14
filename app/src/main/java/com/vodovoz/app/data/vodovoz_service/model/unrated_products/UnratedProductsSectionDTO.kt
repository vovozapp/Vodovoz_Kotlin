package com.vodovoz.app.data.vodovoz_service.model.unrated_products

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UnratedProductsSectionDTO(
    @SerializedName("TITLERAZDEL")
    @Expose
    val TITLERAZDEL: String?,

    @SerializedName("TITLETOVAR")
    @Expose
    val TITLETOVAR: String?,

    @SerializedName("LISTRAZDEL")
    @Expose
    val LISTRAZDEL: List<UnratedProductDTO>?,

    @SerializedName("VSEGOTOVAR")
    @Expose
    val VSEGOTOVAR: String?
)