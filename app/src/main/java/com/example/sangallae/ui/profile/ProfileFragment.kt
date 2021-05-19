package com.example.sangallae.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.LayoutInflater.from
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.sangallae.R
import com.example.sangallae.retrofit.models.Course
import com.example.sangallae.retrofit.models.NewProfile
import com.example.sangallae.retrofit.models.Profile
import com.example.sangallae.ui.MainActivity
import com.example.sangallae.ui.SplashActivity
import com.example.sangallae.utils.Constants
import com.example.sangallae.utils.RESPONSE_STATUS
import com.example.sangallae.utils.Usage
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.jeongdaeri.unsplash_app_tutorial.retrofit.RetrofitManager
import com.kakao.sdk.auth.TokenManager
import com.kakao.sdk.user.UserApiClient
import com.nhn.android.naverlogin.OAuthLogin
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var pToolbar: androidx.appcompat.widget.Toolbar
    private lateinit var profileItem: Profile
    private var nick:String = ""
    private var hei:String = ""
    private var wei:String = ""
    private var pic:String = ""


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
            // Dialog 띄우기
            val mDialogView = from(activity).inflate(R.layout.edit_profile_dialog, null)
            val mBuilder = activity?.let { it1 ->
                AlertDialog.Builder(it1)
                    .setView(mDialogView)
                //.setTitle("Login Form")
            }

            mDialogView.findViewById<EditText>(R.id.editNickname).setText(nick)
            mDialogView.findViewById<EditText>(R.id.editHeight).setText(hei)
            mDialogView.findViewById<EditText>(R.id.editWeight).setText(wei)

            if(pic != "no_image"){
                mDialogView.findViewById<ImageView>(R.id.popImageView)?.let {
                    Glide.with(this)
                        .load(pic)
                        .circleCrop()
                        .into(it)
                }
            }

            val mAlertDialog = mBuilder?.show()
            //확인 버튼
            val okButton = mDialogView.findViewById<Button>(R.id.successButton)
            okButton.setOnClickListener {
                // 유저가 입력한 내용 받아오기
                nick = mDialogView.findViewById<EditText>(R.id.editNickname).text.toString()
                hei = mDialogView.findViewById<EditText>(R.id.editHeight).text.toString()
                wei = mDialogView.findViewById<EditText>(R.id.editWeight).text.toString()


                // 서버에 업데이트 요청
                profileUpdateApiCall(nick, hei, wei)

                // 화면 업데이트
                view?.findViewById<TextView>(R.id.nickname)?.text = nick + getString(R.string.profile_name)
                view?.findViewById<TextView>(R.id.height_weight)?.text = hei + "cm / " + wei + "kg"

                // 팝업 닫기
                mAlertDialog?.dismiss()
            }
            //취소 버튼
            val cancelButton = mDialogView.findViewById<Button>(R.id.cancelBtn)
            cancelButton.setOnClickListener {
                mAlertDialog?.dismiss()
            }
        }

        return root
    }
    private fun logout(view: View) {
        val popup = activity?.let { PopupMenu(it, view) }
        popup?.inflate(R.menu.profile_menu)

        popup?.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.logout -> {
                    //카카오
                    Log.d(Constants.TAG,TokenManager.instance.getToken().toString())

                    UserApiClient.instance.logout { error ->
                        if (error != null) {
                            Log.e(Constants.TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                        }
                        else {
                            Log.i(Constants.TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                        }
                    }
                    if(OAuthLogin.getInstance()!=null){ //네이버
                        OAuthLogin.getInstance().logout(activity)
                    }
                    FirebaseAuth.getInstance().signOut() //구글

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

                        view?.findViewById<TextView>(R.id.nickname)?.text = profileItem.nickname + getString(R.string.profile_name)
                        //view?.findViewById<TextView>(R.id.height_weight)?.text = profileItem.user_height_weight
                        view?.findViewById<TextView>(R.id.height_weight)?.text = profileItem.user_height + "cm / "+profileItem.user_weight + "kg"

                        nick = profileItem.nickname
                        wei = profileItem.user_weight
                        hei =  profileItem.user_height
                        pic = profileItem.picture

//                        view?.findViewById<CircleImageView>(R.id.imageView)?.let {
//                            Glide.with(this).load(profileItem.picture).into(
//                                it
//                            )
//                        }

                        if(profileItem.picture != "no_image"){
                            view?.findViewById<ImageView>(R.id.imageView)?.let {
                                Glide.with(this)
                                    .load(profileItem.picture)
                                    .circleCrop()
                                    .into(it)
                            }
                        }
                    }
                }
                else -> {
                    Log.d(Constants.TAG, "ProfileFragment-OncreateView-profileLoadApiCall() profileItem: ${profileItem.toString()}")
                    Toast.makeText(this.context, "페이지를 로드할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun profileUpdateApiCall(newnick:String, newhei:String, newwei:String) {
        val retrofit = RetrofitManager(Usage.ACCESS)
        retrofit.profileUpdate(NewProfile(newnick, newhei, newwei), completion = { status ->
            when(status){
                RESPONSE_STATUS.OKAY -> {
                    Log.d(Constants.TAG, "profileUpdateApiCall()")
                    Toast.makeText(this.context, "프로필이 업데이트 되었습니다.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Log.d(Constants.TAG, "ProfileFragment-OncreateView-profileUpdateApiCall() Error")
                    Toast.makeText(this.context, "프로필을 업데이트할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}