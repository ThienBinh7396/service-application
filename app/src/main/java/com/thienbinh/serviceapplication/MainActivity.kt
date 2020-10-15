package com.thienbinh.serviceapplication

import android.app.Service
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.thienbinh.serviceapplication.base.BaseActivity
import com.thienbinh.serviceapplication.service.IOnServiceChangeListener
import com.thienbinh.serviceapplication.service.MainService
import java.util.*

class MainActivity : BaseActivity() {
  companion object {
    const val MAIN_ACTION_START_COUNT_ACTION = "MAIN_ACTION_START_COUNT_ACTION"
    const val MAIN_ACTION_CANCEL_COUNT_ACTION = "MAIN_ACTION_CANCEL_COUNT_ACTION"
    const val MAIN_ACTION_RECEIVER_COUNT_ACTION = "MAIN_ACTION_RECEIVER_COUNT_ACTION"
    const val MAIN_ACTION_RECEIVER_COUNT_DATA = "MAIN_ACTION_RECEIVER_COUNT_DATA"
  }

  private var mMainService: MainService? = null
  private var mBound: Boolean = false
  private lateinit var receiverCountData: BroadcastReceiver

  private var stackCountChangeEventListener = Stack<IOnCountChangeEventListener>()

  fun registerCountChangeEventListener(eventListener: IOnCountChangeEventListener){
    stackCountChangeEventListener.push(eventListener)
  }

  fun unregisterCountChangeEventListener(eventListener: IOnCountChangeEventListener){
    stackCountChangeEventListener.push(eventListener)
  }

  private var mServiceConnection = object : ServiceConnection {
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
      if (service is MainService.MainServiceBinder) {
        mMainService = service.getService()
        mMainService?.setupMainNotification(mainNotification)
        mBound = true
      }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
      mBound = false
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    setupBroadcastReceiver()
  }

  private fun setupBroadcastReceiver() {
    receiverCountData = object : BroadcastReceiver() {
      override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
          MAIN_ACTION_RECEIVER_COUNT_ACTION -> {
            val data = intent.getIntExtra(MAIN_ACTION_RECEIVER_COUNT_DATA, 0)

            val iterator = stackCountChangeEventListener.iterator()
            while (iterator.hasNext()){
              iterator.next().onCountChangeEventListener(data)
            }
          }
        }
      }
    }

    val intentFilter = IntentFilter()
    intentFilter.addAction(MAIN_ACTION_RECEIVER_COUNT_ACTION)

    registerReceiver(receiverCountData, intentFilter)
  }

  override fun onStart() {
    super.onStart()

    bindService(Intent(this, MainService::class.java), mServiceConnection, BIND_AUTO_CREATE)
  }

  override fun onStop() {
    super.onStop()

    unbindService(mServiceConnection)
  }

  override fun onDestroy() {
    super.onDestroy()
    unregisterReceiver(receiverCountData)
  }
}

interface IOnCountChangeEventListener{
  fun onCountChangeEventListener(newValue: Int)
}
