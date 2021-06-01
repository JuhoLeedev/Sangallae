package com.example.sangallae.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sangallae.R
import com.example.sangallae.retrofit.models.CourseItem
import com.example.sangallae.ui.detail.CourseDetailActivity
import com.example.sangallae.utils.Constants
import com.example.sangallae.utils.RESPONSE_STATUS
import com.example.sangallae.utils.Usage
import com.jeongdaeri.unsplash_app_tutorial.retrofit.RetrofitManager


class HotCourseList : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var hToolbar: androidx.appcompat.widget.Toolbar
    private var courseList = ArrayList<CourseItem>()
    private lateinit var hotCourseListAdapter: CourseViewAdapter
    //private lateinit var callback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.hot_course_list, container, false)
        //homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        //val textView: TextView = root.findViewById(R.id.text_home)
        //homeViewModel.text.observe(viewLifecycleOwner, Observer { textView.text = it })

//        hToolbar = root.findViewById(R.id.home_toolbar)
//        (activity as AppCompatActivity).setSupportActionBar(hToolbar)
        //hToolbar?.elevation = 0f

        setHasOptionsMenu(true)

        // 추천 화면 갱신
        //refreshHome()
        this.hotCourseListAdapter = CourseViewAdapter()
        this.hotCourseListAdapter.submitList(courseList)

        //root activity view context this.context 중에 root만 되네
        root.findViewById<RecyclerView>(R.id.hot_course_recycler_view)?.layoutManager =
            LinearLayoutManager(
                context, //activity?
                RecyclerView.VERTICAL,
                false
            )
        root.findViewById<RecyclerView>(R.id.hot_course_recycler_view)?.adapter =
            this.hotCourseListAdapter

        hotCourseListAdapter.setOnItemClickListener(object : CourseViewAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: Int, pos : Int) {
                Intent(activity, CourseDetailActivity::class.java).apply {
                    putExtra("id", data)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { startActivity(this) }
            }
        })

        hotCourseApiCall()

        return root
    }

    private fun hotCourseApiCall() {
        val retrofit = RetrofitManager(Usage.ACCESS)
        retrofit.hotCourseList(completion = { status, list ->
            when (status) {
                RESPONSE_STATUS.OKAY -> {
                    //Log.d(Constants.TAG, "PhotoCollectionActivity - searchPhotoApiCall() called 응답 성공 / list.size : ${list?.size}")
                    if (list != null) {
                        this.courseList.clear()
                        this.courseList = list
                        hotCourseListAdapter.submitList(this.courseList)
                        hotCourseListAdapter.notifyDataSetChanged()
                        //Log.d(Constants.TAG,"여기까지 됨 $courseList")
                        //
//                        popularCourseAdapter.submitList(this.recCourseList)
//                        popularCourseAdapter.notifyDataSetChanged()
                    }
                }
                else -> {
                    Log.d(
                        Constants.TAG,
                        "ProfileFragment-OncreateView-hotListApiCall() ${list.toString()}"
                    )
                    Toast.makeText(this.context, "페이지를 로드할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        })
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
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        callback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                sample_text.text = "occur back pressed event!!"
//            }
//        }
//        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        callback.remove()
//    }
}