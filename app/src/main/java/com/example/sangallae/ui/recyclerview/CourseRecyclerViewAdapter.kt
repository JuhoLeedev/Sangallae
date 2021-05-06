package com.example.sangallae.ui.recyclerview


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sangallae.R
import com.example.sangallae.models.Course

class CourseRecyclerViewAdapter : RecyclerView.Adapter<CourseItemViewHolder>() {

    private var courseList = ArrayList<Course>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseItemViewHolder {
        val courseItemViewHolder = CourseItemViewHolder(
            LayoutInflater
            .from(parent.context)
            .inflate(R.layout.layout_course_item, parent, false))

        return courseItemViewHolder
    }

    override fun onBindViewHolder(holder: CourseItemViewHolder, position: Int) {
        holder.bindWithView(this.courseList[position])
    }

    override fun getItemCount(): Int {
        return this.courseList.size
    }

    fun submitList(courseList: ArrayList<Course>){
        this.courseList = courseList
    }
}