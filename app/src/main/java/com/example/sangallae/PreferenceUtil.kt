package com.example.sangallae

import android.content.Context
import android.content.SharedPreferences

// 서버에서 준 토큰을 로컬에 저장
class PreferenceUtil(context: Context) {
//    val naver_access_token = ""  // 서버에서 준 거
//    val kakao_access_token = ""
//    val google_access_token = ""
    val prefs: SharedPreferences =
        context.getSharedPreferences("access_token", Context.MODE_PRIVATE)

    fun getString(key: String, defValue: String): String {
        return prefs.getString(key, defValue).toString()
    }
    fun setString(key: String, str: String) {
        prefs.edit().putString(key, str).apply()
    }
}
