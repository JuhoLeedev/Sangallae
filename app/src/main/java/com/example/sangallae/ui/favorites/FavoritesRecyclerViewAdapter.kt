package com.example.sangallae.ui.favorites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sangallae.GlobalApplication
import com.example.sangallae.R
import com.example.sangallae.databinding.LayoutCourseItemBinding
import com.example.sangallae.retrofit.models.CourseItem

class FavoritesRecyclerViewAdapter : RecyclerView.Adapter<FavoritesRecyclerViewAdapter.FavoritesItemViewHolder>() {
    private var courseList = ArrayList<CourseItem>()

    inner class FavoritesItemViewHolder(private val itemBinding: LayoutCourseItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
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
        fun onItemClick(v: View, data: Int, pos : Int)
    }
    private var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesItemViewHolder {
        val itemBinding = LayoutCourseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoritesItemViewHolder(itemBinding)
    }

    override fun onBindViewHolder(
        holder: FavoritesItemViewHolder,
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