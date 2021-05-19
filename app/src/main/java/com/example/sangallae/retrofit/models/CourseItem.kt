package com.example.sangallae.retrofit.models

import java.io.Serializable

data class CourseItem(
    var id: Int,
    var name: String,
    var distance: String,
    var avg_speed: String,
    var ele_dif: String,
    var difficulty: String,
    var moving_time: String,
    var thumbnail: String
): Serializable
