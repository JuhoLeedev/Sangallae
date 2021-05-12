package com.example.sangallae.retrofit.models

import java.io.Serializable

data class Profile (
    var user_id: String?,
    var picture: String?,
    var nickname: String?,
    var user_height_weight: String?,
    var total_distance: String?,
    var avg_distance: String?,
    var total_time: String?,
    var avg_time: String?,
    var total_height: String?,
    var max_height: String?,
    var avg_height: String?,
    var max_speed: String?,
    var avg_speed: String?,
    var total_calories: String?,
    var avg_calories: String?
): Serializable
