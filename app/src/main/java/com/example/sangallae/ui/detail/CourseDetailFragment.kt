package com.example.sangallae.ui.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.sangallae.GlobalApplication
import com.example.sangallae.R
import com.example.sangallae.databinding.CourseDetailFragmentBinding
import com.example.sangallae.retrofit.models.Course
import com.example.sangallae.utils.Constants
import com.example.sangallae.utils.RESPONSE_STATUS
import com.example.sangallae.utils.Usage
import com.jeongdaeri.unsplash_app_tutorial.retrofit.RetrofitManager


class CourseDetailFragment : Fragment() {
    private lateinit var detailViewModel: CourseDetailViewModel
    private val courseId: Int by lazy { requireArguments().getInt("id") }
    private var _binding: CourseDetailFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CourseDetailFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.detailCourseToolbar.inflateMenu(R.menu.detail_course_menu)
        setHasOptionsMenu(true)
        getCourseDetailApiCall(courseId)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        detailViewModel = ViewModelProvider(this).get(CourseDetailViewModel::class.java)
        detailViewModel.courseDetailValue.observe(viewLifecycleOwner, {
            binding.detailCourseTitle.text = it.name
            binding.detailLocationValue.text = it.location
            binding.detailDistanceValue.text = it.distance
            binding.detailMovingTimeValue.text = it.moving_time
            binding.detailTotalTimeValue.text = it.total_time
            binding.detailAvgSpeedValue.text = it.avg_speed
            binding.detailAvgPaceValue.text = it.avg_pace
            binding.detailMaxHeightValue.text = it.max_height
            binding.detailMinHeightValue.text = it.min_height
            binding.detailEleDifValue.text = it.ele_dif
            binding.detailDifficultyValue.text = it.difficulty
            binding.detailUphillValue.text = it.uphill
            binding.detailDownhillValue.text = it.downhill
            binding.detailDateValue.text = it.date
            Glide.with(GlobalApplication.instance).load(it.thumbnail)
                .placeholder(R.drawable.ic_baseline_photo_24).into(binding.detailThumbnail)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun getCourseDetailApiCall(id: Int) {
        val retrofit = RetrofitManager(Usage.ACCESS)
        retrofit.getCourseDetail(id = id, completion = { status, course ->
            when(status){
                RESPONSE_STATUS.OKAY -> {
                    Log.d(Constants.TAG, "PhotoCollectionActivity - searchPhotoApiCall() called 응답 성공")

                    if (course != null){
                        detailViewModel.setCourseValue(course)
                    }
                }
                RESPONSE_STATUS.NO_CONTENT -> {
                    Toast.makeText(requireContext(), "등산로에 대한 검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(requireContext(), "검색을 할 수 없습니다.", Toast.LENGTH_SHORT).show()
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