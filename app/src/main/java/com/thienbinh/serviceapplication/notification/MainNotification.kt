package com.thienbinh.serviceapplication.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat
import com.thienbinh.serviceapplication.MainActivity
import com.thienbinh.serviceapplication.R
import com.thienbinh.serviceapplication.service.MainService


class MainNotification(context: Context, var service: Service? = null) {
  companion object {
    private const val MAIN_NOTIFICATION_ID = 731996
    private const val MAIN_NOTIFICATION_CHANNEL_ID = "731996"
    private const val MAIN_NOTIFICATION_CHANNEL_NAME = "MAIN_NOTIFICATION_CHANNEL_NAME"

    const val KEY_TEXT_REPLY = "KEY_TEXT_REPLY"
  }

  private var mContext: Context? = null
  private var isChannelCreated = false
  private var pendingIntentToActivity: PendingIntent
  private var mNotificationManager: NotificationManager? = null
  private var mNotificationBuilder: NotificationCompat.Builder

  init {
    mContext = context
    mNotificationBuilder = NotificationCompat.Builder(context, MAIN_NOTIFICATION_CHANNEL_ID)
    createNotificationChannel()

    pendingIntentToActivity = PendingIntent.getActivity(
      mContext, 0, Intent(
        mContext,
        MainActivity::class.java
      ).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      }, PendingIntent.FLAG_UPDATE_CURRENT
    )
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
        .setColor(ContextCompat.getColor(it, R.color.colorPrimary))
        .setContentTitle(it.getString(R.string.app_name))
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setCategory(NotificationCompat.CATEGORY_SERVICE)
        .setContentText("Current: $count")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setOngoing(true)
        .setOnlyAlertOnce(true)
        .setContentIntent(pendingIntentToActivity)
        .setStyle(
          NotificationCompat.BigTextStyle()
            .bigText("Hello from Android $count \nMuch longer text that cannot fit one line...")
        )
        .apply {
          val replyLabel = "Type to reply..."
          val remoteInput = RemoteInput.Builder(KEY_TEXT_REPLY).setLabel(replyLabel).build()
          val resultIntent = Intent(it, MainActivity::class.java)
          resultIntent.putExtra("notificationId", MAIN_NOTIFICATION_ID)
          resultIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

          val resultPendingIntent =
            PendingIntent.getActivity(it, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)

          val replyAction: NotificationCompat.Action = NotificationCompat.Action.Builder(
            R.drawable.icon_send,
            "REPLY",
            resultPendingIntent
          )
            .addRemoteInput(remoteInput)
            .setAllowGeneratedReplies(true)
            .build()

          addAction(replyAction)

          val replyLabel2 = "reply..."
          val remoteInput2 = RemoteInput.Builder(KEY_TEXT_REPLY).setLabel(replyLabel).build()
          val resultIntent2 = Intent(it, MainActivity::class.java)
          resultIntent2.putExtra("notificationId", MAIN_NOTIFICATION_ID)
          resultIntent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

          val resultPendingIntent2 =
            PendingIntent.getActivity(it, 0, resultIntent2, PendingIntent.FLAG_UPDATE_CURRENT)

          val replyAction2: NotificationCompat.Action = NotificationCompat.Action.Builder(
            R.drawable.icon_send,
            "REPLY",
            resultPendingIntent2
          )
            .addRemoteInput(remoteInput2)
            .setAllowGeneratedReplies(true)
            .build()

          addAction(replyAction2)

        }
        .build()
        .apply {
          flags = Notification.FLAG_ONGOING_EVENT or Notification.FLAG_NO_CLEAR
        }

      if (service != null && !MainService.isStartForegroundService){
        service?.startForeground(MAIN_NOTIFICATION_ID, notification)
        MainService.isStartForegroundService = true
      }else{
        mNotificationManager?.notify(MAIN_NOTIFICATION_ID, notification)
      }
    }
  }
}