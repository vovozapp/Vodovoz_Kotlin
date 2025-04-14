package com.vodovoz.app.domain.general.model.login

data class UserAuthInfoModel(
    val userId: Long,
    val authStatus: Boolean,
    val token: String
)
