package com.example.sangallae.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sangallae.retrofit.models.Course
import com.example.sangallae.retrofit.models.Profile

class ProfileViewModel : ViewModel() {

    private val _profileValue = MutableLiveData<Profile>()

    val profileValue: LiveData<Profile> = _profileValue

    fun setProfileValue (input: Profile) {
        _profileValue.value = input
    }
}