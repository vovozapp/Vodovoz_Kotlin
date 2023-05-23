package com.vodovoz.app.common.permissions

import android.Manifest
import android.content.Context
import androidx.fragment.app.FragmentActivity
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class PermissionsController(
    val context: Context
) {

    @AfterPermissionGranted(LOCATION_CONSTANTS.REQUEST_LOCATION_PERMISSION)
    fun methodRequiresLocationsPermission(activity: FragmentActivity, failure: () -> Unit = {}, success: () -> Unit = {}) {
        val perms = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (EasyPermissions.hasPermissions(context, *perms)) {
            success.invoke()
        } else {
            EasyPermissions.requestPermissions(
                activity, "Необходимы разрешения, чтобы использовать приложение.",
                LOCATION_CONSTANTS.REQUEST_LOCATION_PERMISSION, *perms
            )
            failure.invoke()
        }
    }

    @AfterPermissionGranted(LOCATION_CONSTANTS.REQUEST_CAMERA_PERMISSION)
    fun methodRequiresCameraPermission(activity: FragmentActivity, failure: () -> Unit = {}, success: () -> Unit = {}) {
        val perms = arrayOf(
            Manifest.permission.CAMERA,
        )
        if (EasyPermissions.hasPermissions(context, *perms)) {
            success.invoke()
        } else {
            EasyPermissions.requestPermissions(
                activity, "Необходимы разрешения, чтобы использовать приложение.",
                LOCATION_CONSTANTS.REQUEST_CAMERA_PERMISSION, *perms
            )
            failure.invoke()
        }
    }
}