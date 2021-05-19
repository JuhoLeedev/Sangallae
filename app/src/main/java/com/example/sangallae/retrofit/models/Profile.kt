package com.example.sangallae.retrofit.models

import java.io.Serializable

data class Profile (
    var user_id: String?,
    var picture: String,
    var nickname: String,
    //var user_height_weight: String?,
    var user_height: String,
    var user_weight: String,

    var total_distance: String,
    var avg_distance: String,
    var max_distance: String,

    var total_total_time: String,
    var avg_total_time: String,
    var max_total_time: String,

    var total_moving_time: String,
    var avg_moving_time:String,
    var max_moving_time:String,

    var max_speed: String,
    var avg_speed: String,

    var avg_pace: String,
    var max_pace: String,

    //var total_height: String,
    var max_height: String,
    var avg_height: String,

    var total_total_uphill:String,
    var avg_total_uphill:String,
    var max_total_uphill:String,

    var total_total_downhill:String,
    var avg_total_downhill:String,
    var max_total_downhill:String,

    var total_calories: String,
    var avg_calories: String
): Serializable
