package com.example.sangallae.ui.map

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sangallae.R
import com.example.sangallae.utils.Constants
import com.example.sangallae.utils.PermissionUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource


class MyMapFragment : Fragment(), View.OnClickListener, OnMapReadyCallback {

    private lateinit var mapViewModel: MapViewModel
    private lateinit var fab: FloatingActionButton
    private lateinit var fab1: FloatingActionButton
    private lateinit var fab2: FloatingActionButton
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private var isFabOpen = false
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.to_bottom_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.from_bottom_anim
        )
    }
    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_close_anim
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_map, container, false)
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.mapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.mapView, it).commit()
            }
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        mapFragment.getMapAsync(this)

        fab = root.findViewById(R.id.map_fab)
        fab1 = root.findViewById(R.id.map_fab1)
        fab2 = root.findViewById(R.id.map_fab2)
        fab.setOnClickListener(this)
        fab1.setOnClickListener(this)
        fab2.setOnClickListener(this)
        return root
    }


    override fun onClick(v: View) {
        val id = v.id
        when (id) {
            R.id.map_fab -> anim()
            R.id.map_fab1 -> {
                anim()
            }
            R.id.map_fab2 -> {
                anim()
            }
        }
    }

    private fun anim() {
        if (isFabOpen) {
            fab1.startAnimation(toBottom)
            fab2.startAnimation(toBottom)
            fab.startAnimation(rotateClose)
            fab.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.sky_blue));
            fab.supportImageTintList = ColorStateList.valueOf(resources.getColor(R.color.white));
            fab1.isClickable = false
            fab2.isClickable = false
            isFabOpen = false
        } else {
            fab1.startAnimation(fromBottom)
            fab2.startAnimation(fromBottom)
            fab.startAnimation(rotateOpen)
            fab.setBackgroundResource(R.color.sky_blue)
            fab.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.white));
            fab.supportImageTintList = ColorStateList.valueOf(resources.getColor(R.color.sky_blue));
            fab1.isClickable = true
            fab2.isClickable = true
            isFabOpen = true
        }
    }

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

        @RequiresApi(Build.VERSION_CODES.O)
        @UiThread
        override fun onMapReady(naverMap: NaverMap) {
            // ...
            this.naverMap = naverMap
            naverMap.locationSource = locationSource
            val uiSettings = naverMap.uiSettings

            naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_MOUNTAIN, true)
            naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_MOUNTAIN, true)
            //사용자 현재위치
            uiSettings.isLocationButtonEnabled = true
            naverMap.locationSource = locationSource
            uiSettings.isScaleBarEnabled = false
            uiSettings.isZoomControlEnabled = false

            val locationOverlay = naverMap.locationOverlay
            locationOverlay.isVisible = true
            naverMap.locationTrackingMode = LocationTrackingMode.Face //위치 추적 모드

            val path = PathOverlay() // 따라갈 경로 그리기
            val path2 = PathOverlay() // 현재 위치 그리기

            var gg = MyGPX()
//
            if (PermissionUtils.requestPermission(
                    requireActivity(),
                    READ_STORAGE_PERMISSIONS_REQUEST,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA
                ) && PermissionUtils.requestPermission(
                    requireActivity(),
                    MANAGE_STORAGE_PERMISSIONS_REQUEST,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA
                )
            ) {
                drawFullCourse(
                gg,
                "/storage/emulated/0/gpxdata/4_sample[2].gpx",
                path,
                naverMap,
                locationOverlay
            )
            }

            drawCurrentCourse(gg, path2, naverMap)
        }

        @RequiresApi(Build.VERSION_CODES.N)
        fun drawFullCourse(
            gg: MyGPX,
            coursePath: String,
            path: PathOverlay,
            naverMap: NaverMap,
            locationOverlay: LocationOverlay
        ) {
            val gpx = gg.read(coursePath)
            val course = gg.getWayPoints(gpx)

            val coords = mutableListOf( // 첫 점을 넣어야 되는데
                LatLng(course[0].latitude.toDouble(), course[0].longitude.toDouble())
            )
            Log.d(Constants.TAG, "$coords")

            //locationOverlay.position = LatLng(course[0].latitude.toDouble(), course[0].longitude.toDouble())
            //naverMap.locationTrackingMode = LocationTrackingMode.Face

            course.forEach { track ->
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
        fun drawCurrentCourse(gg: MyGPX, path2: PathOverlay, naverMap: NaverMap) {
            val coords = mutableListOf<LatLng>(
                LatLng(131.0, 37.0),
                LatLng(131.0, 37.0)
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

                Log.d(Constants.TAG, "현재: $lat $lon $alt $coords")
                //}
                path2.coords = coords
            }
            // }
            // path2.coords = coords
        }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (locationSource.onRequestPermissionsResult(
                        requestCode, permissions,
                        grantResults
                    )
                ) {
                    if (!locationSource.isActivated) { // 권한 거부됨
                        naverMap.locationTrackingMode = LocationTrackingMode.None
                    }
                    return
                }
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
            READ_STORAGE_PERMISSIONS_REQUEST -> {
                // If request is cancelled, the result arrays are empty.
                if (PermissionUtils.permissionGranted(
                        requestCode,
                        READ_STORAGE_PERMISSIONS_REQUEST,
                        grantResults
                    )) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return
            }
            MANAGE_STORAGE_PERMISSIONS_REQUEST -> {
                if (PermissionUtils.permissionGranted(
                        requestCode,
                        MANAGE_STORAGE_PERMISSIONS_REQUEST,
                        grantResults
                    )) {

                } else {
                }
                return
            }

            else -> {
            }
        }
    }

        companion object {
            private const val READ_STORAGE_PERMISSIONS_REQUEST = 1001
            private const val MANAGE_STORAGE_PERMISSIONS_REQUEST = 1002
            private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        }
}


