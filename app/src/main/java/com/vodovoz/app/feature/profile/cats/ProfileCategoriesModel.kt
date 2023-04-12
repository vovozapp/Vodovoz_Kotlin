package com.vodovoz.app.feature.profile.cats

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class ProfileCategoriesModel(
    val block: String?,
    val dannie: List<Dannie>?,
    val message: String?,
    val status: String?,
    val zakaz: List<Zakaz>?
): Parcelable {

    fun fetchProfileCategoryUIList() : List<ProfileCategoryUI>? {
        return dannie?.map {
            ProfileCategoryUI(
                it.RAZDEL,
                it.PODRAZDEL?.map {
                    ProfileInsideCategoryUI(
                        it.ID,
                        it.NAME,
                        it.URL,
                        it.ZNACHENI
                    )
                }
            )
        }
    }
}

@Parcelize
@JsonClass(generateAdapter = true)
data class Dannie(
    val PODRAZDEL: List<PODRAZDEL>?,
    val RAZDEL: String?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Zakaz(
    val ADRESSDOSTAVKI: String?,
    val DATE_OUT: String?,
    val ID: Int?,
    val INTERVAL: String?,
    val PRICE: Int?,
    val STATUS_NAME: String?,
    val STATUS_NAME_ID: String?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class PODRAZDEL(
    val ID: String?,
    val NAME: String?,
    val URL: String?,
    val ZNACHENI: Int?
) : Parcelable