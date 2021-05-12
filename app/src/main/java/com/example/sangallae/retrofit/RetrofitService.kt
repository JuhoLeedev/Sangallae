package com.example.sangallae.retrofit

import com.example.sangallae.utils.API
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.*
import com.example.sangallae.retrofit.models.KakaoLogin

interface RetrofitService {

    @Headers(API.CONTENTTYPE_JSON)
    @POST(API.LOGIN_NAVER)
    fun requestNaverLogin(@Body access_token: String): Call<JsonElement>

    @Headers(API.CONTENTTYPE_JSON)
    @POST(API.LOGIN_GOOGLE)
    fun requestGoogleLogin(@Body access_token: String): Call<JsonElement>

    @Headers(API.CONTENTTYPE_JSON)
    @POST(API.LOGIN_KAKAO)
    fun requestKakaoLogin(@Body body: KakaoLogin): Call<JsonElement>

    @GET(API.SEARCH_COURSE)
    fun searchCourses(@Query("keyword") keyword: String, @Query("order") order: String): Call<JsonElement>

    @GET(API.PROFILE_LOAD)
    fun profileLoad(@Query("order") order:String): Call<JsonElement>
}