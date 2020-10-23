package com.thienbinh.serviceapplication.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thienbinh.serviceapplication.MainActivity
import com.thienbinh.serviceapplication.TestActivity
import com.thienbinh.serviceapplication.base.BaseFragment
import com.thienbinh.serviceapplication.databinding.FragmentHomeBinding
import com.thienbinh.serviceapplication.service.MainService
import com.thienbinh.serviceapplication.viewModel.HomeFragmentViewModel

class HomeFragment : BaseFragment<HomeFragmentViewModel>() {
  lateinit var binding: FragmentHomeBinding

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentHomeBinding.inflate(inflater, container, false).apply {
      mHomeFragmentViewModel = viewModel

      btnStartCount.setOnClickListener {
        context?.startService(Intent(context, MainService::class.java).also {
          it.action = MainActivity.MAIN_ACTION_START_COUNT_ACTION
        })
      }
      btnPauseCount.setOnClickListener {
        context?.startService(Intent(context, MainService::class.java).also {
          it.action = MainActivity.MAIN_ACTION_CANCEL_COUNT_ACTION
        })
      }
      btnStopCount.setOnClickListener {
        context?.startService(Intent(context, MainService::class.java).also {
          it.action = MainActivity.MAIN_ACTION_STOP_COUNT_ACTION
        })
      }

      btnHideNavigation.setOnClickListener {
        btnHideNavigation.apply {
          text = if (mainActivity?.checkIsShowBottomNavigation() == true) "Show Navigation" else "Hide Navigation"
        }
        mainActivity?.apply {
          toggleBottomNavigation(!checkIsShowBottomNavigation())
        }
      }

      btnGotoTest.setOnClickListener {
        Intent(context, TestActivity::class.java).also{
          startActivity(it)
        }
      }

      lifecycleOwner = this@HomeFragment
    }

    Log.d("Binh", "Home fragment createview")
    return binding.root
  }

  override fun onPause() {
    super.onPause()
    Log.d("Binh", "Home fragment pasue")
  }

  override fun onStart() {
    super.onStart()

    mainActivity?.countValue?.observe(this,  {
        viewModel.updateCount(it)
    })
  }

  override fun onDestroy() {
    super.onDestroy()

    Log.d("Binh", "Home fragment destroy")
  }

  override fun onGetViewModel(): Class<HomeFragmentViewModel> = HomeFragmentViewModel::class.java
}