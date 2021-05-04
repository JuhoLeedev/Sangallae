package com.example.sangallae.retrofit2

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NaverLoginPost{
    @Headers("Content-Type:application/json")
    @POST("naver-login")
    fun requestLogin(
        @Body access_token: SocialLoginToken
    ): Call<LoginPostResult>
}

interface GoogleLoginPost{
    @Headers("Content-Type:application/json")
    @POST("google-login")
    fun requestLogin(
        @Body access_token: SocialLoginToken
    ): Call<LoginPostResult>
}

interface KakaoLoginPost{
    @Headers("Content-Type:application/json")
    @POST("kakao-login")
    fun requestLogin(
        @Body access_token: SocialLoginToken
    ): Call<LoginPostResult>
}

interface JoinPost{
    @Headers("Content-Type:application/json")
    @POST("join")
    fun requestLogin(
        @Body body: TestLogin

    ): Call<LoginPostResult>
}
