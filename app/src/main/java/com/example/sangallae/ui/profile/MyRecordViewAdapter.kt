package com.example.sangallae.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sangallae.databinding.LayoutRecordItemBinding
import com.example.sangallae.retrofit.models.CourseItem
import com.example.sangallae.retrofit.models.RecordItem

class MyRecordViewAdapter : RecyclerView.Adapter<MyRecordViewAdapter.RecordItemViewHolder>(){
    private var recCourseList = ArrayList<RecordItem>()

    inner class RecordItemViewHolder(private val itemBinding: LayoutRecordItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bindWithView(recordItem: RecordItem) {
            itemBinding.recordTitle.text = recordItem.fileName
            itemBinding.recordTime.text = recordItem.date

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

    fun submitList(courseList: ArrayList<RecordItem>){
        this.recCourseList = courseList
    }
}