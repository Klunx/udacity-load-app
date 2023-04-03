package com.udacity.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.detail.ui.DetailActivity
import com.udacity.R

private val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(applicationContext: Context, status: String, repository: String) {

    val contentIntent = Intent(applicationContext, DetailActivity::class.java)

    contentIntent.apply {
        putExtra("status", status)
        putExtra("repository", repository)
    }

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_cloud_download)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(applicationContext.getString(R.string.notification_description))
        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_cloud_download,
            applicationContext.getString(R.string.notification_check_status),
            contentPendingIntent
        )

        .setPriority(NotificationCompat.PRIORITY_HIGH)
    notify(NOTIFICATION_ID, builder.build())
}