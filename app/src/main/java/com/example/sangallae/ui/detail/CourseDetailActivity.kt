package com.example.sangallae.ui.detail

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navArgs
import com.amazonaws.AmazonServiceException
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.AmazonS3URI
import com.bumptech.glide.Glide
import com.example.sangallae.GlobalApplication
import com.example.sangallae.R
import com.example.sangallae.databinding.ActivityCourseDetailBinding
import com.example.sangallae.retrofit.models.Favorite
import com.example.sangallae.retrofit.models.NewProfile
import com.example.sangallae.ui.map.RecordMapFragment
import com.example.sangallae.utils.*
import com.example.sangallae.utils.API.GPX_DIR
import com.example.sangallae.utils.API.WRITE_STORAGE_PERMISSIONS_REQUEST
import com.jeongdaeri.unsplash_app_tutorial.retrofit.RetrofitManager
import java.io.*
import java.net.MalformedURLException
import java.net.URI


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
            if(it.like_status)
                binding.favoriteBtn.setImageResource(R.drawable.ic_favorite_filled)
            else
                binding.favoriteBtn.setImageResource(R.drawable.ic_favorite_outline)
        })
        binding.detailDownload.setOnClickListener(clickDownloadLinearLayout())
        binding.detailFollow.setOnClickListener(clickFollowLinearLayout())
        binding.favoriteBtn.setOnClickListener(clickFavoriteLinearLayout())
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.detail_course_menu, menu)
        return true
    }

    private fun clickFavoriteLinearLayout(): View.OnClickListener? {
        return View.OnClickListener {
            val courseId = intent.getSerializableExtra("id") as Int
            toggleFavoriteApiCall(courseId)
        }
    }
    private fun clickDownloadLinearLayout(): View.OnClickListener {
        return View.OnClickListener {
            download(url)
        }
    }

    private fun clickFollowLinearLayout(): View.OnClickListener {
        return View.OnClickListener {
            download(url)
//            supportFragmentManager.beginTransaction()
//                .replace(binding.courseDetail.id, RecordMapFragment())
//                .addToBackStack(CourseDetailActivity::class.java.simpleName)
//                .commit()
            Log.d("test", "$fileName")
            if(fileName != null) {
                Log.d("test", "$fileName")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.course_detail, RecordMapFragment().apply {
                        arguments = Bundle().apply {
                            putString("name", fileName)
                        }
                    })
                    .addToBackStack(CourseDetailActivity::class.java.simpleName).commit()
            }
        }
    }
    fun downloadGPX(url: String, savepath: String) {
        object : Thread() {
            override fun run() {
                try {
                    val awsCredentials: AWSCredentials = BasicAWSCredentials(
                        Info.AWS_ACCESS_KEY,
                        Info.AWS_SECRET_KEY
                    )
                    val s3Client = AmazonS3Client(
                        awsCredentials,
                        Region.getRegion(Regions.AP_NORTHEAST_2)
                    )
                    try {
                        val fileToBeDownloaded = URI(url)
                        val s3URI = AmazonS3URI(fileToBeDownloaded)
                        Log.d(Constants.TAG, "s3 다운로드 테스트용입니다.")
                        Log.d(Constants.TAG, s3URI.key)
                        val s3Object = s3Client.getObject(s3URI.bucket, s3URI.key)
                        val reader = BufferedReader(
                            InputStreamReader(
                                `s3Object`.getObjectContent()
                            )
                        )
//                        var fileName = s3URI.key.split("/")[1]
                        fileName = s3URI.key.split("/")[1]
                        Log.d(Constants.TAG, "savepath는?" + savepath)
                        Log.d(Constants.TAG, "파일명은?"+fileName!!)
                        val file = File(savepath+fileName)
                        val writer: Writer = OutputStreamWriter(FileOutputStream(file))

                        while (true) {
                            val line = reader.readLine() ?: break
                            writer.write(
                                """
            $line

            """.trimIndent()
                            )
                        }

                        writer.close()
                        Log.d(Constants.TAG, "성공적으로 저장되었습니다.")
                        //displayTextInputStream(s3Object.objectContent);
                    } catch (e: AmazonServiceException) {
                        System.err.println(e.errorMessage)
                    }
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }
    private fun download(url: String){
//        if(PermissionUtils.requestPermission(this,WRITE_STORAGE_PERMISSIONS_REQUEST,
//                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE)){
        Log.d("test","여기는 다운로드")


        downloadGPX(url, GPX_DIR)
        Log.d("test","다운로드 성공~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
        Log.d(Constants.TAG, "다운로드 받은 파일명은???" + fileName)
        if(fileName != null) {
            Toast.makeText(this, "Gpx가 저장되었습니다.", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(this, "파일을 다운로드 할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
//        }
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
                if (PermissionUtils.permissionGranted(requestCode, WRITE_STORAGE_PERMISSIONS_REQUEST, grantResults)
                ) {
                    download(url)
                }
            }
        }
    }


    private fun toggleFavoriteApiCall(course_id : Int) {
        val retrofit = RetrofitManager(Usage.ACCESS)
        retrofit.toggleFavorite(Favorite(course_id), completion = { status ->
            Log.d(Constants.TAG, "status는?"+status)
            when (status) {
                RESPONSE_STATUS.OKAY -> {
                    Log.d(Constants.TAG, "toggleFavoriteApiCall()")
                    binding.favoriteBtn.setImageResource(R.drawable.ic_favorite_filled)
                    Toast.makeText(this, "찜 목록에 추가했습니다.", Toast.LENGTH_SHORT).show()
                }
                RESPONSE_STATUS.DELETE_SUCCESS -> {
                    Log.d(Constants.TAG, "toggleFavoriteApiCall()")
                    binding.favoriteBtn.setImageResource(R.drawable.ic_favorite_outline)
                    Toast.makeText(this, "찜 목록에서 제거했습니다.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Log.d(
                        Constants.TAG,
                        "CourseDetailActivity-OncreateView-toggleFavoriteApiCall() Error"
                    )
                    Toast.makeText(this, "찜 목록을 업데이트 할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}