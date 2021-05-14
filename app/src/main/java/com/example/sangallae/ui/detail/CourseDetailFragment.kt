package com.example.sangallae.ui.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.sangallae.R
import com.example.sangallae.databinding.CourseDetailFragmentBinding
import com.example.sangallae.retrofit.models.Course
import com.example.sangallae.utils.Constants
import com.example.sangallae.utils.RESPONSE_STATUS
import com.example.sangallae.utils.Usage
import com.jeongdaeri.unsplash_app_tutorial.retrofit.RetrofitManager


class CourseDetailFragment : Fragment() {
    private lateinit var detailViewModel: CourseDetailViewModel
    private lateinit var detailToolbar: Toolbar
    private lateinit var course: Course
    private lateinit var binding: CourseDetailFragmentBinding
    private val args: CourseDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.course_detail_fragment, container, false)
        detailToolbar = root.findViewById(R.id.detail_course_toolbar)
        val intent = Intent(activity, CourseDetailFragment::class.java)
        startActivity(intent)
        val courseId = args.courseId
        setHasOptionsMenu(true)
        getCourseDetailApiCall(courseId)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        detailViewModel = ViewModelProvider(this).get(CourseDetailViewModel::class.java)
//        detailViewModel.courseDetailValue.observe(viewLifecycleOwner, Observer {
//            binding.detailLocationValue.text = it.location.toString()
//            binding.detailDifficultyValue.text = it.distance.toString()
//            binding.detailTimeValue.text = it.time.toString()
//            binding.detailSpeedValue.text = it.speed.toString()
//            binding.detailHeightValue.text = it.height.toString()
//            binding.detailDifficultyValue.text = it.diffiulty.toString()
//            binding.detailScoreValue.text = it.score.toString()
//        })
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

}