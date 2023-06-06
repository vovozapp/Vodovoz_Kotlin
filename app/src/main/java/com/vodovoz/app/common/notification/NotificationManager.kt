package com.vodovoz.app.common.notification

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.Html
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vodovoz.app.R
import com.vodovoz.app.common.permissions.PermissionsController
import com.vodovoz.app.util.extensions.fromHtml
import org.json.JSONObject

class NotificationManager : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val messageNotification = message.notification

        if (messageNotification != null) {

            val jsonData = if (message.data.isNotEmpty()) {
                JSONObject(message.data.toString())
            } else {
                JSONObject()
            }

            if (messageNotification.imageUrl != null) {
                showLargeIconNotification(messageNotification, jsonData)
            } else {
                showSmallIconNotification(messageNotification, jsonData)
            }
        }
    }

    private fun showLargeIconNotification(not: RemoteMessage.Notification, data: JSONObject) {
        val pendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.nav_graph)
            .setArguments(bundleOf("push" to data))
            .setDestination(R.id.splashFragment)
            .createPendingIntent()

        val bigPictureStyle = NotificationCompat.BigPictureStyle().also {
            it.setBigContentTitle(not.title)
            it.setSummaryText(not.body.fromHtml())
            it.bigPicture(getBitmap(applicationContext.contentResolver, not.imageUrl))
        }

        val notification =
            NotificationCompat.Builder(this, NotificationChannels.NOTIFICATION_CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.png_logo)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent)
                .setStyle(bigPictureStyle)
                .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                NotificationManagerCompat.from(this)
                    .notify(NotificationConfig.NOTIFICATION_ID_BIG_IMAGE, notification)
            }
        } else {
            NotificationManagerCompat.from(this)
                .notify(NotificationConfig.NOTIFICATION_ID_BIG_IMAGE, notification)
        }
    }

    private fun showSmallIconNotification(not: RemoteMessage.Notification, data: JSONObject) {
        val pendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.nav_graph)
            .setArguments(bundleOf("push" to data))
            .setDestination(R.id.splashFragment)
            .createPendingIntent()

        val inboxStyle = NotificationCompat.InboxStyle().also {
            it.addLine(not.body)
        }

        val notification =
            NotificationCompat.Builder(this, NotificationChannels.NOTIFICATION_CHANNEL_ID)
                .setContentTitle(not.title)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.png_logo)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent)
                .setStyle(inboxStyle)
                .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                NotificationManagerCompat.from(this)
                    .notify(NotificationConfig.NOTIFICATION_ID, notification)
            }
        } else {
            NotificationManagerCompat.from(this)
                .notify(NotificationConfig.NOTIFICATION_ID, notification)
        }
    }

    private fun getBitmap(contentResolver: ContentResolver, fileUri: Uri?): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, fileUri!!))
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
            }
        } catch (e: Exception) {
            null
        }
    }

    /*private fun handleNotificationWithoutImage(title: String?, message: String?) {
        if (!isAppIsInBackground(applicationContext)) {
            val pushNotification = Intent(NotificationConfig.PUSH_NOTIFICATION).apply {
                putExtra("title", title)
                putExtra("message", message)
            }

            LocalBroadcastManager
                .getInstance(this)
                .sendBroadcast(pushNotification)

            val resultIntent = Intent(applicationContext, MainActivity::class.java).apply {
                putExtra("message", message)
            }
        } else {
            val pushNotification = Intent(NotificationConfig.PUSH_NOTIFICATION).apply {
                putExtra("title", title)
                putExtra("message", message)
            }

            LocalBroadcastManager
                .getInstance(this)
                .sendBroadcast(pushNotification)

            val resultIntent = Intent(applicationContext, MainActivity::class.java).apply {
                putExtra("message", message)
            }
        }
    }

    private fun handleNotificationWithImage(title: String?, message: String?, image: String?) {
        if (!isAppIsInBackground(applicationContext)) {

            val pushNotification = Intent(NotificationConfig.PUSH_NOTIFICATION).apply {
                putExtra("title", title)
                putExtra("message", message)
                putExtra("image", image)
            }

            LocalBroadcastManager
                .getInstance(this)
                .sendBroadcast(pushNotification)

            val resultIntent = Intent(applicationContext, MainActivity::class.java).apply {
                putExtra("message", message)
            }
        }
    }

    private fun handleDataMessage(json: JSONObject) {
        try {
            val titleScreen = json.getString("extra")
            val message = json.getString("ID")
            if (!isAppIsInBackground(applicationContext)) {
                val pushNotification = Intent(NotificationConfig.PUSH_NOTIFICATION).apply {
                    putExtra("extra", titleScreen)
                    putExtra("ID", message)
                }

                LocalBroadcastManager
                    .getInstance(this)
                    .sendBroadcast(pushNotification)

            } else {
                val resultIntent = Intent(applicationContext, MainActivity::class.java).apply {
                    putExtra("extra", titleScreen)
                    putExtra("ID", message)
                }
            }
        } catch (e: JSONException) {
            debugLog { "Json Exception: " + e.message }
        } catch (e: java.lang.Exception) {
            debugLog { "Exception: " + e.message }
        }
    }

    */
    /**
     * Method checks if the app is in background or not
     *//*
    private fun isAppIsInBackground(context: Context): Boolean {
        var isInBackground = true
        val am = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses = am.runningAppProcesses
        for (processInfo in runningProcesses) {
            if (processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (activeProcess in processInfo.pkgList) {
                    if (activeProcess == context.packageName) {
                        isInBackground = false
                    }
                }
            }
        }
        return isInBackground
    }*/
}