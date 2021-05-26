package com.example.sangallae.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sangallae.R
import com.example.sangallae.retrofit.models.CourseItem
import com.example.sangallae.retrofit.models.Home
import com.example.sangallae.retrofit.models.NewProfile
import com.example.sangallae.ui.detail.CourseDetailActivity
import com.example.sangallae.ui.search.CourseRecyclerViewAdapter
import com.example.sangallae.ui.home.RecommendedCourseAdapter
import com.example.sangallae.utils.Constants
import com.example.sangallae.utils.RESPONSE_STATUS
import com.example.sangallae.utils.Usage
import com.jeongdaeri.unsplash_app_tutorial.retrofit.RetrofitManager


class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var hToolbar: androidx.appcompat.widget.Toolbar
    private var courseList = ArrayList<CourseItem>()
    private var recCourseList = ArrayList<Home>()
    private lateinit var recommendedCourseAdapter: RecommendedCourseAdapter
    private lateinit var popularCourseAdapter: PopularCourseAdapter


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
//        this.popularCourseAdapter = PopularCourseAdapter()
//        this.popularCourseAdapter.submitList(courseList)

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
            this.recommendedCourseAdapter

        recommendedCourseAdapter.setOnItemClickListener(object : RecommendedCourseAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: Int, pos : Int) {
                Intent(activity, CourseDetailActivity::class.java).apply {
                    putExtra("id", data)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { startActivity(this) }
            }
        })
        //homeLoadApiCall()
        homeLoadApiCall()

        // 추천 등산로 더보기 버튼
        val recCourseMore = root.findViewById<ImageButton>(R.id.recCourseMore)
        recCourseMore.setOnClickListener {


        }

        return root
    }

    fun refreshHome(){

    }

    private fun homeLoadApiCall() {
        val retrofit = RetrofitManager(Usage.ACCESS)
        retrofit.homeLoad(completion = { status, list ->
            when (status) {
                RESPONSE_STATUS.OKAY -> {
                    //Log.d(Constants.TAG, "PhotoCollectionActivity - searchPhotoApiCall() called 응답 성공 / list.size : ${list?.size}")
                    if (list != null) {
                        this.recCourseList.clear()
                        this.recCourseList = list
                        recommendedCourseAdapter.submitList(this.recCourseList)
                        recommendedCourseAdapter.notifyDataSetChanged()
//
//                        popularCourseAdapter.submitList(this.recCourseList)
//                        popularCourseAdapter.notifyDataSetChanged()
                    }
                }
                else -> {
                    Log.d(
                        Constants.TAG,
                        "ProfileFragment-OncreateView-homeLoadApiCall() ${list.toString()}"
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