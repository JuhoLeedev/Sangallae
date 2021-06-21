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
import com.example.sangallae.databinding.LayoutMountainItemBinding
import com.example.sangallae.retrofit.models.CourseItem
import com.example.sangallae.retrofit.models.Mountain

class MountainViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1
    private var items = ArrayList<Mountain>()

    inner class MountainItemViewHolder(private val itemBinding: LayoutMountainItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bindWithView(courseItem: Mountain) {
            itemBinding.mountainTitle.text = courseItem.name
            itemBinding.itemLocationValue.text = courseItem.location
            itemBinding.itemHeighValue.text=courseItem.height
//            itemBinding.itemDistanceValue.text = courseItem.distance + "km"
//            itemBinding.itemHeighValue.text = courseItem.ele_dif
//            itemBinding.itemTimeValue.text = courseItem.moving_time
//            itemBinding.itemDifficulty.text = courseItem.difficulty
//            when(courseItem.difficulty){
//                "상" -> itemBinding.difficultyLayout.setBackgroundResource(R.drawable.rounded_background_red)
//                "중" -> itemBinding.difficultyLayout.setBackgroundResource(R.drawable.rounded_background_yellow)
//                "하" -> itemBinding.difficultyLayout.setBackgroundResource(R.drawable.rounded_background_green)
//            }
            Glide.with(GlobalApplication.instance).load(courseItem.thumbnail)
                .placeholder(R.drawable.ic_baseline_photo_24).into(itemBinding.mountainImageView)

            val pos = adapterPosition
            if(pos!= RecyclerView.NO_POSITION)
            {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView, courseItem.id, pos)
                }
            }
        }
    }

    inner class LoadingViewHolder(binding: LayoutLoadingItemBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnItemClickListener{
        fun onItemClick(v:View, data: Int, pos : Int)
    }

    private var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LayoutMountainItemBinding.inflate(layoutInflater, parent, false)
                MountainItemViewHolder(binding)
            }
            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LayoutLoadingItemBinding.inflate(layoutInflater, parent, false)
                LoadingViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        // 게시물과 프로그레스바 아이템뷰를 구분할 기준이 필요하다.
        return when (items[position].id) {
            -1 -> VIEW_TYPE_LOADING
            else -> VIEW_TYPE_ITEM
        }
    }

    fun submitList(courseList: ArrayList<Mountain>){
        items.addAll(courseList)
    }

    fun loadItem(){
        items.add(Mountain(-1," "," "," "," "))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is MountainItemViewHolder){
            holder.bindWithView(items[position])
        }
    }

    fun deleteLoading(){
        items.removeAt(items.lastIndex) // 로딩이 완료되면 프로그레스바를 지움
    }
}