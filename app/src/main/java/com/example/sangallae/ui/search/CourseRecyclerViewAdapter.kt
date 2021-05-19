package com.example.sangallae.ui.search


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sangallae.GlobalApplication

import com.example.sangallae.R
import com.example.sangallae.databinding.LayoutCourseItemBinding
import com.example.sangallae.retrofit.models.CourseItem

class CourseRecyclerViewAdapter : RecyclerView.Adapter<CourseRecyclerViewAdapter.CourseItemViewHolder>() {

    private var courseList = ArrayList<CourseItem>()

    inner class CourseItemViewHolder(private val itemBinding: LayoutCourseItemBinding) : RecyclerView.ViewHolder(itemView) {
        fun bindWithView(courseItem: CourseItem) {
            itemBinding.courseTitle.text = courseItem.name
            itemBinding.itemDistanceValue.text = courseItem.distance
            itemBinding.itemHeighValue.text = courseItem.ele_dif
            itemBinding.itemTimeValue.text = courseItem.moving_time
            itemBinding.itemDifficulty.text = courseItem.difficulty
            when(courseItem.difficulty){
                "상"-> {

                    itemBinding.difficultyLayout.setBackgroundColor(R.id.diff)
                }
            }
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
        val itemBinding = LayoutCourseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CourseItemViewHolder(itemBinding)
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

    fun submitList(courseList: ArrayList<CourseItem>){
        this.courseList = courseList
    }
}