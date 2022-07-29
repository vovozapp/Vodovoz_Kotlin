package com.vodovoz.app.data.model.common

class UserDataEntity(
    val id: Long,
    var firstName: String,
    var secondName: String,
    var email: String,
    val sex: String,
    val registerDate: String,
    var avatar: String,
    var phone: String,
    var birthday: String,
    var token: String
)