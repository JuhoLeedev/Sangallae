package com.example.sangallae.retrofit.models

import java.io.Serializable

data class RecordItem(
    var id: Int,
    var fileName: String,
    var distance: String,
    var height: String,
    var time: String,
    var thumbnail: String,
    var date: String,
    var calorie: String
): Serializable
