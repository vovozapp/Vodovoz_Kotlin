package com.vodovoz.app.data.local

import com.vodovoz.app.data.model.common.LastLoginDataEntity

interface LocalDataSource {

    fun fetchLastLoginData(): LastLoginDataEntity
    fun updateLastLoginData(lastLoginDataEntity: LastLoginDataEntity)

    fun fetchUserId(): Long?
    fun updateUserId(userId: Long)
    fun removeUserId()

    fun isAlreadyLogin(): Boolean

}