package com.thienbinh.serviceapplication

import android.app.Service
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.thienbinh.serviceapplication.base.BaseActivity
import com.thienbinh.serviceapplication.base.BaseNavigationManager
import com.thienbinh.serviceapplication.databinding.ActivityMainBinding
import com.thienbinh.serviceapplication.service.IOnServiceChangeListener
import com.thienbinh.serviceapplication.service.MainService
import java.util.*

class MainActivity : BaseActivity() {
  companion object {
    const val MAIN_ACTION_START_COUNT_ACTION = "MAIN_ACTION_START_COUNT_ACTION"
    const val MAIN_ACTION_CANCEL_COUNT_ACTION = "MAIN_ACTION_CANCEL_COUNT_ACTION"
    const val MAIN_ACTION_STOP_COUNT_ACTION = "MAIN_ACTION_STOP_COUNT_ACTION"
    const val MAIN_ACTION_RECEIVER_COUNT_ACTION = "MAIN_ACTION_RECEIVER_COUNT_ACTION"
    const val MAIN_ACTION_RECEIVER_COUNT_DATA = "MAIN_ACTION_RECEIVER_COUNT_DATA"
  }

  var mMainService: MainService? = null
  private var mBound: Boolean = false
  private lateinit var receiverCountData: BroadcastReceiver

  var countValue = MutableLiveData<Int>().apply {
    value = 0
  }

  private lateinit var binding: ActivityMainBinding

  lateinit var navigationManager: BaseNavigationManager

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

    binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)

    setupNavigation()
    setupBroadcastReceiver()

  }

  private fun setupBroadcastReceiver() {
    receiverCountData = object : BroadcastReceiver() {
      override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
          MAIN_ACTION_RECEIVER_COUNT_ACTION -> {
            val data = intent.getIntExtra(MAIN_ACTION_RECEIVER_COUNT_DATA, 0)

            countValue.postValue(data)
          }
        }
      }
    }

    val intentFilter = IntentFilter()
    intentFilter.addAction(MAIN_ACTION_RECEIVER_COUNT_ACTION)

    registerReceiver(receiverCountData, intentFilter)
  }

  private fun setupNavigation(){
    navigationManager = BaseNavigationManager(this)

    binding.bottomNavigation.setOnNavigationItemSelectedListener {
      when(it.itemId){
        R.id.home -> {
          navigationManager.navigate(R.id.action_to_homeFragment)
        }
        R.id.setting -> {
          navigationManager.navigate(R.id.action_to_settingFragment)
        }
      }

      return@setOnNavigationItemSelectedListener true
    }
  }

  fun toggleBottomNavigation(isShow: Boolean){
    binding.bottomNavigation.apply {
      translationY = if (isShow) 180f else 0f

      animate().translationY(if (isShow) 0f else 180f).start()

      visibility = if (isShow) View.VISIBLE else View.GONE
    }
  }

  fun checkIsShowBottomNavigation() = binding.bottomNavigation.visibility == View.VISIBLE

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

