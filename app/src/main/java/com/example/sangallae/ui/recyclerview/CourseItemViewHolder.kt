package com.example.sangallae.ui.recyclerview

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sangallae.GlobalApplication
import com.example.sangallae.R
import com.example.sangallae.retrofit.models.Course

class CourseItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val courseImageView = itemView.findViewById<ImageView>(R.id.course_image_view)
    private val courseTitleText = itemView.findViewById<TextView>(R.id.course_title)
    private val courseDistanceText = itemView.findViewById<TextView>(R.id.item_distance_value)
    private val courseHeightText = itemView.findViewById<TextView>(R.id.item_heigh_value)
    private val courseTimeText = itemView.findViewById<TextView>(R.id.item_time_value)
    private val courseDifficultyText = itemView.findViewById<TextView>(R.id.item_difficulty)
    private val courseReviewText = itemView.findViewById<TextView>(R.id.item_review)
    private val courseReviewCountText = itemView.findViewById<TextView>(R.id.item_review_count)

    fun bindWithView(courseItem: Course) {
        courseTitleText.text = courseItem.name
        courseDistanceText.text = courseItem.distance + "km"
        courseHeightText.text = courseItem.height + "m"
        courseTimeText.text = courseItem.time
        courseDifficultyText.text = courseItem.diffiulty.toString()
        courseReviewText.text = courseItem.score
        courseReviewCountText.text = courseItem.review_cnt

        Glide.with(GlobalApplication.instance).load(courseItem.thumbnail)
            .placeholder(R.drawable.ic_baseline_photo_24).into(courseImageView)
    }
}