package com.vodovoz.app.data.model.common

class AboutServicesBundleEntity(
    val title: String? = null,
    val detail: String? = null,
    val serviceEntityList: List<ServiceEntity>
)

class ServiceEntity(
    val name: String,
    val price: String? = null,
    val detail: String? = null,
    val detailPicture: String? = null,
    val type: String
)

class ServiceOrderFormFieldEntity(
    val id: String = "",
    val name: String = "",
    val isRequired: Boolean = false,
    val defaultValue: String = ""
)