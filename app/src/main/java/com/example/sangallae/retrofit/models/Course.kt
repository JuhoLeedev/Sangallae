package com.example.sangallae.retrofit.models

import java.io.Serializable

data class Course(
    var id: Int,
    var name: String?,
    var location: String?,
    var distance: String?,
    var height: String?,
    var speed: String?,
    var time: String?,
    var diffiulty: String?,
    var url: String?,
    var review_cnt: String?,
    var score: String?,
    var thumbnail: String?
):Serializable
