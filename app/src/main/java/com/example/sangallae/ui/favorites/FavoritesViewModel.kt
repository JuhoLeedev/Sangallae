package com.example.sangallae.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sangallae.retrofit.models.CourseItem

class FavoritesViewModel : ViewModel() {

    private val _courseItemValue = MutableLiveData<ArrayList<CourseItem>>()

    val courseItemValue: LiveData<ArrayList<CourseItem>> = _courseItemValue

    fun setCourseItemValue (input: ArrayList<CourseItem>) {
        _courseItemValue.value = input
    }
}