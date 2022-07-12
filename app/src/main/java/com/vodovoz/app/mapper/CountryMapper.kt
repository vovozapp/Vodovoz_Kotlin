package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.CountryEntity
import com.vodovoz.app.ui.model.CountryUI

object CountryMapper  {

    fun List<CountryEntity>.mapToUI(): List<CountryUI> = mutableListOf<CountryUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun CountryEntity.mapToUI() = CountryUI(
        id = id,
        name = name,
        detailPicture = detailPicture
    )

}