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
    UNAUTHORIZED
}

enum class Usage {
    LOGIN,
    REFRESH,
    ACCESS
}

object API {
    const val BASE_URL : String = "http://ec2-15-165-252-29.ap-northeast-2.compute.amazonaws.com/"

    // admin jwt token
    const val ADMIN_JWT : String = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJybHRuMmRmZGRkZndmZXNkZmZzMTIxIiwiaWQiOjEwMDQ4LCJleHAiOjE2MjE5ODU5MzgsInVzZXJuYW1lIjoicmx0bjJkZmRkZGZ3ZmVzZGZmczEyMSJ9.TcPb3moRxl5eKt4piHGGEsIdtvnKQzG5flqQ3j78MDL2hcT06lq_fg4fYsD8MBjbEgXKAzsubDlk1cJVZhn9gw"

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
}