package com.vodovoz.app.feature.profile.waterapp.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getColor
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.vodovoz.app.R
import com.vodovoz.app.common.notification.NotificationChannels
import com.vodovoz.app.common.notification.NotificationConfig

class WaterAppWorker(
    context: Context,
    workerParams: WorkerParameters,
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val pendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.nav_graph_profile)
            .setDestination(R.id.waterAppFragment)
            .createPendingIntent()

        val iconColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            applicationContext.getColor(R.color.bluePrimary)
        } else {
            getColor(applicationContext, R.color.bluePrimary)
        }

        val notification =
            NotificationCompat.Builder(applicationContext, NotificationChannels.NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Пора пить воду")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(iconColor)
                .setColorized(true)
                .setSmallIcon(R.mipmap.notification_icon)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                NotificationManagerCompat.from(applicationContext).notify(NotificationConfig.NOTIFICATION_ID, notification)
            }
        } else {
            NotificationManagerCompat.from(applicationContext).notify(NotificationConfig.NOTIFICATION_ID, notification)
        }
        return Result.success()
    }
}