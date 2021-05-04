package com.example.sangallae.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sangallae.MainActivity
import com.example.sangallae.R
import com.example.sangallae.retrofit2.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause.*
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LoginActivity : AppCompatActivity() {
    // Google Login result
    private val RC_SIGN_IN = 9001
    // Google Api Client
    private var googleSigninClient: GoogleSignInClient? = null
    // Firebase Auth
    private var firebaseAuth: FirebaseAuth? = null

    //네이버
    lateinit var mOAuthLoginInstance : OAuthLogin
    lateinit var mContext: Context

 //   var loginResult:LoginPostResult? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        //카카오 디버그 키해시
        val keyHash = Utility.getKeyHash(this)
        Log.d("Hash", keyHash)

        // [START config_signin]
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // [END config_signin]

        googleSigninClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance();

        val go_intent = findViewById(R.id.googleLoginBtn) as SignInButton
        go_intent.setOnClickListener {
            val signInIntent = googleSigninClient?.getSignInIntent()
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }


        //카카오 로그인
        val kakao_login = findViewById(R.id.kakaoLoginBtn) as Button
        kakao_login.setOnClickListener {
            if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            }else{
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }

        //  네이버 아이디로 로그인
        val naver_client_id = "CrwgIv6IKrpcPDkNYllP"
        val naver_client_secret = "cxy3dRqgkR"
        val naver_client_name = "Sangallae"

        mContext = this
        mOAuthLoginInstance = OAuthLogin.getInstance()
        mOAuthLoginInstance.init(mContext, naver_client_id, naver_client_secret, naver_client_name)

        val naver_login = findViewById(R.id.naverLoginBtn) as OAuthLoginButton
        naver_login.setOAuthLoginHandler(mOAuthLoginHandler)
    }  //oncreate

//
//    // 로그인 성공 시 이동할 페이지
//    fun moveMainPage(user: FirebaseUser?) {
//        if (user != null) {
//            Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//        }
//    }
    // [START onActivityResult]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Google 로그인 인텐트 응답
        if (requestCode === RC_SIGN_IN) {
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
        var retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-15-165-252-29.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var loginPost: GoogleLoginPost = retrofit.create(GoogleLoginPost::class.java)

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

    //카카오 로그인
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
        }
        else if (token != null) {
            Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
            //토큰
            UserApiClient.instance.accessTokenInfo { kakaoToken, error ->
                if (error != null) {
                    Log.e("kakao_login_fail", "토큰 정보 보기 실패", error)
                }
                else if (kakaoToken != null) {
                    //Log.d("kakaoToken", OAuthToken.toString())
                    Log.d("kakaoToken", kakaoToken.toString())
                    kakaoToken

                }
            }

            var retrofit = Retrofit.Builder()
                .baseUrl("http://ec2-15-165-252-29.ap-northeast-2.compute.amazonaws.com/") // or 8081 0
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            var loginPost: JoinPost = retrofit.create(JoinPost::class.java)

            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.d("kakaoToken", "사용자 정보 요청 실패", error)
                } else {
                    if (user != null) {
                        Log.i("kakaoToken", "사용자 정보 요청 성공" +
                                "\n회원번호: ${user.id}" +
                                "\n이메일: ${user.kakaoAccount?.email}" +
                                "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                                "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}")
                        loginPost.requestLogin(TestLogin("kakao-" + user.id, user.id.toString())).enqueue(object:
                            retrofit2.Callback<LoginPostResult> {

                            override fun onFailure(call: Call<LoginPostResult>, t: Throwable){
                                //실패시
                                Log.e("LoginResult", "Retrofit2 response error")
                                Toast.makeText(baseContext, "정보 요청에 실패했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                            }
                            override fun onResponse(call:Call<LoginPostResult>, response: Response<LoginPostResult>){
                                //정상응답 옴

                                var loginResult = response.body()?.data?.access_token.toString()
                                Log.d("LoginResult", loginResult.toString())
                            }
                        })
                    } else {
                        Log.i("kakaoToken", "비로그인 상태")
                    }
                }
            }
            moveToMain()
        }
    }

    //네이버
    val mOAuthLoginHandler: OAuthLoginHandler = @SuppressLint("HandlerLeak") //19:28 handlerleak
    object : OAuthLoginHandler() {
        //서버에 토큰 보내기
        var retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-15-165-252-29.ap-northeast-2.compute.amazonaws.com/") // or 8081 0
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var loginPost: NaverLoginPost = retrofit.create(NaverLoginPost::class.java)

        // 로그인 & 토큰
        override fun run(success: Boolean) {
            if (success) {
                //토큰
                var naverToken = mOAuthLoginInstance.getAccessToken(mContext)
                Log.d("naverToken", naverToken)
                //mOAuthLoginInstance.requestApi(mContext, naverToken, url)

                //서버에 보내기
                loginPost.requestLogin(SocialLoginToken(naverToken)).enqueue(object:
                    retrofit2.Callback<LoginPostResult> {

                    override fun onFailure(call: Call<LoginPostResult>, t: Throwable){
                        //실패시
                        Log.e("LoginResult", "Retrofit2 response error")
                        Toast.makeText(baseContext, "로그인에 실패했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                    override fun onResponse(call:Call<LoginPostResult>, response: Response<LoginPostResult>){
                        //정상응답 옴

                        var loginResult = response.body()?.data?.access_token.toString()
                        Log.d("LoginResult", loginResult.toString())
                    }
                })
                moveToMain()

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

    fun moveToMain(){
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent)
    }
}
