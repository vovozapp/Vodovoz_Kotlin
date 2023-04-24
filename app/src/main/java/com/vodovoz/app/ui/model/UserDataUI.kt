package com.vodovoz.app.ui.model

import com.vodovoz.app.feature.profile.userdata.Gender

data class UserDataUI(
    val id: Long,
    var firstName: String,
    var secondName: String,
    var email: String,
    var gender: Gender,
    val registerDate: String,
    var avatar: String,
    var phone: String,
    var birthday: String,
    var token: String
)