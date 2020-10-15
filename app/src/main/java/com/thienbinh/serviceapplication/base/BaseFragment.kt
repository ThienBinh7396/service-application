package com.thienbinh.serviceapplication.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thienbinh.serviceapplication.MainActivity

abstract class BaseFragment<V: ViewModel>: Fragment() {
  var mainActivity: MainActivity? = null
  lateinit var viewModel: V

  abstract fun onGetViewModel(): Class<V>

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    viewModel = ViewModelProvider(this@BaseFragment).get(onGetViewModel())
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    mainActivity = if (context is MainActivity) context else null
  }

  override fun onDetach() {
    super.onDetach()
    mainActivity = null
  }
}