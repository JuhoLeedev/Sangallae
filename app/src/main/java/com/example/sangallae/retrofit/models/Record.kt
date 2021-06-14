package com.example.sangallae.retrofit.models

data class Record(

    val course: Int,
    val title: String,
    val filename: String,
    val distance: Double,
    val moving_time_sec: Long,
    val total_time_sec: Long,
    val moving_time_str: String,
    val total_time_str: String,
    val avg_speed: Double,
    val avg_pace: Double,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val max_height: Int,
    val min_height: Int,
    val ele_dif: Int,
    val total_uphill: Int,
    val total_downhill: Int,
    val difficulty: String,
    val calorie: Int,
    val date: String,
    val gpx_url: String,
    val thumbnail: String

)
