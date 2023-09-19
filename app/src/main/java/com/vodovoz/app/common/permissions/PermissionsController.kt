package com.vodovoz.app.common.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @AfterPermissionGranted(PermissionsConstants.REQUEST_NOTIFICATION_PERMISSION)
    fun methodRequiresNotificationPermission(activity: FragmentActivity, failure: () -> Unit = {}, success: () -> Unit = {}) {
        val perms = arrayOf(
            Manifest.permission.POST_NOTIFICATIONS
        )
        if (EasyPermissions.hasPermissions(context, *perms)) {
            success.invoke()
        } else {
            EasyPermissions.requestPermissions(
                activity, "Необходимы разрешения, чтобы использовать приложение.",
                PermissionsConstants.REQUEST_NOTIFICATION_PERMISSION, *perms
            )
            failure.invoke()
        }
    }

    fun fetchSystemFeatureExists() {
        val packageManager = context.packageManager
        if (packageManager?.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT) == true) {
            Toast.makeText(context, "Fingerprint sensor found!", Toast.LENGTH_SHORT).show()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (packageManager?.hasSystemFeature(PackageManager.FEATURE_FACE) == true) {
                Toast.makeText(context, "Face sensor found!", Toast.LENGTH_SHORT).show()
            }
            if (packageManager?.hasSystemFeature(PackageManager.FEATURE_IRIS) == true) {
                Toast.makeText(context, "Iris sensor found!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}