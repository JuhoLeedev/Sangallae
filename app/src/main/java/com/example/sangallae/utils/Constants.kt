package com.example.sangallae.utils

object Constants {
    const val TAG : String = "로그"
}

enum class RESPONSE_STATUS {
    OKAY,
    DELETE_SUCCESS,
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
    const val ADMIN_JWT : String = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmYXRoZXIiLCJpZCI6MCwiZXhwIjoxNjI1MzY0MTgwLCJ1c2VybmFtZSI6ImZhdGhlciJ9.g-PpF5bT6igQTNRp6pKMj96DXtpcKTmsNDgG7owRIYlVuFMUHZ4Ij5i0ouGhYJxbFqJIbyM_C9VrU-rTUC5b3Q"

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
    const val FAVORITES : String = "favorite"
    const val HOME_LOAD : String= "course/main"
    const val REC_COURSE_LIST : String = "course/recommendation"
    const val HOT_COURSE_LIST : String = "course/hot"
    const val HOT_MOUNTAIN_LIST: String = "mountain/hot"
    const val RECORD: String = "record"

    //aws 가짜
    const val AWS_ACCESS_KEY: String = ""
    const val AWS_SECRET_KEY: String = ""

    //이게 진짜 push 할 때 주석처리 할것!!
//    const val AWS_ACCESS_KEY: String = ""
//    const val AWS_SECRET_KEY: String = ""
    const val S3_BUCKET: String = "gpxfiles"
    const val GPX_DIR: String = "/storage/emulated/0/gpxdata/"

    const val READ_STORAGE_PERMISSIONS_REQUEST = 1001
    const val WRITE_STORAGE_PERMISSIONS_REQUEST = 1002
    const val LOCATION_PERMISSION_REQUEST_CODE = 1000


}