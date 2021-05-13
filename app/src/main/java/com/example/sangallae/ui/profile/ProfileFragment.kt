package com.example.sangallae.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.LayoutInflater.from
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.sangallae.R
import com.example.sangallae.retrofit.models.Course
import com.example.sangallae.retrofit.models.Profile
import com.example.sangallae.ui.MainActivity
import com.example.sangallae.ui.SplashActivity
import com.example.sangallae.utils.Constants
import com.example.sangallae.utils.RESPONSE_STATUS
import com.example.sangallae.utils.Usage
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.jeongdaeri.unsplash_app_tutorial.retrofit.RetrofitManager
import com.kakao.sdk.user.UserApiClient
import com.nhn.android.naverlogin.OAuthLogin
import de.hdodenhof.circleimageview.CircleImageView

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

//        pToolbar = root.findViewById(R.id.toolbar)
//        (activity as AppCompatActivity).setSupportActionBar(pToolbar)
//        pToolbar.title=""
        //hToolbar?.elevation = 0f

        setHasOptionsMenu(true)


        // 페이지 열 때 유저 정보 요청
        profileLoadApiCall()

        // 로그아웃 팝업
        val logoutBtn = root.findViewById<ImageButton>(R.id.logoutBtn)
        logoutBtn.setOnClickListener {
            logout(logoutBtn)
        }

        // 프로필 수정 버튼
        val editBtn = root.findViewById<ImageButton>(R.id.editBtn)
        editBtn.setOnClickListener {
//            //뷰 바인딩
//            binding = ActivityMainBinding.inflate(layoutInflater)
//            setContentView(binding.root)
//
//            binding.startButton.setOnClickListener {

                // Dialog만들기
                val mDialogView = LayoutInflater.from(activity).inflate(R.layout.edit_profile_dialog, null)
                val mBuilder = activity?.let { it1 ->
                    AlertDialog.Builder(it1)
                        .setView(mDialogView)
                        //.setTitle("Login Form")
                }

                mBuilder?.show()
            //}
        }

        return root
    }
    private fun logout(view: View) {
        val popup = activity?.let { PopupMenu(it, view) }
        popup?.inflate(R.menu.profile_menu)

        popup?.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.logout -> {
                    UserApiClient.instance.logout { error ->
                        if (error != null) {
                            Log.e(Constants.TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                        }
                        else {
                            Log.i(Constants.TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                        }
                    }
                    val mOAuthLoginModule = OAuthLogin.getInstance()
                    mOAuthLoginModule.logout(activity)
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(this.context, item.title, Toast.LENGTH_SHORT).show()
                    val intent = Intent(activity, SplashActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent)
                }

            }
            true
        })
        popup?.show()
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
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
                        view?.findViewById<TextView>(R.id.total_distance_view)?.text = profileItem.total_distance
                        view?.findViewById<TextView>(R.id.avg_distance_view)?.text = profileItem.avg_distance
                        view?.findViewById<TextView>(R.id.total_time_view)?.text = profileItem.total_time
                        view?.findViewById<TextView>(R.id.avg_time_view)?.text = profileItem.avg_time
                        view?.findViewById<TextView>(R.id.total_height_view)?.text = profileItem.total_height
                        view?.findViewById<TextView>(R.id.max_height_view)?.text = profileItem.max_height
                        view?.findViewById<TextView>(R.id.avg_height_view)?.text = profileItem.avg_distance
                        view?.findViewById<TextView>(R.id.max_speed_view)?.text = profileItem.max_speed
                        view?.findViewById<TextView>(R.id.avg_speed_view)?.text = profileItem.avg_speed
                        view?.findViewById<TextView>(R.id.total_calories_view)?.text = profileItem.total_calories
                        view?.findViewById<TextView>(R.id.avg_calories_view)?.text = profileItem.avg_calories

                        view?.findViewById<TextView>(R.id.nickname)?.text = profileItem.nickname + getString(R.string.profile_name);
                        view?.findViewById<TextView>(R.id.height_weight)?.text = profileItem.user_height_weight

//                        Glide.with(this).load(profileItem.picture).into(view?.findViewById<CircleImageView>(R.id.imageView))
//
                        view?.findViewById<ImageView>(R.id.imageView)?.let {
                            Glide.with(this)
                                .load(profileItem.picture)
                                .circleCrop()
                                .into(it)
                        };
                    }
                }
                else -> {
                    Log.d(Constants.TAG, "ProfileFragment-OncreateView-profileLoadApiCall() profileItem: ${profileItem.toString()}")
                    Toast.makeText(this.context, "페이지를 로드할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        })

    }
}