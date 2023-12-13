package com.oceanscurse.groupstagesimulator.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
//https://developer.android.com/topic/architecture/recommendations
//https://developer.android.com/topic/architecture/data-layer
//https://developer.android.com/training/dependency-injection
//https://medium.com/@mutebibrian256/mastering-android-mvvm-architecture-developers-guide-3271e4c8908b#:~:text=Best%20Practices%20for%20MVVM%20Architecture&text=Keep%20the%20ViewModel%20Lightweight%3A%20The,remains%20independent%20and%20easily%20testable.
//https://developer.android.com/topic/libraries/architecture/viewmodel
//https://developer.android.com/topic/architecture/recommendations
class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}