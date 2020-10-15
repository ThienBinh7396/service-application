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

      lifecycleOwner = this@HomeFragment
    }
    return binding.root
  }

  override fun onStart() {
    super.onStart()

    mainActivity?.registerCountChangeEventListener(object : IOnCountChangeEventListener {
      override fun onCountChangeEventListener(newValue: Int) {
        viewModel.updateCount(newValue)
      }
    })
  }

  override fun onDestroy() {
    super.onDestroy()

    Log.d("Binh", "Home fragment destroy")
  }

  override fun onGetViewModel(): Class<HomeFragmentViewModel> = HomeFragmentViewModel::class.java
}