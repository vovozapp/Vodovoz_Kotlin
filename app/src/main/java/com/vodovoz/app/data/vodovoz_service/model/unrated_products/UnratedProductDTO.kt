package com.vodovoz.app.data.vodovoz_service.model.unrated_products

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UnratedProductDTO(
    @SerializedName("NAME")
    @Expose
    val NAME: String?,

    @SerializedName("ID")
    @Expose
    val ID: Long?,

    @SerializedName("DETAIL_PICTURE")
    @Expose
    val DETAIL_PICTURE: String?
)