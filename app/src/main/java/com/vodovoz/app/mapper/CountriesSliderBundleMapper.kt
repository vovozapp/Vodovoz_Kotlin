package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.features.CountriesSliderBundleEntity
import com.vodovoz.app.mapper.CountryMapper.mapToUI
import com.vodovoz.app.ui.model.custom.CountriesSliderBundleUI

object CountriesSliderBundleMapper {

    fun CountriesSliderBundleEntity.mapToUI() = CountriesSliderBundleUI(
        title = title,
        backgroundPicture = backgroundPicture,
        countryUIList = countryEntityList.mapToUI()
    )

}