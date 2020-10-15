package com.thienbinh.serviceapplication.model

import com.thienbinh.serviceapplication.service.IOnServiceChangeListener
import java.util.*

class CountVariableModel(private var mEventListener: IOnServiceChangeListener?) {
  private var count: Int = 0
    set(value){
      field = value

      callbackOnCountChangeListener()
    }

  private fun callbackOnCountChangeListener(){
    mEventListener?.onCountChangeListener(count)
  }

  fun resetCount(){
    this.count = 0
  }

  fun getCurrentValue() = this.count

  fun increaseCount(amount: Int){
    this.count += amount
  }

  fun decreaseCount(amount: Int){
    this.count -= amount
  }
}