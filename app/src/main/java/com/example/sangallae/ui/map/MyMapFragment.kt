package com.example.sangallae.ui.map

import android.graphics.Color
import android.graphics.Path
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.sangallae.R
import com.example.sangallae.utils.Constants
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.PathOverlay
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
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
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

        return root
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        // ...
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_MOUNTAIN, true)

        // 그리기
        val path = PathOverlay() // 따라갈 경로 그리기
        val path2 = PathOverlay() // 현재 위치 그리기
        val coords = mutableListOf( // 변경 가능한
            LatLng(37.57152, 126.97714),
            LatLng(37.56607, 126.98268)
//            LatLng(37.56445, 126.97707),
//            LatLng(37.55855, 126.97822)
        )
        path.coords = coords

        path.map = naverMap
        path.width = 10
        path.color = Color.RED
        //path.passedColor = Color.BLUE

        var i = 0
        var a: List<LatLng>  =  listOf(
            LatLng(37.56445, 126.97707),
            LatLng(37.55855, 126.97822))

        val recCourseMore = activity?.findViewById<TextView>(R.id.button)
        recCourseMore?.setOnClickListener {
            coords.add(LatLng(a[i].latitude,a[i++].longitude))
            path.coords = coords
        }


        drawFullCourse(courseId)
        drawCurrentCourse(path2, naverMap)
    }

    fun drawFullCourse(courseId:Int, path:PathOverlay, naverMap: NaverMap){
        val course:List<WayPoint> = getWayPoints(courseId)

        val coords = mutableListOf( // 첫 점을 넣어야 되는데
            LatLng(course[0].latitude, course[0].longitude)
        )

        course.forEach { track->
            val lat = track.latitude
            val lon = track.longitude
            coords.add(LatLng(lat, lon))
        }

        path.coords = coords
        path.map = naverMap
        path.width = 10
        path.color = Color.RED
    }

    fun drawCurrentCourse(path2:PathOverlay, naverMap: NaverMap){
        //var num = 0.1
        var course:List<WayPoint> = 함수이름()
        val coords = mutableListOf( // 변경 가능한
            LatLng(37.57152, 126.97714)
        )
        path2.coords = coords
        path2.map = naverMap
        path2.width = 10
        path2.color = Color.BLUE

        while(true) {
            course = 함수이름()

            val time = course.time
            val height = course.height
            val lat = course.latitude
            val lon = course.longitude

            //경로 띄우기
            coords.add(LatLng(lat, lon))
            path2.coords = coords

            //정보 띄우기





//                while(num < 1){
//            Executors.newSingleThreadScheduledExecutor().schedule({
//            }, 2, TimeUnit.SECONDS)
//        }
            //이게 실시간으로 그려지느냐가 문제..
            coords.add(LatLng(37.56445, 126.97707))
            //coords.add(LatLng(37.55855, 126.97822))
            //coords[2] = LatLng(37.56445, 126.97707)
            path2.coords = coords
//        while(num < 1){
//            path.progress = num
//            Timer().schedule(timerTask {
//                num += 0.1
//                Toast.makeText(context,"$num", Toast.LENGTH_SHORT).show()
//            }, 1000)
//        }

//        while(num < 1){
//            Executors.newSingleThreadScheduledExecutor().schedule({
//                coords[2] = LatLng(37.56445, 126.97707)
//                Log.d(Constants.TAG, "진척률: $num")
//            }, 2, TimeUnit.SECONDS)
//        }
        }
    }
}