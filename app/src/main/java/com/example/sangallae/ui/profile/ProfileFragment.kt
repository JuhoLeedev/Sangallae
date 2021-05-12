package com.example.sangallae.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sangallae.R
import com.example.sangallae.retrofit.models.Course
import com.example.sangallae.retrofit.models.Profile
import com.example.sangallae.utils.Constants
import com.example.sangallae.utils.RESPONSE_STATUS
import com.example.sangallae.utils.Usage
import com.jeongdaeri.unsplash_app_tutorial.retrofit.RetrofitManager

class ProfileFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var pToolbar: androidx.appcompat.widget.Toolbar
    private lateinit var profileItem: Profile

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
//        val textView: TextView = root.findViewById(R.id.text_)
//        profileViewModel.text.observe(viewLifecycleOwner, Observer { textView.text = it })

        pToolbar = root.findViewById(R.id.toolbar)
        (activity as AppCompatActivity).setSupportActionBar(pToolbar)
        pToolbar.title=""
        //hToolbar?.elevation = 0f

        setHasOptionsMenu(true)

        // 페이지 열 때 유저 정보 요청
        profileLoadApiCall()


        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.logout -> {

            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun profileLoadApiCall() {
        val retrofit = RetrofitManager(Usage.ACCESS)
        retrofit.profileLoad(completion = { status, profileItem ->
            when(status){
                RESPONSE_STATUS.OKAY -> {
                    //Log.d(Constants.TAG, "PhotoCollectionActivity - searchPhotoApiCall() called 응답 성공 / list.size : ${list?.size}")
                    if (profileItem != null){
                        //this.profileItem.clear()
                        this.profileItem = profileItem
                        Log.d(Constants.TAG, "ProfileFragment-OncreateView-profileLoadApiCall() profileItem: ${profileItem.toString()}")
//                        this.courseRecyeclerViewAdapter.submitList(this.profileList)
//                        this.courseRecyeclerViewAdapter.notifyDataSetChanged()
                    }
                }
//                RESPONSE_STATUS.NO_CONTENT -> {
//                    Toast.makeText(this, "$query 에 대한 검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
//                }
                else -> {
                    Log.d(Constants.TAG, "ProfileFragment-OncreateView-profileLoadApiCall() profileItem: ${profileItem.toString()}")
                    Toast.makeText(this.context, "페이지를 로드할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        })

    }
}