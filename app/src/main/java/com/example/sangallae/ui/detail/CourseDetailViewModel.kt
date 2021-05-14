package com.example.sangallae.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sangallae.retrofit.models.Course

class CourseDetailViewModel : ViewModel() {
    private val _courseDetailValue = MutableLiveData<Course>()

    val courseDetailValue: LiveData<Course> = _courseDetailValue

    fun setCourseValue (input: Course) {
        _courseDetailValue.value = input
    }
}