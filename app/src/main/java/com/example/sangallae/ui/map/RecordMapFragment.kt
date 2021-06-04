package com.example.sangallae.ui.map

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.amazonaws.AmazonServiceException
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.example.sangallae.R
import com.example.sangallae.utils.API.AWS_ACCESS_KEY
import com.example.sangallae.utils.API.AWS_SECRET_KEY
import com.example.sangallae.utils.API.S3_BUCKET
import com.example.sangallae.utils.Constants
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.widget.LocationButtonView
import java.io.File
import java.net.MalformedURLException
import java.time.Duration
import java.time.LocalDateTime

//class MyMapFragment : Fragment() {
//
//    private lateinit var mapViewModel: MapViewModel
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)
//        val root = inflater.inflate(R.layout.fragment_map, container, false)
//
//        val fm = childFragmentManager
//        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
//            ?: MapFragment.newInstance().also {

//                fm.beginTransaction().add(R.id.map, it).commit()
//            }
//        mapFragment.getMapAsync(this)
//
//        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, true)
//        return root
//    }
//
//    @UiThread
//    override fun onMapReady(naverMap: NaverMap) {
//        // ...
//    }
//}

class RecordMapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mapViewModel: MapViewModel
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap_: NaverMap
    @RequiresApi(Build.VERSION_CODES.N)
    var gg = MyGPX()
    var listenerFlag = false //위치 변경에 대한 리스너를 설정할 것인지 (일시정지/시작에 사용)
    lateinit var locationListener:NaverMap.OnLocationChangeListener
    //퍼미션 응답 처리 코드
    private val multiplePermissionsCode = 100
    var timeflag = false
    var startFlag = true

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)

        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_recording, container, false)
        //setContentView(R.layout.fragment_map)

        //val fm = supportFragmentManager
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }

        mapFragment.getMapAsync(this)

        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        mapFragment.getMapAsync {
            val locationButtonView = root.findViewById(R.id.current) as LocationButtonView
            locationButtonView.map = naverMap_
        }

        // 저장 및 종료
        val stopBtn = root.findViewById<ImageButton>(R.id.stopBtn)
        stopBtn.setOnClickListener {
            //목록 fragment로 넘어가기
            gg.saveGPX("/storage/emulated/0/gpxdata/테스트.gpx")
            uploadGPX("/storage/emulated/0/gpxdata/테스트.gpx", "test.gpx")
            Toast.makeText(this.context,"기록이 저장되었습니다.", Toast.LENGTH_SHORT).show()
            //naverMap_.removeOnLocationChangeListener(locationListener) //리스너 해제
            listenerFlag = false
        }

        //안내 중단
        val btn = root.findViewById<Button>(R.id.button)
        btn.setOnClickListener {
            Toast.makeText(this.context,"안내를 중단합니다.", Toast.LENGTH_SHORT).show()
            //naverMap_.removeOnLocationChangeListener(locationListener) //리스너 해제
            listenerFlag = false
        }
//
//        //시작 버튼
//        val startBtn = root.findViewById<ImageButton>(R.id.startBtn)
//        startBtn.setOnClickListener {
//            //목록 fragment로 넘어가기
//            listenerFlag = true
//            recordCurrentCourse(gg, naverMap_)
//
//            startBtn.visibility = GONE //시작버튼 사라지게
//            //startBtn.visibility = VISIBLE //일시정지 버튼 나타남
//        }

        //일시정지 버튼
        val pauseBtn = root.findViewById<ImageButton>(R.id.pauseBtn)
        pauseBtn.setOnClickListener {
            if(startFlag){ //처음 시작할 때
                startFlag = false
                Toast.makeText(this.context,"측정을 시작합니다.", Toast.LENGTH_SHORT).show()
                pauseBtn.setImageResource(R.drawable.ic_twotone_pause_circle_24)
                recordCurrentCourse(gg, naverMap_)
            }
            //목록 fragment로 넘어가기
            else if(listenerFlag){ //측정중이었으면
                pauseBtn.setImageResource(R.drawable.ic_twotone_play_circle_24)
                Toast.makeText(this.context,"일시정지 되었습니다.", Toast.LENGTH_SHORT).show()
                gg.pause()
            }
            else { //멈춰있으면
                pauseBtn.setImageResource(R.drawable.ic_twotone_pause_circle_24)
                Toast.makeText(this.context,"이어서 측정합니다.", Toast.LENGTH_SHORT).show()
                gg.restart()
            }
            listenerFlag = !listenerFlag //토글

            Log.d(Constants.TAG, "일시정지, flag: $listenerFlag")


        }

        root.findViewById<ProgressBar>(R.id.progressBar)?.max = 100

        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        // ...
        naverMap_ = naverMap
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_MOUNTAIN, true)
        //사용자 현재위치
        naverMap.locationSource = locationSource

        val uiSettings = naverMap.uiSettings
        //uiSettings.isLocationButtonEnabled = true
        uiSettings.isScaleBarEnabled = false
        uiSettings.isZoomControlEnabled = false
        uiSettings.isCompassEnabled = false

        val locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true
        naverMap.locationTrackingMode = LocationTrackingMode.Face //위치 추적 모드

        val path = PathOverlay() // 따라갈 경로 그리기

        drawFullCourse(gg,"/storage/emulated/0/gpxdata/문학산.gpx", path, naverMap, locationOverlay)
        //drawFullCourse(gg,"/storage/emulated/0/gpxdata/inha (1).gpx", path, naverMap, locationOverlay)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun drawFullCourse(gg: MyGPX, coursePath:String, path:PathOverlay, naverMap: NaverMap, locationOverlay:LocationOverlay){
        gg.read(coursePath) // 객체에 gpx 저장하기
        val course = gg.getWayPoints()

        val coords = mutableListOf( // 첫 점을 넣어야 되는데
            LatLng(course[0].latitude.toDouble(), course[0].longitude.toDouble())
        )
        //Log.d(Constants.TAG,"$coords")
        //locationOverlay.position = LatLng(course[0].latitude.toDouble(), course[0].longitude.toDouble())
        //naverMap.locationTrackingMode = LocationTrackingMode.Face

        course.forEach { track->
            val lat = track.latitude.toDouble()
            val lon = track.longitude.toDouble()
            coords.add(LatLng(lat, lon))
        }
        //path.coords = gg.getWayPoints()
        path.coords = coords
        path.map = naverMap
        path.width = 10
        path.color = Color.RED
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun recordCurrentCourse(gg: MyGPX, naverMap: NaverMap){
        var lastTime = LocalDateTime.now()
        naverMap.addOnLocationChangeListener { location ->
            //Log.d(Constants.TAG, "record함수 flag: $listenerFlag")
            if(timeflag) //처음에 리스트에 점 없을 때 시간 null 들어가는거 막으려고
                activity?.findViewById<TextView>(R.id.total_time_view2)?.text = gg.printInfo(gg.getGPX()).total_time

            if(listenerFlag){ // 일시정지 아닐 때만 동작하게
                val lat = location.latitude
                val lon = location.longitude
                val alt = location.altitude
                //GlobalScope.launch { // launch new coroutine in background and continue
                //delay(2000) // non-blocking delay for 1 second (default time unit is ms)
                var currentTime = LocalDateTime.now()
                if(Duration.between(lastTime, currentTime).seconds > 3){
                    gg.addWayPoint(lat, lon, alt)
                    timeflag=true
                    //coords.add(LatLng(lat, lon))
                    lastTime = currentTime
                    //Log.d(Constants.TAG,"현재: $lat $lon $alt")
                    //path2.coords = coords

                    var info = gg.printInfo(gg.getGPX())
                    Log.d(Constants.TAG,"$info")


                    activity?.findViewById<TextView>(R.id.moving_time_view)?.text = info.moving_time
                    activity?.findViewById<TextView>(R.id.moving_distance_view)?.text = info.moving_distance
                    activity?.findViewById<TextView>(R.id.left_distance_view)?.text = info.left_distance
                    activity?.findViewById<TextView>(R.id.cur_height_view)?.text = alt.toString().substring(0,4) +"m"
                    activity?.findViewById<TextView>(R.id.arrival_time_view)?.text = info.expected_time
                    activity?.findViewById<ProgressBar>(R.id.progressBar)?.progress = info.progress.toInt()
                    //activity?.findViewById<ProgressBar>(R.id.progressBar)?.progress = 50
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap_.locationTrackingMode = LocationTrackingMode.None
                Log.d(Constants.TAG, "요청3")
                //checkPermissions()
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

//    //퍼미션 체크 및 권한 요청 함수
//    @RequiresApi(Build.VERSION_CODES.R)
//    private fun checkPermissions() {
//        //거절되었거나 아직 수락하지 않은 권한(퍼미션)을 저장할 문자열 배열 리스트
//        var rejectedPermissionList = ArrayList<String>()
//
//        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
//        for(permission in requiredPermissions){
//            if(context?.let { ContextCompat.checkSelfPermission(it, permission) } != PackageManager.PERMISSION_GRANTED) {
//                //만약 권한이 없다면 rejectedPermissionList에 추가
//                rejectedPermissionList.add(permission)
//                Log.d(Constants.TAG, "요청1: $rejectedPermissionList")
//            }
//        }
//        //거절된 퍼미션이 있다면...
//        if(rejectedPermissionList.isNotEmpty()){
//            //권한 요청!
//            val array = arrayOfNulls<String>(rejectedPermissionList.size)
//            activity?.let { ActivityCompat.requestPermissions(it, rejectedPermissionList.toArray(array), multiplePermissionsCode)
//            Log.d(Constants.TAG, "요청2")
//            }
//        }
//    }

    private fun uploadGPX(path: String, filename: String) {
        object : Thread() {
            override fun run() {
                try {
                    val awsCredentials: AWSCredentials = BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY)
                    val s3Client = AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2))
                    try {
                        var s3Directory = "save_test/"
                        val file: File = File(path)
                        s3Client.putObject(S3_BUCKET, s3Directory+filename, file)

                    } catch (e: AmazonServiceException) {
                        System.err.println(e.errorMessage)
                    }
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }
}