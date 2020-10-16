  package com.thienbinh.serviceapplication.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.thienbinh.serviceapplication.MainActivity
import com.thienbinh.serviceapplication.model.CountVariableModel
import com.thienbinh.serviceapplication.notification.MainNotification
import java.util.*
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class MainService : Service() {
  companion object {
    const val COUNT_DATA_DELAY = 1000L
  }

  private val mBinder = MainServiceBinder()
  private var mMainNotification: MainNotification? = null

  private var mScheduledExecutorService: ScheduledExecutorService? = null
  private var mScheduledFuture: ScheduledFuture<*>? = null

  private var mEventListener = object : IOnServiceChangeListener {
    override fun onCountChangeListener(newValue: Int) {
      sendBroadcast(Intent().also {
        it.action = MainActivity.MAIN_ACTION_RECEIVER_COUNT_ACTION
        it.putExtra(MainActivity.MAIN_ACTION_RECEIVER_COUNT_DATA, newValue)
      })

      mMainNotification?.makeNotification(newValue)
    }
  }

  private var mCount = CountVariableModel(mEventListener)

  fun getCurrentCount() = mCount.getCurrentValue()

  override fun onBind(intent: Intent?): IBinder? {
    return mBinder
  }

  override fun onCreate() {
    super.onCreate()

    mMainNotification = MainNotification(applicationContext, this@MainService)
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    when (intent?.action) {
      MainActivity.MAIN_ACTION_START_COUNT_ACTION -> {
        startScheduleCountData()
      }
      MainActivity.MAIN_ACTION_CANCEL_COUNT_ACTION -> {
        pauseScheduleCountData()
      }
    }

    return START_NOT_STICKY
  }

  private fun startScheduleCountData() {
    if (mScheduledExecutorService == null) {
      mScheduledExecutorService = ScheduledThreadPoolExecutor(1)
    }

    if (mScheduledFuture?.isDone == false) return

    mScheduledFuture = mScheduledExecutorService!!.scheduleWithFixedDelay({
      Log.d("Binh", "Count: ${mCount.getCurrentValue()}")

      mCount.increaseCount(1)
    }, COUNT_DATA_DELAY, COUNT_DATA_DELAY, TimeUnit.MILLISECONDS)
  }

  private fun pauseScheduleCountData() {
    mScheduledFuture?.apply { cancel(true) }
  }

  inner class MainServiceBinder : Binder() {
    fun getService(): MainService {
      return this@MainService
    }
  }
}

interface IOnServiceChangeListener {
  fun onCountChangeListener(newValue: Int)
}
