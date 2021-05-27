package com.example.sangallae.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sangallae.GlobalApplication

import com.example.sangallae.R
import com.example.sangallae.databinding.HomeItemBinding
import com.example.sangallae.retrofit.models.Mountain

class PopularMountainAdapter : RecyclerView.Adapter<PopularMountainAdapter.MtnItemViewHolder>() {

    private var mtnList = ArrayList<Mountain>()

    inner class MtnItemViewHolder(private val itemBinding: HomeItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bindWithView(mtnItem: Mountain) {
            itemBinding.courseName.text = mtnItem.name
            //itemBinding.courseInfo.text = courseItem.distance + " / " + courseItem.moving_time + " / " + courseItem.difficulty

            Glide.with(GlobalApplication.instance).load(mtnItem.thumbnail)
                .placeholder(R.drawable.ic_baseline_photo_24).into(itemBinding.courseImageView)

            val pos = adapterPosition
            if(pos!= RecyclerView.NO_POSITION)
            {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView, mtnItem.id, pos)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MtnItemViewHolder {
        val itemBinding = HomeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MtnItemViewHolder(itemBinding)
    }

    override fun onBindViewHolder(
        holder: MtnItemViewHolder,
        position: Int
    ) {
        holder.bindWithView(this.mtnList[position])
    }

    override fun getItemCount(): Int {
        return this.mtnList.size
    }

    fun submitList(mtnList: ArrayList<Mountain>){
        this.mtnList = mtnList
    }
}