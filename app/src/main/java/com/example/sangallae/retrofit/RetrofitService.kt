package com.example.sangallae.retrofit

import com.example.sangallae.retrofit.models.JsonToken
import com.example.sangallae.utils.API
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.*
import com.example.sangallae.retrofit.models.KakaoLogin
import com.example.sangallae.retrofit.models.NewProfile

interface RetrofitService {

    @Headers(API.CONTENTTYPE_JSON)
    @POST(API.LOGIN_NAVER)
    fun requestNaverLogin(@Body body: JsonToken): Call<JsonElement>
    //fun requestNaverLogin(@Body access_token: String): Call<JsonElement>

    @Headers(API.CONTENTTYPE_JSON)
    @POST(API.LOGIN_GOOGLE)
    fun requestGoogleLogin(@Body access_token: String): Call<JsonElement>

    @Headers(API.CONTENTTYPE_JSON)
    @POST(API.LOGIN_KAKAO)
    fun requestKakaoLogin(@Body body: KakaoLogin): Call<JsonElement>

    @GET(API.SEARCH_COURSE)
    fun searchCourses(@Query("keyword") keyword: String, @Query("order") order: String): Call<JsonElement>

    @GET(API.PROFILE_LOAD)
    fun profileLoad(): Call<JsonElement>

//    @GET(API.PROFILE_UPDATE)
//    fun profileUpdate(@Query("nickname") nickname:String, @Query("Height") height:String, @Query("Weight") weight:String): Call<JsonElement>
    //@FormUrlEncoded
    @Headers(API.CONTENTTYPE_JSON)
    @POST(API.PROFILE_UPDATE)
    fun profileUpdate(@Body body: NewProfile): Call<JsonElement>

    @GET(API.COURSE_DETAIL)
    fun getCourseDetail(@Path("id") id: String): Call<JsonElement>

    @GET(API.FAVORITES)
    fun favoriteCourses(): Call<JsonElement>
    @GET(API.HOME_LOAD)
    fun homeLoad(): Call<JsonElement>

    @GET(API.REC_COURSE_LIST)
    fun recCourseList(): Call<JsonElement>

    @GET(API.HOT_COURSE_LIST)
    fun hotCourseList(): Call<JsonElement>

    @GET(API.HOT_MOUNTAIN_LIST)
    fun hotMountainList(): Call<JsonElement>
}