package com.vodovoz.app.feature.bottom.services.detail.model

import com.vodovoz.app.data.model.common.ProductEntity
import com.vodovoz.app.ui.model.ProductUI

class ServiceDetailEntity(
    val dataEntity: ServiceDetailDataEntity?
)

class ServiceDetailDataEntity(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val preview: String? = null,
    val blocksList: List<ServiceDetailBlockEntity> = listOf(),
)

class ServiceDetailBlockEntity(
    val blockTitle: String?,
    val coef: Int?,
    val extProductId: String?,
    val productEntityList: List<ProductEntity> = listOf()
)

data class ServiceDetailUI(
    val id: String?,
    val name: String?,
    val description: String?,
    val preview: String?,
    val blocksList: List<ServiceDetailBlockUI>,
)

data class ServiceDetailBlockUI(
    val blockTitle: String?,
    val coef: Int?,
    val extProductId: String?,
    val productList: List<ProductUI>
)