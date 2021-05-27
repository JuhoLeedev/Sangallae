package com.example.sangallae.retrofit.models

import java.io.Serializable

data class Home (
//    var user_name:String,
    var id: Int, //courseid
    var name: String, //coursename
    var distance: String,
    var difficulty: String,
    var moving_time: String,
    var thumbnail: String
    ):Serializable