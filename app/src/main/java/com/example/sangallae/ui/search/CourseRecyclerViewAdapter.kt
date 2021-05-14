package com.example.sangallae.ui.search


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.findFragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sangallae.GlobalApplication

import com.example.sangallae.R
import com.example.sangallae.retrofit.models.Course

class CourseRecyclerViewAdapter : RecyclerView.Adapter<CourseRecyclerViewAdapter.CourseItemViewHolder>() {

    private var courseList = ArrayList<Course>()

    inner class CourseItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

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
            courseDistanceText.text = courseItem.distance
            courseHeightText.text = courseItem.height
            courseTimeText.text = courseItem.time
            courseDifficultyText.text = courseItem.diffiulty.toString()
            courseReviewText.text = courseItem.score
            courseReviewCountText.text = courseItem.review_cnt


            Glide.with(GlobalApplication.instance).load(courseItem.thumbnail)
                .placeholder(R.drawable.ic_baseline_photo_24).into(courseImageView)

//            itemView.setOnClickListener {
//                val navController: NavController = Navigation.findNavController(itemView)
//                val action = SearchFragmentDirections.actionSearchFragment3ToCourseDetailFragment2(courseId = courseItem.id)
//                navController.navigate(action)
//            }
            val pos = adapterPosition
            if(pos!= RecyclerView.NO_POSITION)
            {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView, courseItem.id, pos)
                }
            }
        }

    }

    interface OnItemClickListener{
        fun onItemClick(v:View, data: Int, pos : Int)
    }
    private var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseItemViewHolder {
        return CourseItemViewHolder(
            LayoutInflater
            .from(parent.context)
            .inflate(R.layout.layout_course_item, parent, false))
    }

    override fun onBindViewHolder(
        holder: CourseItemViewHolder,
        position: Int
    ) {
        holder.bindWithView(this.courseList[position])
    }

    override fun getItemCount(): Int {
        return this.courseList.size
    }

    fun submitList(courseList: ArrayList<Course>){
        this.courseList = courseList
    }
}