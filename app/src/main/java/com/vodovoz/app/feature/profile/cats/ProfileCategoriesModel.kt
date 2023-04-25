package com.vodovoz.app.feature.profile.cats

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class ProfileCategoriesModel(
    val block: List<BLOCKRESPONSE>?,
    val dannie: List<Dannie>?,
    val message: String?,
    val status: String?,
    val zakaz: List<Zakaz>?
): Parcelable {

    fun fetchProfileCategoryUIList() : MappedProfileCategories {
        var amount: Int? = null
        val list = dannie?.map {
            ProfileCategoryUI(
                it.RAZDEL,
                it.PODRAZDEL?.map {
                    if (it.ZNACHENI != null) {
                        amount = it.ZNACHENI
                    }
                    ProfileInsideCategoryUI(
                        it.ID,
                        it.NAME,
                        it.URL,
                        it.ZNACHENI
                    )
                }
            )
        }

        return MappedProfileCategories(list, amount)
    }
}

data class MappedProfileCategories(
    val list: List<ProfileCategoryUI>?,
    val amount: Int?
)

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

@Parcelize
@JsonClass(generateAdapter = true)
data class BLOCKRESPONSE(
    val ZAGALOVOK: String?,
    val OPISANIE: String?
) : Parcelable, Item {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_slider_block
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is BLOCKRESPONSE) return false

        return this == item
    }

}