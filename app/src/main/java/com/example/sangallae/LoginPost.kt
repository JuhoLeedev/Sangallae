package com.example.sangallae

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoginPost{

    @FormUrlEncoded
    @POST("/app_login/")
    fun requestLogin(
        @Field("socialToken") socialToken:String
    ): Call<LoginPostResult>

}