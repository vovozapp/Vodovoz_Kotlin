package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.model.FIELD_DTO
import com.vodovoz.app.data.vodovoz_service.model.PreOrderDTO
import com.vodovoz.app.domain.general.model.FieldModel
import com.vodovoz.app.domain.general.model.PreOrderSectionModel

fun PreOrderDTO.toDomain(): PreOrderSectionModel {
    return PreOrderSectionModel(
        title = TITLE ?: "",
        fields = POLYA?.mapNotNull { it.toDomain() } ?: emptyList(),
        colorfulButton = KNOPKA?.toDomain()
            ?: throw IllegalArgumentException("Colorful button can't be null in PreOrder:$this")
    )
}

fun FIELD_DTO.toDomain(): FieldModel? {
    return FieldModel(
        id = SID ?: return null,
        title = TITLE ?: "",
        value = VALUE ?: "",
        valueType = TITLE_TYPE ?: "text",
        isRequired = REQUIRED == "Y",
        readOnly = ZAPRETREDAKTOR == "Y",
        supportingText = COMMENTS ?: ""
    )
}

