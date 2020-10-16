package com.thienbinh.serviceapplication.base

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.thienbinh.serviceapplication.R

class BaseNavigationManager(activity: BaseActivity) {
  private var navigationController: NavController? = null

  init {
    navigationController = activity.findNavController(R.id.navHostFragment)
  }

  fun navigate(graphId: Int, bundle: Bundle? = null){
    navigationController?.navigate(graphId, bundle)
  }
}