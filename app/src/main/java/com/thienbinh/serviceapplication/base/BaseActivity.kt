package com.thienbinh.serviceapplication.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.thienbinh.serviceapplication.notification.MainNotification

abstract class BaseActivity: AppCompatActivity() {
  lateinit var mainNotification: MainNotification

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    mainNotification = MainNotification(applicationContext)
  }
}