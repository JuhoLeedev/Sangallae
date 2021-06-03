package com.example.sangallae.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Path
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.UiThread
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.sangallae.R
import com.example.sangallae.utils.Constants
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import io.jenetics.jpx.WayPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask

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

class MyMapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mapViewModel: MapViewModel
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    //퍼미션 응답 처리 코드
    private val multiplePermissionsCode = 100

//    //필요한 퍼미션 리스트
//    @RequiresApi(Build.VERSION_CODES.R)
//    private val requiredPermissions = arrayOf(
//        Manifest.permission.ACCESS_FINE_LOCATION,
//        Manifest.permission.MANAGE_EXTERNAL_STORAGE)

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)

//        //퍼미션 체크 -> 그냥 앱설정 들어가서 해야됨.
//        //checkPermissions()
//        val permission1 = Manifest.permission.ACCESS_FINE_LOCATION
//        val permissionResult1 = this.context?.let { ContextCompat.checkSelfPermission(it, permission1) }
//        when(permissionResult1){
//            PackageManager.PERMISSION_GRANTED -> {
//                //Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
//                Log.d(Constants.TAG,"됨")
//// Go Main Function
//            }
//            PackageManager.PERMISSION_DENIED -> {
//                //Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
//                this.activity?.let { ActivityCompat.requestPermissions(it, arrayOf(permission1), 100) }
//                Log.d(Constants.TAG,"거부됨")
//                }
//        }
//
//        val permission2 = Manifest.permission.READ_EXTERNAL_STORAGE
//        val permissionResult2 = this.context?.let { ContextCompat.checkSelfPermission(it, permission2) }
//        when(permissionResult2){
//            PackageManager.PERMISSION_GRANTED -> {
//                //Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
//                Log.d(Constants.TAG,"됨")
//// Go Main Function
//            }
//            PackageManager.PERMISSION_DENIED -> {
//                //Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
//                this.activity?.let { ActivityCompat.requestPermissions(it, arrayOf(permission1), 100) }
//                Log.d(Constants.TAG,"거부됨")
//            }
//        }

        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_map, container, false)
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



        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        // ...
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_MOUNTAIN, true)
        //사용자 현재위치
        naverMap.locationSource = locationSource

        val uiSettings = naverMap.uiSettings
        uiSettings.isLocationButtonEnabled = true
        uiSettings.isScaleBarEnabled = false
        uiSettings.isZoomControlEnabled = false

        val locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true
        naverMap.locationTrackingMode = LocationTrackingMode.Face //위치 추적 모드

        val path = PathOverlay() // 따라갈 경로 그리기
        val path2 = PathOverlay() // 현재 위치 그리기

        var gg = MyGPX()

        drawFullCourse(gg,"/storage/emulated/0/gpxdata/4_sample[2].gpx", path, naverMap, locationOverlay)
        drawCurrentCourse(gg, path2, naverMap)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun drawFullCourse(gg:MyGPX, coursePath:String, path:PathOverlay, naverMap: NaverMap, locationOverlay:LocationOverlay){
        val gpx = gg.read(coursePath)
        val course = gg.getWayPoints(gpx)

        val coords = mutableListOf( // 첫 점을 넣어야 되는데
            LatLng(course[0].latitude.toDouble(), course[0].longitude.toDouble())
        )
        Log.d(Constants.TAG,"$coords")

        //locationOverlay.position = LatLng(course[0].latitude.toDouble(), course[0].longitude.toDouble())
        //naverMap.locationTrackingMode = LocationTrackingMode.Face

        course.forEach { track->
            val lat = track.latitude.toDouble()
            val lon = track.longitude.toDouble()
            coords.add(LatLng(lat, lon))
        }
        path.coords = coords
        path.map = naverMap
        path.width = 10
        path.color = Color.RED
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun drawCurrentCourse(gg:MyGPX, path2:PathOverlay, naverMap: NaverMap){
        val coords = mutableListOf<LatLng>(
            LatLng(131.0,37.0),
            LatLng(131.0,37.0)
        )
        path2.coords = coords
        path2.map = naverMap
        path2.width = 10
        path2.color = Color.BLUE

        //GlobalScope.launch{
        // 사용자의 위치가 변경되면 그 좌표를 토스트로 표시
            naverMap.addOnLocationChangeListener { location ->
                val lat = location.latitude
                val lon = location.longitude
                val alt = location.altitude
                //GlobalScope.launch { // launch new coroutine in background and continue
                    //delay(2000) // non-blocking delay for 1 second (default time unit is ms)
                    gg.addWayPoint(lat, lon, alt)
                    coords.add(LatLng(lat, lon))

                    Log.d(Constants.TAG,"현재: $lat $lon $alt $coords")
                //}
                path2.coords = coords
            }
       // }
       // path2.coords = coords
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
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
}