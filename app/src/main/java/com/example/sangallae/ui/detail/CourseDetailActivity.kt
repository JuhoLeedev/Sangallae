package com.example.sangallae.ui.detail

import android.app.SearchManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navArgs
import com.bumptech.glide.Glide
import com.example.sangallae.GlobalApplication
import com.example.sangallae.R
import com.example.sangallae.databinding.ActivityCourseDetailBinding
import com.example.sangallae.databinding.CourseDetailFragmentBinding
import com.example.sangallae.retrofit.models.Course
import com.example.sangallae.utils.Constants
import com.example.sangallae.utils.RESPONSE_STATUS
import com.example.sangallae.utils.Usage
import com.jeongdaeri.unsplash_app_tutorial.retrofit.RetrofitManager

class CourseDetailActivity : AppCompatActivity() {
    private lateinit var detailViewModel: CourseDetailViewModel
    private lateinit var binding: ActivityCourseDetailBinding
    private val args: CourseDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseDetailBinding.inflate(layoutInflater)
        setSupportActionBar(binding.detailCourseToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val view = binding.root
        setContentView(view)
        val courseId = intent.getSerializableExtra("id") as Int
        getCourseDetailApiCall(courseId)

        detailViewModel = ViewModelProvider(this).get(CourseDetailViewModel::class.java)
        detailViewModel.courseDetailValue.observe(this, Observer {
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
        binding.detailDownload.setOnClickListener {  }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.detail_course_menu, menu)
        return true
    }

    private fun getCourseDetailApiCall(id: Int) {
        val retrofit = RetrofitManager(Usage.ACCESS)
        retrofit.getCourseDetail(id = id) { status, course ->
            when (status) {
                RESPONSE_STATUS.OKAY -> {
                    Log.d(
                        Constants.TAG,
                        "CourseDetailActivity - getCourseDetailApiCall() called 응답 성공"
                    )

                    if (course != null) {
                        detailViewModel.setCourseValue(course)
                    }
                }
                RESPONSE_STATUS.NO_CONTENT -> {
                    Toast.makeText(this, "등산로에 대한 검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "인터넷에 연결할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}