package com.example.sangallae.retrofit.models

import java.io.Serializable

data class Course(
    var id: String?,
    var name: String?,
    var distance: String?,
    var height: String?,
    var time: String?,
    var diffiulty: String?,
    var url: String?,
    var review_cnt: String?,
    var score: String?,
    var thumbnail: String?
):Serializable
