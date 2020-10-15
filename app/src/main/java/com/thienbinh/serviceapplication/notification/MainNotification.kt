package com.thienbinh.serviceapplication.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.thienbinh.serviceapplication.MainActivity
import com.thienbinh.serviceapplication.R

class MainNotification(context: Context) {
  companion object {
    private const val MAIN_NOTIFICATION_ID = 731996
    private const val MAIN_NOTIFICATION_CHANNEL_ID = "731996"
    private const val MAIN_NOTIFICATION_CHANNEL_NAME = "MAIN_NOTIFICATION_CHANNEL_NAME"
  }

  private var mContext: Context? = null
  private var isChannelCreated = false
  private var intentToActivity :Intent
  private var mNotificationManager: NotificationManager? = null

  init {
    mContext = context
    createNotificationChannel()

    intentToActivity = Intent(mContext, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mContext != null && !isChannelCreated) {
      val channel = NotificationChannel(
        MAIN_NOTIFICATION_CHANNEL_ID, MAIN_NOTIFICATION_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_HIGH
      )

      mNotificationManager =
        mContext!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      mNotificationManager?.createNotificationChannel(channel)

      isChannelCreated = true
    }
  }

  fun makeNotification(count: Int) {
    mContext?.let {

      val builder = NotificationCompat.Builder(it, MAIN_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.logo_png)
        .setContentTitle(it.getString(R.string.app_name))
        .setContentText("Current: $count")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(false)

      mNotificationManager?.notify(MAIN_NOTIFICATION_ID, builder.build())
    }
  }
}