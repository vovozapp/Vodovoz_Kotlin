package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.ParentSectionDataEntity
import com.vodovoz.app.data.model.common.SectionDataEntity
import com.vodovoz.app.data.model.common.SectionsEntity
import com.vodovoz.app.mapper.ParentSectionsMapper.mapToUI
import com.vodovoz.app.mapper.SectionDataMapper.mapToUI
import com.vodovoz.app.ui.model.ParentSectionDataUI
import com.vodovoz.app.ui.model.SectionDataUI
import com.vodovoz.app.ui.model.SectionsUI

object TopSectionsMapper {

    fun SectionsEntity.mapToUI() = SectionsUI(
        color = color,
        parentSectionDataUIList = parentSectionDataEntityList.mapToUI(),
    )
}

object ParentSectionsMapper {
    fun List<ParentSectionDataEntity>.mapToUI() = mapIndexed { index, parentSectionDataEntity ->
        ParentSectionDataUI(
            title = parentSectionDataEntity.title,
            sectionDataEntityUIList = parentSectionDataEntity.sectionDataEntityList.mapToUI(),
            isSelected = index == 0,
        )
    }
}


object SectionDataMapper {
    fun List<SectionDataEntity>.mapToUI() = map {
        SectionDataUI(
            id = it.id,
            name = it.name,
            imageUrl = it.imageUrl,
        )
    }
}