package com.example.sangallae.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sangallae.retrofit.models.CourseItem

class MyRecordViewModel {
    private val _recordValue = MutableLiveData<CourseItem>()

    val recordValue: LiveData<CourseItem> = _recordValue

    fun setRecordValue (input: CourseItem) {
        _recordValue.value = input
    }
}