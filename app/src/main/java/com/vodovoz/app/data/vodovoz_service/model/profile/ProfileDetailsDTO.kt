package com.vodovoz.app.data.vodovoz_service.model.profile


import com.squareup.moshi.Json
import androidx.annotation.Keep
import com.vodovoz.app.data.vodovoz_service.model.BannerDTO
import com.vodovoz.app.data.vodovoz_service.model.TOVARY_DTO

@Keep
data class ProfileDetailsDTO(
    @Json(name = "PROFIL")
    val PROFIL: PROFIL_DTO?,
    @Json(name = "BLOCK")
    val BLOCK: List<PROFILE_BLOCK_DTO>?,
    @Json(name = "DENIGI")
    val DENIGI: List<DENIGI_DTO>?,
    @Json(name = "BANNER")
    val BANNER: List<BannerDTO>?,
    @Json(name = "MENU")
    val MENU: PROFILE_MENU_DTO?,
    @Json(name = "TOVARY")
    val TOVARY: TOVARY_DTO?
)