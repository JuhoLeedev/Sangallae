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


class MainMapFragment : Fragment(), View.OnClickListener, OnMapReadyCallback {

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
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
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
                    )
                ) {
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return
            }
        }
    }

    companion object {
        private const val READ_STORAGE_PERMISSIONS_REQUEST = 1001
        private const val WRITE_STORAGE_PERMISSIONS_REQUEST = 1002
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}


