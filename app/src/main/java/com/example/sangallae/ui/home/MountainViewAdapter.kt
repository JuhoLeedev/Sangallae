package com.example.sangallae.ui.home


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sangallae.GlobalApplication

import com.example.sangallae.R
import com.example.sangallae.databinding.LayoutMountainItemBinding
import com.example.sangallae.retrofit.models.CourseItem
import com.example.sangallae.retrofit.models.Mountain

class MountainViewAdapter : RecyclerView.Adapter<MountainViewAdapter.MountainItemViewHolder>() {

    private var courseList = ArrayList<Mountain>()

    inner class MountainItemViewHolder(private val itemBinding: LayoutMountainItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bindWithView(courseItem: Mountain) {
            itemBinding.mountainTitle.text = courseItem.name
            itemBinding.itemLocation.text = courseItem.location
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

    interface OnItemClickListener{
        fun onItemClick(v:View, data: Int, pos : Int)
    }
    private var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MountainItemViewHolder {
        val itemBinding = LayoutMountainItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MountainItemViewHolder(itemBinding)
    }

    override fun onBindViewHolder(
        holder: MountainItemViewHolder,
        position: Int
    ) {
        holder.bindWithView(this.courseList[position])
    }

    override fun getItemCount(): Int {
        return this.courseList.size
    }

    fun submitList(courseList: ArrayList<Mountain>){
        this.courseList = courseList
    }
}