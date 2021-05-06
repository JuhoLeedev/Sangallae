package com.example.sangallae.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sangallae.R
import com.example.sangallae.models.Course
import com.example.sangallae.models.SearchQuery
import com.example.sangallae.models.SearchResult
import com.example.sangallae.retrofit.*
import com.example.sangallae.ui.recyclerview.CourseRecyclerViewAdapter
import com.example.sangallae.utils.API
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private var courseList = ArrayList<Course>()
    private lateinit var courseRecyeclerViewAdapter: CourseRecyclerViewAdapter

    private lateinit var mySearchViewEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setSupportActionBar(findViewById(R.id.search_toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        if (Intent.ACTION_SEARCH == intent.action) {
//            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
//                Log.d("query", query)
//            }
//        }
     //   courseList = intent.getBundleExtra("array_bundle")?.getSerializable("corse_array_list") as ArrayList<Course>

        this.courseCollectionRecyclerViewSetting(this.courseList)

    }

    private fun courseCollectionRecyclerViewSetting(courseList: ArrayList<Course>) {
        //Log.d(TAG, "PhotoCollectionActivity - photoCollecitonRecyclerViewSetting() called")

        this.courseRecyeclerViewAdapter = CourseRecyclerViewAdapter()

        this.courseRecyeclerViewAdapter.submitList(courseList)

        this.findViewById<RecyclerView>(R.id.search_course_recycler_view).layoutManager =
            GridLayoutManager(
                this,
                1,
                GridLayoutManager.VERTICAL,
                false
            )
        this.findViewById<RecyclerView>(R.id.search_course_recycler_view).adapter =
            this.courseRecyeclerViewAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.app_bar_search).actionView as SearchView).apply {
            // Assumes current activity is the searchable activity
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            isFocusable = true
            isIconified = false
            requestFocusFromTouch()
            this.queryHint = getString(R.string.search_hint)

            setOnQueryTextListener(this@SearchActivity)


        }

        return true
    }


    override fun onQueryTextSubmit(query: String?): Boolean {

        Log.d("query", "SearchActivity - onQueryTextSubmit() called / query: $query")


        if (!query.isNullOrEmpty()) {
            this.findViewById<androidx.appcompat.widget.Toolbar>(R.id.search_toolbar).title = query

            //TODO:: api 호출
            //TODO:: 검색어 저장
            var retrofit = Retrofit.Builder()
                .baseUrl(API.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            var searchPost: SearchPost = retrofit.create(SearchPost::class.java)



            searchPost.searchCourses(query, "")
                .enqueue(object : retrofit2.Callback<SearchResult> {
                    override fun onFailure(call: Call<SearchResult>, t: Throwable) {
                        //실패시
                        Log.e("LoginResult", "Retrofit2 response error")
                        Toast.makeText(
                            baseContext,
                            "정보 요청에 실패했습니다. 잠시 후 다시 시도해주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onResponse(
                        call: Call<SearchResult>,
                        response: Response<SearchResult>
                    ) {
                        var searchResult = response.body()?.data
                        if (searchResult != null) {
                            courseList.clear()
                            courseList = searchResult
                            courseRecyeclerViewAdapter.submitList(courseList)
                            courseRecyeclerViewAdapter.notifyDataSetChanged()

                        }

                    }

//            this.searchPhotoApiCall(query)
//        }

//        this.mySearchView.setQuery("", false)
//        this.mySearchView.clearFocus()


                })

        }
        this.findViewById<androidx.appcompat.widget.Toolbar>(R.id.search_toolbar)
            .collapseActionView()

        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val userInputText = newText.let {
            it
        } ?: ""

        return true
    }
}


