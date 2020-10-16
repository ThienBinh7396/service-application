package com.thienbinh.serviceapplication.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.thienbinh.serviceapplication.IOnCountChangeEventListener
import com.thienbinh.serviceapplication.MainActivity
import com.thienbinh.serviceapplication.R
import com.thienbinh.serviceapplication.base.BaseFragment
import com.thienbinh.serviceapplication.databinding.FragmentHomeBinding
import com.thienbinh.serviceapplication.service.MainService
import com.thienbinh.serviceapplication.viewModel.HomeFragmentViewModel
import java.security.Provider

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

      btnHideNavigation.setOnClickListener {
        btnHideNavigation.apply {
          text = if (mainActivity?.checkIsShowBottomNavigation() == true) "Show Navigation" else "Hide Navigation"
        }
        mainActivity?.apply {
          toggleBottomNavigation(!checkIsShowBottomNavigation())
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

  override fun onResume() {
    super.onResume()

    viewModel.updateCount(mainActivity?.mMainService?.getCurrentCount() ?: 0)
  }

  override fun onStart() {
    super.onStart()

    Log.d("Binh", "Home fragment start")

    mainActivity?.registerCountChangeEventListener(object : IOnCountChangeEventListener {
      override fun onCountChangeEventListener(newValue: Int) {
        viewModel.updateCount(newValue)
      }
    })

    viewModel.updateCount(mainActivity?.mMainService?.getCurrentCount() ?: 0)
  }

  override fun onDestroy() {
    super.onDestroy()

    Log.d("Binh", "Home fragment destroy")
  }

  override fun onGetViewModel(): Class<HomeFragmentViewModel> = HomeFragmentViewModel::class.java
}