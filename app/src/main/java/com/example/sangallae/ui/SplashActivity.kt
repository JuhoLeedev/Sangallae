package com.example.sangallae.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.kakao.sdk.auth.TokenManager
import com.kakao.sdk.auth.model.OAuthToken
import com.nhn.android.naverlogin.OAuthLogin

class SplashActivity : AppCompatActivity() {

    //val SPLASH_VIEW_TIME: Long = 2000 //2초간 스플래시 화면을 보여줌 (ms)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        Handler().postDelayed({ //delay를 위한 handler
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//        }, SPLASH_VIEW_TIME)
    }

    //자동 로그인. 셋 중 하나라도 토큰 저장되어 있으면 메인페이지로 넘어감
    override fun onStart() {
        super.onStart()
        val firebaseAuth = FirebaseAuth.getInstance();
        val mOAuthLoginInstance = OAuthLogin.getInstance()
        val mContext = this

        //구글 자동로그인
        if(firebaseAuth?.currentUser != null){
            Log.d("start", "google: "+firebaseAuth?.currentUser.toString())
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        //네이버 자동로그인
        else if(mOAuthLoginInstance.getAccessToken(mContext)!=null){
            Log.d("start", "naver: "+mOAuthLoginInstance.getAccessToken(mContext).toString())
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        //카카오 자동로그인
        else if(TokenManager.instance.getToken()!= null){
            Log.d("start", "kakao: "+ OAuthToken.toString())
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        else { //로그인 정보 없음
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}