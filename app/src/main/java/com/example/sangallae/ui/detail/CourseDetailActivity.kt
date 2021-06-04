package com.example.sangallae.ui.detail

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navArgs
import com.bumptech.glide.Glide
import com.example.sangallae.GlobalApplication
import com.example.sangallae.R
import com.example.sangallae.databinding.ActivityCourseDetailBinding
import com.example.sangallae.ui.map.MainMapFragment
import com.example.sangallae.ui.map.RecordMapFragment
import com.example.sangallae.utils.*
import com.example.sangallae.utils.API.GPX_DIR
import com.example.sangallae.utils.API.LOCATION_PERMISSION_REQUEST_CODE
import com.example.sangallae.utils.API.READ_STORAGE_PERMISSIONS_REQUEST
import com.example.sangallae.utils.API.WRITE_STORAGE_PERMISSIONS_REQUEST
import com.jeongdaeri.unsplash_app_tutorial.retrofit.RetrofitManager
import com.naver.maps.map.LocationTrackingMode


class CourseDetailActivity : AppCompatActivity() {
    private lateinit var detailViewModel: CourseDetailViewModel
    private lateinit var binding: ActivityCourseDetailBinding
    private lateinit var url: String
    private var fileName: String? = null
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
            url = it.url
            Glide.with(GlobalApplication.instance).load(it.thumbnail)
                .placeholder(R.drawable.ic_baseline_photo_24).into(binding.detailThumbnail)
        })
        binding.detailDownload.setOnClickListener {
            if(PermissionUtils.requestPermission(this,WRITE_STORAGE_PERMISSIONS_REQUEST,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE)){
                        fileName = S3FileManager().downloadGPX(url, GPX_DIR)
                if(fileName != null) {
                    Toast.makeText(this, "Gpx가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(this, "파일을 다운로드 할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.detailFollow.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(binding.courseDetail.id, RecordMapFragment())
                .addToBackStack(CourseDetailActivity::class.java.simpleName)
                .commit()
//            if(PermissionUtils.requestPermission(this,WRITE_STORAGE_PERMISSIONS_REQUEST,
//                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE)){
//                fileName = S3FileManager().downloadGPX(url, GPX_DIR)
//                if(fileName != null) {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.course_detail, RecordMapFragment().apply {
//                            arguments = Bundle().apply {
//                                putString("name", fileName)
//                            }
//                        })
//                        .addToBackStack(CourseDetailActivity::class.java.simpleName)
//                        .commit()
//                }
//                else {
//                    Toast.makeText(this, "파일을 다운로드 할 수 없습니다.", Toast.LENGTH_SHORT).show()
//                }
//            }
        }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            WRITE_STORAGE_PERMISSIONS_REQUEST -> {
                // If request is cancelled, the result arrays are empty.
                if (PermissionUtils.permissionGranted(
                        requestCode,
                        WRITE_STORAGE_PERMISSIONS_REQUEST,
                        grantResults
                    )
                ) {
                    fileName = S3FileManager().downloadGPX(url, GPX_DIR)
                } else {
                }
                return
            }
        }
    }
}