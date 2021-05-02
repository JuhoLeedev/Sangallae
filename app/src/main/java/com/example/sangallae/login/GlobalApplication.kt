package com.example.sangallae.login

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, "8572c67014e380dd4f8ebd6503382cd0")
    }
}