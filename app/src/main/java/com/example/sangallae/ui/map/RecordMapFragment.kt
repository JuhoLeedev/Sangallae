package com.example.sangallae.ui.map

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.example.sangallae.R
import com.example.sangallae.retrofit.models.Record
import com.example.sangallae.utils.API.LOCATION_PERMISSION_REQUEST_CODE
import com.example.sangallae.utils.API.S3_THUMBNAILURL
import com.example.sangallae.utils.API.S3_UPLOADURL
import com.example.sangallae.utils.Constants
import com.example.sangallae.utils.RESPONSE_STATUS
import com.example.sangallae.utils.S3FileManager
import com.example.sangallae.utils.Usage
import com.jeongdaeri.unsplash_app_tutorial.retrofit.RetrofitManager
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.widget.LocationButtonView
import java.lang.Math.round
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

class RecordMapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mapViewModel: MapViewModel
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap_: NaverMap
    private var fileName: String? = null
    @RequiresApi(Build.VERSION_CODES.N)
    var gg = MyGPX()
    var listenerFlag = false //위치 변경에 대한 리스너를 설정할 것인지 (일시정지/시작에 사용)
    lateinit var locationListener:NaverMap.OnLocationChangeListener
    //퍼미션 응답 처리 코드
    private val multiplePermissionsCode = 100
    //var timeflag = false
    var startFlag = true
    var stopFlag = false

    var timerTask: Timer? = null

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
            val saveName = "저장테스트_" + LocalDateTime.now().toString() + ".gpx"
            val uploadName = "업로드테스트_" + LocalDateTime.now().toString() + ".gpx"

            // 1. 휴대폰에 저장
            gg.saveGPX("/storage/emulated/0/gpxdata/" + saveName)
            Log.d(Constants.TAG, "저장완료~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")

            // 2. S3에 업로드
            S3FileManager().uploadGPX("/storage/emulated/0/gpxdata/" + saveName, uploadName)
            Log.d(Constants.TAG, "업로드완료~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
            Toast.makeText(this.context,"기록이 저장되었습니다.", Toast.LENGTH_SHORT).show()


            //naverMap_.removeOnLocationChangeListener(locationListener) //리스너 해제
            listenerFlag = false
            stopFlag = true

            // 3. DB에 삽입
            val now = LocalDateTime.now()
            val title = "Track_"+String.format("%04d-%02d-%02d %02d:%02d:%02d", now.year, now.monthValue, now.dayOfMonth, now.hour, now.minute, now.second)
            val record = Record(
                course = 1,                             // courseId 받아오는 법을 모르겠음
                title = title,
                filename = uploadName,
                distance = round(gg.movingDistance*100) / 100.0,
                moving_time_sec = gg.movingTimeSec,
                total_time_sec = gg.totalTimeSec,
                moving_time_str = gg.convertSecondToTime(gg.movingTimeSec),
                total_time_str = gg.convertSecondToTime(gg.totalTimeSec),
                avg_speed = round(gg.speed*100)/100.0,
                avg_pace = round(60.0/gg.speed*100)/100.0,
                location = "",                          // 주소 어떻게 하지?
                latitude = gg.getWayPoints()[0].latitude.toDouble(),
                longitude = gg.getWayPoints()[0].longitude.toDouble(),
                max_height = gg.maxEle.toInt(),
                min_height = gg.minEle.toInt(),
                ele_dif = (gg.maxEle.toInt() - gg.minEle.toInt()),
                total_uphill = gg.upHill.toInt(),
                total_downhill =gg.downHill.toInt(),
                difficulty = "중",                       // 난이도 일단 "중"으로
                calorie = 100,                          // 칼로리 일단 100으로
                date = gg.getStartTime().toString(),
                gpx_url = S3_UPLOADURL + uploadName,
                thumbnail = S3_THUMBNAILURL
            )

            uploadRecordApiCall(record)

        }

        //안내 중단
        val btn = root.findViewById<Button>(R.id.button)
        btn.setOnClickListener {
            Toast.makeText(this.context,"안내를 중단합니다.", Toast.LENGTH_SHORT).show()
            stopFlag = true
            //naverMap_.removeOnLocationChangeListener(locationListener) //리스너 해제
            listenerFlag = false
            stopFlag = true
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

        //시작/일시정지 버튼
        val pauseBtn = root.findViewById<ImageButton>(R.id.pauseBtn)
        pauseBtn.setOnClickListener {
            if(startFlag){ //처음 시작할 때
                // startFlag = false
                Toast.makeText(this.context,"측정을 시작합니다.", Toast.LENGTH_SHORT).show()
                pauseBtn.setImageResource(R.drawable.ic_twotone_pause_circle_24)
                timeUpdate()
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

            //Log.d(Constants.TAG, "일시정지, flag: $listenerFlag")
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
        //naverMap.locationTrackingMode = LocationTrackingMode.Face //위치 추적 모드

        val path = PathOverlay() // 따라갈 경로 그리기

//        fileName = arguments?.getString("name")
//        val dir = GPX_DIR + fileName
//        if(fileName != null) {
//            drawFullCourse(
//                gg,
//                dir,
//                path,
//                naverMap,
//                locationOverlay
//            )
//        }
        val fileName by lazy { requireArguments().getString("name") }


        var filePath = "/storage/emulated/0/gpxdata/"
        Log.d(Constants.TAG, "경로 + 파일명?: " +filePath+fileName)
        drawFullCourse(gg,filePath+fileName, path, naverMap, locationOverlay)
//        drawFullCourse(gg,"/storage/emulated/0/gpxdata/해오름동산.gpx", path, naverMap, locationOverlay)
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

        var minLat = 999.0
        var minLon = 999.0
        var maxLat = -1.0
        var maxLon = -1.0
        course.forEach { track->
            val lat = track.latitude.toDouble()
            val lon = track.longitude.toDouble()
            coords.add(LatLng(lat, lon))
            if(lat > maxLat)
                maxLat = lat
            if(lon > maxLon)
                maxLon = lon
            if(lat < minLat)
                minLat = lat
            if(lon < minLon)
                minLon = lon
        }
        //path.coords = gg.getWayPoints()
        val cameraUpdate = CameraUpdate.scrollTo(LatLng((minLat + maxLat)/2, (minLon + maxLon)/2))
        naverMap.moveCamera(cameraUpdate)
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
//            if(timeflag and !stopFlag) //처음에 리스트에 점 없을 때 시간 null 들어가는거 막으려고, 중단했을때 전체시간 증가x
//                activity?.findViewById<TextView>(R.id.total_time_view2)?.text = gg.printInfo(gg.getGPX()).total_time

            if(listenerFlag){ // 일시정지 아닐 때만 동작하게
                val lat = location.latitude
                val lon = location.longitude
                val alt = location.altitude
                var currentTime = LocalDateTime.now()
                if(startFlag){ // 처음 시작했을 때 -> 3초 기다리지 않고 바로 점 입력
                    startFlag = false
                    gg.addWayPoint(lat, lon, alt)
                    //Log.d(Constants.TAG,"시작함.")
                }
                else if(Duration.between(lastTime, currentTime).seconds > 3){
                    gg.addWayPoint(lat, lon, alt)
                    //timeflag = true
                    lastTime = currentTime

                    //var info = gg.printInfo(gg.getGPX())
                    //Log.d(Constants.TAG,"$info")

                    //activity?.findViewById<TextView>(R.id.moving_time_view)?.text = info.moving_time
                    //activity?.findViewById<TextView>(R.id.moving_distance_view)?.text = info.moving_distance
                    //activity?.findViewById<TextView>(R.id.left_distance_view)?.text = info.left_distance
                    activity?.findViewById<TextView>(R.id.cur_height_view)?.text = alt.toString().substring(0,4) +"m"
                    //activity?.findViewById<TextView>(R.id.arrival_time_view)?.text = info.expected_time
                    //a/ctivity?.findViewById<ProgressBar>(R.id.progressBar)?.progress = info.progress.toInt()
                    //activity?.findViewById<ProgressBar>(R.id.progressBar)?.progress = 50
                    Log.d(Constants.TAG, "uphill: ${gg.upHill}, downhill: ${gg.downHill}")
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
                //Log.d(Constants.TAG, "요청3")
                //checkPermissions()
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun timeUpdate(){
        timerTask = kotlin.concurrent.timer(period = 1000) {
            var info: RecordInfo? = null
            if (!startFlag and !stopFlag) { //처음에 리스트에 점 없을 때 시간 null 들어가는거 막으려고, 중단했을때 전체시간 증가x
                info = gg.printInfo(gg.getGPX())
            }
            runOnUiThread {
                if (!startFlag and !stopFlag) {
                    activity?.findViewById<TextView>(R.id.total_time_view2)?.text = info?.total_time
                    if(listenerFlag) {
                        activity?.findViewById<TextView>(R.id.moving_time_view)?.text =
                            info?.moving_time
                        activity?.findViewById<TextView>(R.id.moving_distance_view)?.text =
                            info?.moving_distance
                        activity?.findViewById<TextView>(R.id.left_distance_view)?.text =
                            info?.left_distance
                        //activity?.findViewById<TextView>(R.id.cur_height_view)?.text = alt.toString().substring(0,4) +"m"
                        activity?.findViewById<TextView>(R.id.arrival_time_view)?.text =
                            info?.expected_time
                        activity?.findViewById<ProgressBar>(R.id.progressBar)?.progress =
                            info?.progress?.toInt()!!
                    }
                }
            }
        }
    }

    private fun uploadRecordApiCall(record : Record) {
        val retrofit = RetrofitManager(Usage.ACCESS)
        retrofit.uploadRecord(record, completion = { status ->
            Log.d(Constants.TAG, "status는?"+status)
            when (status) {
                RESPONSE_STATUS.OKAY -> {
                    Log.d(Constants.TAG, "DB삽입완료~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
//                    Toast.makeText(this, "찜 목록에 추가했습니다.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Log.d(Constants.TAG, "DB삽입실패~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
//                    Toast.makeText(this, "찜 목록을 업데이트 할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}