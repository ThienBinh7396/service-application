package com.thienbinh.serviceapplication.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.thienbinh.serviceapplication.MainActivity
import com.thienbinh.serviceapplication.R

class MainNotification(context: Context, var service: Service? = null) {
  companion object {
    private const val MAIN_NOTIFICATION_ID = 731996
    private const val MAIN_NOTIFICATION_CHANNEL_ID = "731996"
    private const val MAIN_NOTIFICATION_CHANNEL_NAME = "MAIN_NOTIFICATION_CHANNEL_NAME"
  }

  private var mContext: Context? = null
  private var isChannelCreated = false
  private var pendingIntentToActivity: PendingIntent
  private var mNotificationManager: NotificationManager? = null
  private var mNotificationBuilder: NotificationCompat.Builder

  private var first = false

  init {
    mContext = context
    mNotificationBuilder = NotificationCompat.Builder(context, MAIN_NOTIFICATION_CHANNEL_ID)
    createNotificationChannel()

    pendingIntentToActivity = PendingIntent.getActivity(mContext, 0, Intent(mContext, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }, PendingIntent.FLAG_UPDATE_CURRENT)
  }

  private fun createNotificationChannel() {
    if (mNotificationManager == null) {
      mNotificationManager =
        mContext!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mContext != null && !isChannelCreated) {
      val channel = NotificationChannel(
        MAIN_NOTIFICATION_CHANNEL_ID, MAIN_NOTIFICATION_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_DEFAULT
      )

      channel.setSound(null, null)
      channel.enableVibration(false)

      mNotificationManager?.createNotificationChannel(channel)

      isChannelCreated = true
    }
  }

  fun makeNotification(count: Int) {
    mContext?.let {

      val notification = mNotificationBuilder
        .setSmallIcon(R.drawable.logo_png)
        .setContentTitle(it.getString(R.string.app_name))
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setCategory(NotificationCompat.CATEGORY_SERVICE)
        .setContentText("Current: $count")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setOngoing(true)
        .setOnlyAlertOnce(true)
        .setContentIntent(pendingIntentToActivity)
        .build()
        .apply {
          flags = Notification.FLAG_ONGOING_EVENT or Notification.FLAG_NO_CLEAR
        }

      if (service != null && !first){
        service?.startForeground(MAIN_NOTIFICATION_ID, notification)
      }else{
        mNotificationManager?.notify(MAIN_NOTIFICATION_ID, notification)
      }

      first = true
    }
  }
}