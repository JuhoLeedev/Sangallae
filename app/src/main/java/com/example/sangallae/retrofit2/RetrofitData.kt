package com.example.sangallae.retrofit2

data class SocialLoginToken (val access_token: String)

data class kakaoLogin (var username: String, var nickname: String? = null)

data class LoginPostResult (var status: String? = null, var message: String? = null, var data: Data? = null)

data class Data (var access_token: String? = null)

