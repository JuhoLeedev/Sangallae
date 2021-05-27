package com.example.sangallae.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sangallae.R
import com.example.sangallae.retrofit.models.CourseItem
import com.example.sangallae.retrofit.models.Home
import com.example.sangallae.retrofit.models.Mountain
import com.example.sangallae.ui.MainActivity
import com.example.sangallae.ui.detail.CourseDetailActivity
import com.example.sangallae.utils.Constants
import com.example.sangallae.utils.RESPONSE_STATUS
import com.example.sangallae.utils.Usage
import com.jeongdaeri.unsplash_app_tutorial.retrofit.RetrofitManager


class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var hToolbar: androidx.appcompat.widget.Toolbar
    //private var courseList = ArrayList<CourseItem>()
    private var recCourseList = ArrayList<Home>()
    private var hotCourseList = ArrayList<Home>()
    private var hotMtnList = ArrayList<Mountain>()
    private lateinit var recommendedCourseAdapter: RecommendedCourseAdapter
    private lateinit var popularCourseAdapter: PopularCourseAdapter
    private lateinit var popularMountainAdapter: PopularMountainAdapter
    lateinit var navController: NavController

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

        // 추천 화면 갱신
        //refreshHome()
        this.recommendedCourseAdapter = RecommendedCourseAdapter()
        this.recommendedCourseAdapter.submitList(recCourseList)
//
        this.popularCourseAdapter = PopularCourseAdapter()
        this.popularCourseAdapter.submitList(hotCourseList)

        this.popularMountainAdapter = PopularMountainAdapter()
        this.popularMountainAdapter.submitList(hotMtnList)

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

//        recommendedCourseAdapter.setOnItemClickListener(object : RecommendedCourseAdapter.OnItemClickListener{
//            override fun onItemClick(v: View, data: Int, pos : Int) {
//                Intent(activity, CourseDetailActivity::class.java).apply {
//                    putExtra("id", data)
//                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                }.run { startActivity(this) }
//            }
//        })
        //homeLoadApiCall()
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
        return root
    }
//
//    fun refreshHome(){
//
//    }

    private fun homeLoadApiCall() {
        val retrofit = RetrofitManager(Usage.ACCESS)
        retrofit.homeLoad(completion = { status, list1, list2, list3 ->
            when (status) {
                RESPONSE_STATUS.OKAY -> {
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
}