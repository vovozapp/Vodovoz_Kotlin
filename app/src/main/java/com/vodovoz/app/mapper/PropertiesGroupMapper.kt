package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.PropertiesGroupEntity
import com.vodovoz.app.mapper.PropertyMapper.mapToUI
//import com.vodovoz.app.mapper.PropertiesGroupMapper.mapToUI
//import com.vodovoz.app.mapper.PropertyMapper.mapToUI
import com.vodovoz.app.ui.model.PropertiesGroupUI

object PropertiesGroupMapper {

    fun List<PropertiesGroupEntity>.mapToUI(): List<PropertiesGroupUI> = mutableListOf<PropertiesGroupUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun PropertiesGroupEntity.mapToUI() = PropertiesGroupUI(
        name = name,
        propertyUIList = propertyEntityList.mapToUI()
    )

}
