package com.thienbinh.serviceapplication

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.thienbinh.serviceapplication.base.BaseNavigationManager
import com.thienbinh.serviceapplication.databinding.ActivityMainBinding
import com.thienbinh.serviceapplication.service.MainService

class TestActivity : AppCompatActivity() {

  var mMainService: MainService? = null
  private var mBound: Boolean = false

  private lateinit var receiverCountData: BroadcastReceiver

  var countValue = MutableLiveData<Int>().apply {
    value = 0
  }

  private var mServiceConnection = object : ServiceConnection {
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
      if (service is MainService.MainServiceBinder) {
        mMainService = service.getService()
        mBound = true

        countValue.value = service.getService().getCurrentCount()
      }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
      mBound = false
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_test)
    setSupportActionBar(findViewById(R.id.toolbar))

    findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
      Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        .setAction("Action", null).show()
    }

    countValue.observe(this, {
      findViewById<TextView>(R.id.tvCountText).apply{
        text = it.toString()
      }
    })

    setupBroadcastReceiver()
  }

  private fun setupBroadcastReceiver() {
    receiverCountData = object : BroadcastReceiver() {
      override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
          MainActivity.MAIN_ACTION_RECEIVER_COUNT_ACTION -> {
            val data = intent.getIntExtra(MainActivity.MAIN_ACTION_RECEIVER_COUNT_DATA, 0)

            countValue.postValue(data)
          }
        }
      }
    }

    val intentFilter = IntentFilter()
    intentFilter.addAction(MainActivity.MAIN_ACTION_RECEIVER_COUNT_ACTION)

    registerReceiver(receiverCountData, intentFilter)
  }

  override fun onStart() {
    super.onStart()

    Log.d("Binh", "MainActivity start")

    bindService(Intent(this, MainService::class.java), mServiceConnection, BIND_ADJUST_WITH_ACTIVITY)
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