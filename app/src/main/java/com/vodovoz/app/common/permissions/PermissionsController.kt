package com.vodovoz.app.common.permissions

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.fragment.app.FragmentActivity
import com.vodovoz.app.util.extensions.debugLog
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class PermissionsController(
    val context: Context
) {

    @AfterPermissionGranted(PermissionsConstants.REQUEST_LOCATION_PERMISSION)
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
                PermissionsConstants.REQUEST_LOCATION_PERMISSION, *perms
            )
            failure.invoke()
        }
    }

    @AfterPermissionGranted(PermissionsConstants.REQUEST_CAMERA_PERMISSION)
    fun methodRequiresCameraPermission(activity: FragmentActivity, failure: () -> Unit = {}, success: () -> Unit = {}) {
        val perms = arrayOf(
            Manifest.permission.CAMERA,
        )
        if (EasyPermissions.hasPermissions(context, *perms)) {
            success.invoke()
        } else {
            EasyPermissions.requestPermissions(
                activity, "Необходимы разрешения, чтобы использовать приложение.",
                PermissionsConstants.REQUEST_CAMERA_PERMISSION, *perms
            )
            failure.invoke()
        }
    }

    @AfterPermissionGranted(PermissionsConstants.REQUEST_RECORD_AUDIO_PERMISSION)
    fun methodRequiresRecordAudioPermission(activity: FragmentActivity, failure: () -> Unit = {}, success: () -> Unit = {}) {
        val perms = arrayOf(
            Manifest.permission.RECORD_AUDIO,
        )
        if (EasyPermissions.hasPermissions(context, *perms)) {
            success.invoke()
        } else {
            EasyPermissions.requestPermissions(
                activity, "Необходимы разрешения, чтобы использовать приложение.",
                PermissionsConstants.REQUEST_RECORD_AUDIO_PERMISSION, *perms
            )
            failure.invoke()
        }
    }

    @AfterPermissionGranted(PermissionsConstants.REQUEST_STORAGE_PERMISSION)
    fun methodRequiresStoragePermission(activity: FragmentActivity, failure: () -> Unit = {}, success: () -> Unit = {}) {
        val perms = arrayOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        )
        if (EasyPermissions.hasPermissions(context, *perms)) {
            success.invoke()
        } else {
            EasyPermissions.requestPermissions(
                activity, "Необходимы разрешения, чтобы использовать приложение.",
                PermissionsConstants.REQUEST_STORAGE_PERMISSION, *perms
            )
            failure.invoke()
        }
    }
}