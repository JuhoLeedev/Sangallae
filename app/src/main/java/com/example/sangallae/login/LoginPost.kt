package com.example.sangallae.login

import com.kakao.sdk.user.model.User
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*


interface LoginPost{

//    @FormUrlEncoded
//    //@Headers("Content-Type:application/json")
//    @POST("access_token") //base_url + "api/login" 으로 POST 통신
//    fun requestLogin(
//        @Field("access_token") access_token: String
//    ): Call<LoginPostResult>

    //@FormUrlEncoded
//    @Headers("Content-Type:application/json")
//    @POST("/naver-login")
//    fun requestLogin(
//        //@Part access_token:String
//        //@Body access_token:JSONObject
//        //@Field("access_token") access_token: String
//        @Header("access_token") access_token: String
//    ): Call<LoginPostResult>
//
    //로그인토큰
    //    @Headers("Content-Type:application/json")
    @GET("hello") // base_url + "api/login" 으로 POST 통신
    fun requestLogin(
        //@Body access_token:String
    //    @Header("access_token") access_token: String
    ): Call<MsgResult>

//        //테스트-hello
//    //    @Headers("Content-Type:application/json")
//    @GET("hello") // base_url + "api/login" 으로 POST 통신
//    fun requestLogin(
//        //@Body access_token:String
//        //@Header("access_token") access_token: String
//    ): Call<LoginPostResult>
//    //테스트-뉴스
//    //    @Headers("Content-Type:application/json")
//    @GET("v2/sources?apiKey=b101c7b2164c4b4c91d0504bed72979d") // base_url + "api/login" 으로 POST 통신
//    fun requestLogin(
//        //@Body access_token:String
//        @Header("access_token") access_token: String
//    ): Call<LoginPostResult>

    //테스트-해파랑길
//        @Headers("Content-Type:application/json")
//    @GET("/search") // base_url + "api/login" 으로 POST 통신
//    fun requestLogin(
//        //@Body access_token:String
//        @Header("keyword") course: String
//    ): Call<LoginPostResult>
}