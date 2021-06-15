package com.example.sangallae.retrofit

import com.example.sangallae.retrofit.models.*
import com.example.sangallae.utils.API
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.*

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
    fun searchCourses(@Query("keyword") keyword: String, @Query("order") order: String, @Query("page") page: Int): Call<JsonElement>

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

    /**
     * 찜 목록 추가 / 제거
     */
    @POST(API.FAVORITES)
    fun toggleFavorite(@Body body: Favorite): Call<JsonElement>

    @GET(API.HOME_LOAD)
    fun homeLoad(@Query("latitude") lat: Double, @Query("longitude") lon: Double): Call<JsonElement>

    @GET(API.REC_COURSE_LIST)
    fun recCourseList(@Query("page") page: Int): Call<JsonElement>

    @GET(API.HOT_COURSE_LIST)
    fun hotCourseList(@Query("page") page: Int): Call<JsonElement>

    @GET(API.HOT_MOUNTAIN_LIST)
    fun hotMountainList(@Query("page") page: Int): Call<JsonElement>

    @GET(API.RECORD)
    fun recordList(): Call<JsonElement>

    /**
     * 등산기록 업로드
     */
    @POST(API.RECORD)
    fun uploadRecord(@Body body: Record): Call<JsonElement>
}