package com.example.sangallae.retrofit.models

import java.io.Serializable

data class Course(
    var id: Int,
    var name: String,
    var location: String,
    var distance: String,
    var max_height: String,
    var min_height: String,
    var ele_dif: String,
    var uphill: String,
    var downhill: String,
    var avg_speed: String,
    var avg_pace: String,
    var moving_time: String,
    var total_time: String,
    var diffiulty: String,
    var url: String,
    var review_cnt: String,
    var score: String,
    var thumbnail: String,
    var date: String
):Serializable
