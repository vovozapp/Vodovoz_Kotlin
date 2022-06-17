package com.vodovoz.app.data.model.features

import com.vodovoz.app.data.model.common.CountryEntity

class CountryBundleEntity(
    val title: String,
    val backgroundPicture: String,
    val countryEntityList: List<CountryEntity>
)