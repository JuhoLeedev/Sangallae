package com.example.sangallae.utils

object Constants {
    const val TAG : String = "로그"
}

enum class RESPONSE_STATUS {
    OKAY,
    FAIL,
    NO_CONTENT,
    NOT_FOUND,
    BAD_REQUEST,
    UNAUTHORIZED,
}

enum class Usage {
    LOGIN,
    REFRESH,
    ACCESS
}

object API {
    const val BASE_URL : String = "http://ec2-15-165-252-29.ap-northeast-2.compute.amazonaws.com/"

    // admin jwt token
    //const val ADMIN_JWT : String = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbmFkbWluYWRtaW5hZG1pbiIsImlkIjoxMDA4MSwiZXhwIjoxNjIyNjM1MzIwLCJ1c2VybmFtZSI6ImFkbWluYWRtaW5hZG1pbmFkbWluIn0.2BJvol8Xwth_bMX12TzTY9qMW6aDsjPhn3qFIaMdmBtpBF5ftd7sHXooxWCdzcXiTCVpAe0xCqNxDQplH38ZCQ"
    const val ADMIN_JWT : String = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmYXRoZXIiLCJpZCI6MCwiZXhwIjoxNjIzNjUxMjE0LCJ1c2VybmFtZSI6ImZhdGhlciJ9.MrZo6o7a3C3LnJoubu-nJjq09nBMq96YZDlE-vEkB12WBdgx5hUdDnvS1zTmFNee5_GFXY8m_RSqziiQSRiPRw"

    // social login
    const val NAVER_CLIENT_ID = "CrwgIv6IKrpcPDkNYllP"
    const val NAVER_CLIENT_SECRET = "cxy3dRqgkR"
    const val NAVER_CLIENT_NAME = "Sangallae"


    // retrofit
    const val CONTENTTYPE_JSON = "Content-Type:application/json"
    const val LOGIN_KAKAO : String = "join"
    const val LOGIN_NAVER : String = "naver-login"
    const val LOGIN_GOOGLE : String = "google-login"
    const val SEARCH_COURSE : String = "search"
    const val PROFILE_LOAD : String = "user" // profile
    const val PROFILE_UPDATE : String = "user" //profile update
    const val COURSE_DETAIL : String = "course/{id}"
    const val HOME_LOAD : String= "recommendation/main"
    const val REC_COURSE_LIST : String = "recommendation/detail"
}