package com.example.sangallae.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sangallae.R
import com.example.sangallae.databinding.ActivityCourseDetailBinding
import com.example.sangallae.databinding.CourseDetailFragmentBinding
import com.example.sangallae.databinding.FragmentMyrecordBinding
import com.example.sangallae.retrofit.models.CourseItem
import com.example.sangallae.retrofit.models.RecordItem
import com.example.sangallae.ui.detail.CourseDetailActivity
import com.example.sangallae.ui.home.CourseViewAdapter
import com.example.sangallae.ui.home.HomeViewModel
import com.example.sangallae.utils.Constants
import com.example.sangallae.utils.RESPONSE_STATUS
import com.example.sangallae.utils.Usage
import com.jeongdaeri.unsplash_app_tutorial.retrofit.RetrofitManager

class MyRecordFragment : Fragment() {
    private lateinit var recordViewModel: MyRecordViewModel
    private lateinit var recordViewAdapter: MyRecordViewAdapter
    private var courseList = ArrayList<RecordItem>()

    private var _binding: FragmentMyrecordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMyrecordBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.myRecordToolbar.inflateMenu(R.menu.profile_menu)
        setHasOptionsMenu(true)

        this.recordViewAdapter = MyRecordViewAdapter()
        this.recordViewAdapter.submitList(courseList)

        //root activity view context this.context 중에 root만 되네
        binding.myRecord.layoutManager =
            LinearLayoutManager(
                context, //activity?
                RecyclerView.VERTICAL,
                false
            )
        binding.myRecord.adapter =
            this.recordViewAdapter
        binding.myRecord.addItemDecoration(DividerItemDecoration(context,1))

        recordApiCall()

        return view
    }

    private fun recordApiCall() {
        val retrofit = RetrofitManager(Usage.ACCESS)
        retrofit.recordList(completion = { status, list ->
            when (status) {
                RESPONSE_STATUS.OKAY -> {
                    //Log.d(Constants.TAG, "PhotoCollectionActivity - searchPhotoApiCall() called 응답 성공 / list.size : ${list?.size}")
                    if (list != null) {
                        this.courseList.clear()
                        this.courseList = list
                        recordViewAdapter.submitList(this.courseList)
                        recordViewAdapter.notifyDataSetChanged()
                        Log.d(Constants.TAG,"여기까지 됨 $courseList")
                        //
//                        popularCourseAdapter.submitList(this.recCourseList)
//                        popularCourseAdapter.notifyDataSetChanged()
                    }
                }
                else -> {
                    Log.d(
                        Constants.TAG,
                        "ProfileFragment-OncreateView-homeLoadApiCall() ${list.toString()}"
                    )
                    Toast.makeText(this.context, "페이지를 로드할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}