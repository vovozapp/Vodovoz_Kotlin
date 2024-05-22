package com.vodovoz.app.common.permissions

import com.vodovoz.app.common.datastore.DataStoreRepository
import com.vodovoz.app.util.extensions.debugLog
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionsManager @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
){
    fun getPermissionChecked(permissionRequestCode: Int): Boolean {
        val ret =  dataStoreRepository.getBoolean("permissionCode_$permissionRequestCode") ?: false
        debugLog { "Permissions getPermissionChecked: $permissionRequestCode -> $ret" }
        return ret
    }

    fun setPermissionChecked(permissionRequestCode: Int, isChecked: Boolean) {
        debugLog { "Permissions setPermissionChecked: $permissionRequestCode -> $isChecked" }
        dataStoreRepository.putBoolean("permissionCode_$permissionRequestCode", isChecked)
    }
}