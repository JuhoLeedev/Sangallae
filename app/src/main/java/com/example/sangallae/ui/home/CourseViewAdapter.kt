package com.example.sangallae.ui.home


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sangallae.GlobalApplication

import com.example.sangallae.R
import com.example.sangallae.databinding.LayoutCourseItemBinding
import com.example.sangallae.databinding.LayoutLoadingItemBinding
import com.example.sangallae.retrofit.models.CourseItem

class CourseViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1
    private var items = ArrayList<CourseItem>()

    // course item이 들어가는 경우
    inner class CourseItemViewHolder(private val itemBinding: LayoutCourseItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bindWithView(courseItem: CourseItem) {
            itemBinding.courseTitle.text = courseItem.name
            itemBinding.itemDistanceValue.text = courseItem.distance
            itemBinding.itemHeighValue.text = courseItem.ele_dif
            itemBinding.itemTimeValue.text = courseItem.moving_time
            itemBinding.itemDifficulty.text = courseItem.difficulty
            when(courseItem.difficulty){
                "상" -> itemBinding.difficultyLayout.setBackgroundResource(R.drawable.rounded_background_red)
                "중" -> itemBinding.difficultyLayout.setBackgroundResource(R.drawable.rounded_background_yellow)
                "하" -> itemBinding.difficultyLayout.setBackgroundResource(R.drawable.rounded_background_green)
            }
            Glide.with(GlobalApplication.instance).load(courseItem.thumbnail)
                .placeholder(R.drawable.ic_baseline_photo_24).into(itemBinding.courseImageView)

            val pos = adapterPosition
            if(pos!= RecyclerView.NO_POSITION)
            {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView, courseItem.id, pos)
                }
            }
        }
    }

    // progress bar item이 들어가는 경우
    inner class LoadingViewHolder(binding: LayoutLoadingItemBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnItemClickListener{
        fun onItemClick(v:View, data: Int, pos : Int)
    }
    private var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }

    override fun getItemViewType(position: Int): Int {
        // 게시물과 프로그레스바 아이템뷰를 구분할 기준이 필요하다.
        return when (items[position].id) {
            -1 -> VIEW_TYPE_LOADING
            else -> VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LayoutCourseItemBinding.inflate(layoutInflater, parent, false)
                CourseItemViewHolder(binding)
            }
            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LayoutLoadingItemBinding.inflate(layoutInflater, parent, false)
                LoadingViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int {
        return this.items.size
    }

    fun submitList(courseList: ArrayList<CourseItem>){
        items.addAll(courseList)
    }

    fun loadItem(){
        items.add(CourseItem(-1, " ", " ", " ", " ", " ", " "))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is CourseItemViewHolder){
            holder.bindWithView(items[position])
        }
    }

    fun deleteLoading(){
        items.removeAt(items.lastIndex) // 로딩이 완료되면 프로그레스바를 지움
    }
}