package com.example.sangallae

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {

    companion object{
        lateinit var instance: GlobalApplication
            private set
        lateinit var prefs: PreferenceUtil
    }

    override fun onCreate() {
        prefs = PreferenceUtil(applicationContext)
        super.onCreate()
        instance = this
        KakaoSdk.init(this, "8572c67014e380dd4f8ebd6503382cd0")
    }
}