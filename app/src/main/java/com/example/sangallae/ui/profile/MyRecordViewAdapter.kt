package com.example.sangallae.ui.profile

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sangallae.databinding.LayoutRecordItemBinding
import com.example.sangallae.retrofit.models.CourseItem
import com.example.sangallae.utils.Constants

class MyRecordViewAdapter : RecyclerView.Adapter<MyRecordViewAdapter.RecordItemViewHolder>(){
    private var recCourseList = ArrayList<CourseItem>()

    inner class RecordItemViewHolder(private val itemBinding: LayoutRecordItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bindWithView(recordItem: CourseItem) {
            itemBinding.recordTitle.text = recordItem.name
            itemBinding.recordTime.text = recordItem.moving_time

            val pos = adapterPosition
            if(pos!= RecyclerView.NO_POSITION)
            {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView, recordItem.id, pos)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordItemViewHolder {
        val itemBinding = LayoutRecordItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordItemViewHolder(itemBinding)
    }

    override fun onBindViewHolder(
        holder: RecordItemViewHolder,
        position: Int
    ) {
        holder.bindWithView(this.recCourseList[position])
    }

    override fun getItemCount(): Int {
        return this.recCourseList.size
    }

    fun submitList(courseList: ArrayList<CourseItem>){
        this.recCourseList = courseList
        Log.d(Constants.TAG, "submit: $recCourseList")
    }
}