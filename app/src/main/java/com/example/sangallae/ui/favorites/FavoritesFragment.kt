package com.example.sangallae.ui.favorites

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sangallae.R
import com.example.sangallae.retrofit.models.CourseItem
import com.example.sangallae.ui.detail.CourseDetailActivity
import com.example.sangallae.ui.home.RecommendedCourseAdapter
import com.example.sangallae.ui.search.CourseRecyclerViewAdapter
import com.example.sangallae.utils.Constants
import com.example.sangallae.utils.RESPONSE_STATUS
import com.example.sangallae.utils.Usage
import com.jeongdaeri.unsplash_app_tutorial.retrofit.RetrofitManager

class FavoritesFragment : Fragment() {

    private lateinit var favoritesRecyclerViewAdapter: FavoritesRecyclerViewAdapter
    private lateinit var favoritesViewModel: FavoritesViewModel
    private lateinit var fToolbar: androidx.appcompat.widget.Toolbar
    private var courseList = ArrayList<CourseItem>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        favoritesViewModel = ViewModelProvider(this).get(FavoritesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_favorites, container, false)

        fToolbar = root.findViewById(R.id.home_toolbar)
        (activity as AppCompatActivity).setSupportActionBar(fToolbar)

        setHasOptionsMenu(true)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favoritesRecyclerViewSetting(courseList)
        favoritesCourseApiCall()

        favoritesRecyclerViewAdapter.setOnItemClickListener(object : FavoritesRecyclerViewAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: Int, pos : Int) {
                Intent(activity, CourseDetailActivity::class.java).apply {
                    putExtra("id", data)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { startActivity(this) }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun favoritesRecyclerViewSetting(courseList: ArrayList<CourseItem>) {

        this.favoritesRecyclerViewAdapter = FavoritesRecyclerViewAdapter()

        this.favoritesRecyclerViewAdapter.submitList(courseList)

        requireView().findViewById<RecyclerView>(R.id.favorites_recycler_view).layoutManager =
            GridLayoutManager(
                requireContext(),
                1,
                GridLayoutManager.VERTICAL,
                false
            )
        requireView().findViewById<RecyclerView>(R.id.favorites_recycler_view).adapter = this.favoritesRecyclerViewAdapter
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

    private fun favoritesCourseApiCall() {
        val retrofit = RetrofitManager(Usage.ACCESS)
        retrofit.favoriteCourses(completion = { status, list ->
            when(status){
                RESPONSE_STATUS.OKAY -> {
                    Log.d(Constants.TAG, "SearchActivity - searchCourseApiCall() called 응답 성공 / list.size : ${list?.size}")

                    if (list != null){
                        this.courseList.clear()
                        this.courseList = list
                        this.favoritesRecyclerViewAdapter.submitList(this.courseList)
                        this.favoritesRecyclerViewAdapter.notifyDataSetChanged()
                    }
                }
                RESPONSE_STATUS.NO_CONTENT -> {
                    Toast.makeText(requireContext(), "찜 목록이 없습니다", Toast.LENGTH_SHORT).show()
                    view?.findViewById<View>(R.id.no_favorites_layout)?.visibility = View.VISIBLE
                }
                else -> {
                    Toast.makeText(requireContext(), "검색을 할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}