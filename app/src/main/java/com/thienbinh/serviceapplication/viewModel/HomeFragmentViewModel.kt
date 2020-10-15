package com.thienbinh.serviceapplication.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeFragmentViewModel: ViewModel() {
  val count = MutableLiveData<Int>().apply {
    value = 0
  }

  fun updateCount(value: Int){
    count.value = value
  }
}
