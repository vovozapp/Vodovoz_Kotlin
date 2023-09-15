package com.vodovoz.app.common.notification

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vodovoz.app.R
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.extensions.fromHtml
import org.json.JSONObject

class NotificationManager : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val messageNotification = message.notification

        if (messageNotification != null) {

            val jsonData = if (message.data.isNotEmpty()) {
                JSONObject(message.data.toString())
            } else {
                JSONObject()
            }

            debugLog { "jsonData $jsonData" }
            debugLog { "imageUrl ${messageNotification.imageUrl}" }

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
            .setArguments(bundleOf("push" to data.toString()))
            .setDestination(R.id.splashFragment)
            .createPendingIntent()

        val bitmap = getBitmap(not.imageUrl)
        debugLog { "large icon bitmap $bitmap" }
        val bigPictureStyle = NotificationCompat.BigPictureStyle().also {
            it.setBigContentTitle(not.title)
            it.setSummaryText(not.body.fromHtml())
            it.bigPicture(bitmap)
        }

        val notification =
            NotificationCompat.Builder(this, NotificationChannels.NOTIFICATION_CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.png_logo)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent)
                .setLargeIcon(bitmap)
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
            .setArguments(bundleOf("push" to data.toString()))
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

    private fun getBitmap(fileUri: Uri?): Bitmap? {
        return Glide
            .with(applicationContext)
            .asBitmap()
            .load(fileUri)
            .submit()
            .get()
    }
}