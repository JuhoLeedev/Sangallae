package com.example.sangallae.retrofit

import com.example.sangallae.utils.API
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.*

//interface RetrofitInterface {
//
//    @Headers(API.CONTENTTYPE_JSON)
//    @POST(API.LOGIN_NAVER)
//    fun requestNaverLogin(@Body access_token: SocialLoginToken): Call<JsonElement>
//
//    @Headers(API.CONTENTTYPE_JSON)
//    @POST(API.LOGIN_GOOGLE)
//    fun requestGoogleLogin(@Body access_token: SocialLoginToken): Call<JsonElement>
//
//    @Headers(API.CONTENTTYPE_JSON)
//    @POST(API.LOGIN_KAKAO)
//    fun requestKakaoLogin(@Body access_token: SocialLoginToken): Call<JsonElement>
//
//    @Headers(API.ADMIN_JWT)
//    @GET(API.SEARCH_COURSE)
//    fun searchCourses(@Query("keyword") keyword: String, @Query("order") order: String)
//}