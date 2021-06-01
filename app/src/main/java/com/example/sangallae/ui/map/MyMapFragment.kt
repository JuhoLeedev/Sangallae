package com.example.sangallae.ui.map

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.sangallae.R
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.PathOverlay

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
        val path = PathOverlay()
        val coords = mutableListOf( // 변경 가능한
            LatLng(37.57152, 126.97714),
            LatLng(37.56607, 126.98268),
            LatLng(37.56445, 126.97707),
            LatLng(37.55855, 126.97822)
        )
        path.coords = coords
        coords[0] = LatLng(37.5734571, 126.975335)
        // 아직 반영되지 않음
        path.coords = coords
        // 반영됨
        path.map = naverMap

        path.width = 10

        path.progress = 0.5

        path.color = Color.RED
        path.passedColor = Color.BLUE
    }

    fun drawFullCourse(){

    }
    fun drawCurrentCourse(){

    }
}