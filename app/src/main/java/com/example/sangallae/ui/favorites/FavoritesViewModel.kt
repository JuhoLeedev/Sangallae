package com.example.sangallae.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoritesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = ""
        //value = "This is favorites Fragment"
    }
    val text: LiveData<String> = _text
}