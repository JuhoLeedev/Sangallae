package com.example.sangallae.retrofit

import com.example.sangallae.models.SearchQuery
import com.example.sangallae.models.SearchResult
import com.example.sangallae.utils.API
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.*

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
    @POST("join")
    fun requestLogin(
        @Body body: kakaoLogin
    ): Call<LoginPostResult>
}

interface SearchPost {
    @Headers(API.ADMIN_JWT)
    @GET(API.SEARCH_COURSE)
    fun searchCourses(@Query("keyword") keyword: String?, @Query ("order") order: String? ): Call<SearchResult>
   // fun searchCourses(@Query body: SearchQuery: Call<SearchResult>
}
