package com.example.sangallae.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sangallae.GlobalApplication
import com.example.sangallae.R
import com.example.sangallae.retrofit.*
import com.example.sangallae.retrofit.models.JsonToken
import com.example.sangallae.retrofit.models.KakaoLogin
import com.example.sangallae.utils.API
import com.example.sangallae.utils.Constants
import com.example.sangallae.utils.RESPONSE_STATUS
import com.example.sangallae.utils.Usage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.jeongdaeri.unsplash_app_tutorial.retrofit.RetrofitManager
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause.*
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton



class LoginActivity : AppCompatActivity() {
    // Google Login result
    private val RC_SIGN_IN = 9001

    // Google Api Client
    private var googleSigninClient: GoogleSignInClient? = null

    // Firebase Auth
    private var firebaseAuth: FirebaseAuth? = null

    //네이버
    lateinit var mOAuthLoginInstance: OAuthLogin
    lateinit var mContext: Context

    //   var loginResult:LoginPostResult? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        mContext = applicationContext

//        //카카오 디버그 키해시 - 컴당 한 번만 받으면 됨
//        val keyHash = Utility.getKeyHash(this)
//        Log.d("Hash", keyHash)

        // [START config_signin]
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // [END config_signin]

        googleSigninClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance();

        val googleLogin = findViewById<SignInButton>(R.id.googleLoginBtn)
        googleLogin.setOnClickListener {
            val signInIntent = googleSigninClient?.getSignInIntent()
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }


        //카카오 로그인
        val kakaoLogin = findViewById<Button>(R.id.kakaoLoginBtn)
        kakaoLogin.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }

        //  네이버 아이디로 로그인
        mContext = this
        mOAuthLoginInstance = OAuthLogin.getInstance()
        mOAuthLoginInstance.init(mContext, API.NAVER_CLIENT_ID, API.NAVER_CLIENT_SECRET, API.NAVER_CLIENT_NAME)

        val naverLogin = findViewById<OAuthLoginButton>(R.id.naverLoginBtn)
        naverLogin.setOAuthLoginHandler(mOAuthLoginHandler)
    }  //oncreate


    // [START onActivityResult]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Google 로그인 인텐트 응답
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            } catch (e: ApiException) {

            }
        }
    }
    // [END onActivityResult]

    private lateinit var auth: FirebaseAuth

    // [START firebaseAuthWithGoogle] 구글
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
//        var retrofit = Retrofit.Builder()
//            .baseUrl("http://ec2-15-165-252-29.ap-northeast-2.compute.amazonaws.com/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//        var loginPost: GoogleLoginPost = retrofit.create(GoogleLoginPost::class.java)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) {

                // 성공여부
                if (it.isSuccessful) {
                    //val user = firebaseAuth?.currentUser
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                    moveToMain()

                } else {
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }
    // [END firebaseAuthWithGoogle]

    // 카카오 로그인
    private val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            when {
                error.toString() == AccessDenied.toString() -> {
                    Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
                }
                error.toString() == InvalidClient.toString() -> {
                    Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                }
                error.toString() == InvalidGrant.toString() -> {
                    Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT).show()
                }
                error.toString() == InvalidRequest.toString() -> {
                    Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                }
                error.toString() == InvalidScope.toString() -> {
                    Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                }
                error.toString() == Misconfigured.toString() -> {
                    Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT).show()
                }
                error.toString() == ServerError.toString() -> {
                    Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
                }
                error.toString() == Unauthorized.toString() -> {
                    Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                }
                else -> { // Unknown
                    Toast.makeText(this, "기타 에러", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (token != null) {
            Toast.makeText(this, "카카오 로그인 성공.", Toast.LENGTH_SHORT).show()
            //토큰
            UserApiClient.instance.accessTokenInfo { kakaoToken, error ->
                if (error != null) {
                    Log.e("kakao_login_fail", "토큰 정보 보기 실패", error)
                } else if (kakaoToken != null) {
                    //Log.d("kakaoToken", OAuthToken.toString())
                    Log.d("kakaoToken", kakaoToken.toString())
                }
            }

            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.d("kakaoToken", "사용자 정보 요청 실패", error)
                } else {
                    if (user != null) {
                        Log.i(
                            "kakaoToken", "사용자 정보 요청 성공" +
                                    "\n회원번호: ${user.id}" +
                                    "\n이메일: ${user.kakaoAccount?.email}" +
                                    "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                                    "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}"
                        )
                        // 서버에 보내기
                        val retrofit = RetrofitManager(Usage.LOGIN)
                        retrofit.kakaoLogin(KakaoLogin("kakao" + user.id, user.kakaoAccount?.profile?.nickname), completion = { status, token ->
                            when(status){
                                RESPONSE_STATUS.OKAY -> {
                                    Log.d(Constants.TAG, "LoginActivity - kakaoLogin called 응답 성공 / token : ${token?.access_token}")
                                    // 저장
                                    GlobalApplication.prefs.setString("access_token", token?.access_token.toString())
                                    Log.d(Constants.TAG, "LoginActivity - kakaoLogin token 저장확인 / local_token: ${GlobalApplication.prefs.getString("access_token","fail")}")
                                    moveToMain()
                                }
                                else -> {
                                    Toast.makeText(this, "서버 토큰 받기 실패", Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                    } else {
                        Log.i("kakaoToken", "비로그인 상태")
                    }
                }
            }
        }
    }

    //네이버
    val mOAuthLoginHandler: OAuthLoginHandler = @SuppressLint("HandlerLeak") //19:28 handlerleak
    object : OAuthLoginHandler() {

        // 로그인 & 토큰
        override fun run(success: Boolean) {
            if (success) {
                //토큰
                var naverToken = mOAuthLoginInstance.getAccessToken(mContext)
                Log.d("naverToken", naverToken)
                //mOAuthLoginInstance.requestApi(mContext, naverToken, url)

                //서버에 보내기
                val retrofit = RetrofitManager(Usage.LOGIN)
                retrofit.naverLogin(JsonToken(naverToken), completion = { status, token ->
                    when(status){
                        RESPONSE_STATUS.OKAY -> {
                            Log.d(Constants.TAG, "LoginActivity - naverLogin called 응답 성공 / token : ${token?.access_token}")
                            GlobalApplication.prefs.setString("access_token", token?.access_token.toString())
                            Log.d(Constants.TAG, "LoginActivity - naverLogin token 저장확인 / local_token: ${GlobalApplication.prefs.getString("access_token","fail")}")
                            moveToMain()
                        }
                        else -> {
                            Toast.makeText(baseContext, "로그인을 할 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            } else {
                val errorCode: String = mOAuthLoginInstance.getLastErrorCode(mContext).code
                val errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext)

                Toast.makeText(
                    baseContext, "errorCode:" + errorCode
                            + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun moveToMain() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent)
    }
}
