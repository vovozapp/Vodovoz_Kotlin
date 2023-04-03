package com.vodovoz.app.ui.model.custom

import android.os.Parcelable
import com.vodovoz.app.ui.model.CountryUI
import kotlinx.parcelize.Parcelize

@Parcelize
data class CountriesSliderBundleUI(
    val title: String,
    val backgroundPicture: String,
    val countryUIList: List<CountryUI>
): Parcelable
