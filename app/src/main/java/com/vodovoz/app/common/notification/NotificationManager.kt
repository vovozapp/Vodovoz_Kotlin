package com.vodovoz.app.common.notification

import android.app.PendingIntent
import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vodovoz.app.ui.base.MainActivity


class NotificationManager : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)


        if (message.notification != null) {
            showNotification(message.notification!!)
        }


        /*if (message.notification != null) {
            if (message.notification?.imageUrl != null) {
                handleNotificationWithImage(
                    message.notification?.title,
                    message.notification?.body,
                    message.notification?.imageUrl.toString()
                )
            } else {
                handleNotificationWithoutImage(
                    message.notification?.title,
                    message.notification?.body
                )
            }
        }

        if (message.data.isNotEmpty()) {
            try {
                val json = JSONObject(message.data.toString())
                handleDataMessage(json)
            } catch (e: Exception) {
                debugLog { "NotifApp ${e.localizedMessage}" }
            }
        }*/
    }

    private fun showNotification(notification: RemoteMessage.Notification) {

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )


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