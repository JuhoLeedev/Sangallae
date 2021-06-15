package com.example.sangallae.ui.home

import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sangallae.R
import com.example.sangallae.retrofit.models.Home
import com.example.sangallae.retrofit.models.Mountain
import com.example.sangallae.ui.MainActivity
import com.example.sangallae.ui.detail.CourseDetailActivity
import com.example.sangallae.utils.*
import com.jeongdaeri.unsplash_app_tutorial.retrofit.RetrofitManager


class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var hToolbar: androidx.appcompat.widget.Toolbar
    //private var courseList = ArrayList<CourseItem>()
    private var recCourseList = ArrayList<Home>()
    private var hotCourseList = ArrayList<Home>()
    private var hotMtnList = ArrayList<Mountain>()
    private var nearMtnList = ArrayList<Mountain>()
    private lateinit var recommendedCourseAdapter: RecommendedCourseAdapter
    private lateinit var popularCourseAdapter: PopularCourseAdapter
    private lateinit var popularMountainAdapter: PopularMountainAdapter
    private lateinit var nearMountainAdapter: NearMountainAdapter
    lateinit var navController: NavController
    private lateinit var locationManager: LocationManager
    private lateinit var greetingTextView: TextView
    private var lat = 0.0
    private var lon = 0.0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        //val textView: TextView = root.findViewById(R.id.text_home)
        //homeViewModel.text.observe(viewLifecycleOwner, Observer { textView.text = it })

        hToolbar = root.findViewById(R.id.home_toolbar)
        (activity as AppCompatActivity).setSupportActionBar(hToolbar)
        //hToolbar?.elevation = 0f

        setHasOptionsMenu(true)

        greetingTextView = root.findViewById(R.id.home_greeting1)
        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        getLatLon()

        // 추천 화면 갱신
        //refreshHome()
        this.recommendedCourseAdapter = RecommendedCourseAdapter()
//        this.recommendedCourseAdapter.submitList(recCourseList)
//
        this.popularCourseAdapter = PopularCourseAdapter()
//        this.popularCourseAdapter.submitList(hotCourseList)

        this.popularMountainAdapter = PopularMountainAdapter()
//        this.popularMountainAdapter.submitList(hotMtnList)

        this.nearMountainAdapter = NearMountainAdapter()

        //root activity view context this.context 중에 root만 되네
        root.findViewById<RecyclerView>(R.id.recCourse)?.layoutManager =
            LinearLayoutManager(
                context, //activity?
                RecyclerView.HORIZONTAL,
                false
            )
        root.findViewById<RecyclerView>(R.id.recCourse)?.adapter =
            this.recommendedCourseAdapter

        root.findViewById<RecyclerView>(R.id.popCourse)?.layoutManager =
            LinearLayoutManager(
                context, //activity?
                RecyclerView.HORIZONTAL,
                false
            )
        root.findViewById<RecyclerView>(R.id.popCourse)?.adapter =
            this.popularCourseAdapter

        root.findViewById<RecyclerView>(R.id.popMtn)?.layoutManager =
            LinearLayoutManager(
                context, //activity?
                RecyclerView.HORIZONTAL,
                false
            )
        root.findViewById<RecyclerView>(R.id.popMtn)?.adapter =
            this.popularMountainAdapter

        root.findViewById<RecyclerView>(R.id.nearMtn)?.layoutManager =
            LinearLayoutManager(
                context, //activity?
                RecyclerView.HORIZONTAL,
                false
            )
        root.findViewById<RecyclerView>(R.id.nearMtn)?.adapter =
            this.nearMountainAdapter



        // 메인 추천 등산로 -> 등산로 상세
        recommendedCourseAdapter.setOnItemClickListener(object :
            RecommendedCourseAdapter.OnItemClickListener {
            override fun onItemClick(v: View, data: Int, pos: Int) {
                Intent(activity, CourseDetailActivity::class.java).apply {
                    putExtra("id", data)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { startActivity(this) }
            }
        })

        // 메인 인기 등산로 -> 등산로 상세
        popularCourseAdapter.setOnItemClickListener(object :
            PopularCourseAdapter.OnItemClickListener {
            override fun onItemClick(v: View, data: Int, pos: Int) {
                Intent(activity, CourseDetailActivity::class.java).apply {
                    putExtra("id", data)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { startActivity(this) }
            }
        })

        homeLoadApiCall()


        // 추천 등산로 더보기 버튼
        val recCourseMore = root.findViewById<TextView>(R.id.recCourseMore)
        recCourseMore.setOnClickListener {
            //목록 fragment로 넘어가기
            findNavController().navigate(R.id.action_navigation_home_to_navigation_rec_course)
        }

        // 인기 등산로 더보기 버튼
        val hotCourseMore = root.findViewById<TextView>(R.id.popCourseMore)
        hotCourseMore.setOnClickListener {
            //목록 fragment로 넘어가기
            findNavController().navigate(R.id.action_navigation_home_to_navigation_hot_course)
        }

        // 인기 산 더보기 버튼
        val hotMtnMore = root.findViewById<TextView>(R.id.popMtnMore)
        hotMtnMore.setOnClickListener {
            //목록 fragment로 넘어가기
            findNavController().navigate(R.id.action_navigation_home_to_navigation_hot_mountain)
        }


        Log.d("gps", "$lat, $lon")
        return root
    }
//
//    fun refreshHome(){
//
//    }

//    override fun onStart() {
//        homeLoadApiCall()
//        super.onStart()
//    }

    private fun homeLoadApiCall() {
        val retrofit = RetrofitManager(Usage.ACCESS)
        retrofit.homeLoad(
            lat = lat,
            lon = lon,
            completion = { status, nickName, list1, list2, list3, list4 ->
                when (status) {
                    RESPONSE_STATUS.OKAY -> {
                        greetingTextView.text = nickName + resources.getString(R.string.greeting1)
                        //Log.d(Constants.TAG, "PhotoCollectionActivity - searchPhotoApiCall() called 응답 성공 / list.size : ${list?.size}")
                        if (list1 != null) {
                            this.recCourseList.clear()
                            this.recCourseList = list1
                            recommendedCourseAdapter.submitList(this.recCourseList)
                            recommendedCourseAdapter.notifyDataSetChanged()
                            //Log.d(Constants.TAG,"여기까지 됨1 $recCourseList")
                        }
                        if (list2 != null) {
                            this.hotCourseList.clear()
                            this.hotCourseList = list2
                            popularCourseAdapter.submitList(this.hotCourseList)
                            popularCourseAdapter.notifyDataSetChanged()
                            //Log.d(Constants.TAG,"여기까지 됨1 $recCourseList")
                        }
                        if (list3 != null) {
                            this.hotMtnList.clear()
                            this.hotMtnList = list3
                            popularMountainAdapter.submitList(this.hotMtnList)
                            popularMountainAdapter.notifyDataSetChanged()
                            //Log.d(Constants.TAG,"여기까지 됨1 $recCourseList")
                        }
                        if (list4 != null) {
                            this.nearMtnList.clear()
                            this.nearMtnList = list4
                            nearMountainAdapter.submitList(this.nearMtnList)
                            nearMountainAdapter.notifyDataSetChanged()
                            if (list4.size == 0) {
                                val textView =
                                    view?.findViewById<TextView>(R.id.no_nearMtn_textview)
                                if (textView != null) {
                                    Log.d("야야", "여기는 null안")
                                    textView.visibility = View.VISIBLE
                                }
                            }
                            //Log.d(Constants.TAG,"여기까지 됨1 $recCourseList")
                        }
                    }
                    else -> {
                        Log.d(
                            Constants.TAG,
                            "ProfileFragment-OncreateView-homeLoadApiCall() ${list1.toString()}"
                        )
                        Toast.makeText(this.context, "페이지를 로드할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    private fun getLatLon(){
//        if(PermissionUtils.requestPermission(requireActivity(),
//                API.LOCATION_PERMISSION_REQUEST_CODE,
//                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)){
            try {
                Log.d("gps", "여기는 try")
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10000,  // 10-second interval.
                    10.0f,  // 10 meters
                    gpsListener
                )

                var location : Location? = locationManager
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                    Log.d(
                        "gps", "GPS Location changed, Latitude: $lat" +
                                ", Longitude: $lon"
                    )
                }
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                    Log.d(
                        "gps", "GPS Location changed, Latitude: $lat" +
                                ", Longitude: $lon"
                    )
                }
            } catch (e: SecurityException) {
                Toast.makeText(this.context, "GPS 권한이 없습니다", Toast.LENGTH_SHORT).show()
            }
 //       }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.main_menu_search -> {
            findNavController().navigate(R.id.action_navigation_home_to_searchActivity)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private var gpsListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            lat = location.latitude
            lon = location.longitude
            Log.d(
                "gps", "GPS Location changed, Latitude: $lat" +
                        ", Longitude: $lon"
            )
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        }
        override fun onProviderEnabled(provider: String) {
        }
        override fun onProviderDisabled(provider: String) {
        }
    }
    override fun onResume() {
        super.onResume()
        val activity = activity as MainActivity?
        activity?.hideUpButton()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            API.LOCATION_PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (PermissionUtils.permissionGranted(
                        requestCode,
                        API.LOCATION_PERMISSION_REQUEST_CODE, grantResults
                    )
                ) {
                    getLatLon()
                }
            }
        }
    }
}