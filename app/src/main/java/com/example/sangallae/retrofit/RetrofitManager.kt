package com.jeongdaeri.unsplash_app_tutorial.retrofit

import android.util.Log
import com.example.sangallae.retrofit.RetrofitClient
import com.example.sangallae.retrofit.RetrofitService
import com.example.sangallae.retrofit.models.*
import com.example.sangallae.utils.API
import com.example.sangallae.utils.Constants.TAG
import com.example.sangallae.utils.RESPONSE_STATUS
import com.example.sangallae.utils.Usage
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Response

class RetrofitManager(usage: Usage) {

    // 레트로핏 인터페이스 가져오기
    private val iRetrofit: RetrofitService? =
        RetrofitClient.getClient(API.BASE_URL, usage)?.create(RetrofitService::class.java)

    // 등산로 검색 api 호출
    fun searchCourses(
        keyword: String?, order: String?,
        completion: (RESPONSE_STATUS, ArrayList<CourseItem>?) -> Unit
    ) {
        val term = keyword ?: ""

        val call = iRetrofit?.searchCourses(keyword = term, order = "") ?: return

        call.enqueue(object : retrofit2.Callback<JsonElement> {
            // 응답 실패시
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "RetrofitManager - onFailure() called / t: $t")
                completion(RESPONSE_STATUS.FAIL, null)
            }

            // 응답 성공시
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d(TAG, "RetrofitManager - onResponse() called / response : ${response.body()}")

                if(response.isSuccessful) {
                    response.body()?.let {
                        val parsedCourseDataArray = ArrayList<CourseItem>()
                        val body = it.asJsonObject
                        val results = body.getAsJsonArray("data")
                        val message = body.get("message")

                        when (val status = body.get("status").asString) {
                            "OK" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                results.forEach { resultItem ->
                                    val resultItemObject = resultItem.asJsonObject
                                    val courseId = resultItemObject.get("id").asInt
                                    val courseName = resultItemObject.get("name").asString
                                    val courseDistance = resultItemObject.get("distance").asString
                                    val courseSpeed = resultItemObject.get("avg_speed").asString
                                    val courseMovingTime = resultItemObject.get("moving_time").asString
                                    val courseElevation = resultItemObject.get("ele_dif").asString
                                    val courseDifficulty = resultItemObject.get("difficulty").asString
                                    val courseThumbnailUrl = resultItemObject.get("thumbnail").asString

                                    val courseItem = CourseItem(
                                        id = courseId,
                                        name = courseName,
                                        distance = courseDistance + "km",
                                        avg_speed = courseSpeed + "km/h",
                                        moving_time = courseMovingTime,
                                        ele_dif = courseElevation + "m",
                                        thumbnail = courseThumbnailUrl,
                                        difficulty = courseDifficulty
                                    )
                                    parsedCourseDataArray.add(courseItem)
                                }
                                completion(RESPONSE_STATUS.OKAY, parsedCourseDataArray)
                            }
                            "NO_CONTENT" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                completion(RESPONSE_STATUS.NO_CONTENT, null)
                            }
                            "BAD_REQUEST" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                completion(RESPONSE_STATUS.BAD_REQUEST, null)
                            }
                            "UNAUTHORIZED" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                completion(RESPONSE_STATUS.UNAUTHORIZED, null)
                            }
                        }
                    }
                }
                else {
                    Log.d(TAG, "RetrofitManager - onResponse() called / 404 NOT FOUND")
                    completion(RESPONSE_STATUS.NOT_FOUND, null)
                }
            }
        })
    }

    // 상세 정보 api 호출
    fun getCourseDetail(
        id: Int,
        completion: (RESPONSE_STATUS, Course?) -> Unit
    ) {
        val call = iRetrofit?.getCourseDetail(id = id.toString()) ?: return

        call.enqueue(object : retrofit2.Callback<JsonElement> {
            // 응답 실패시
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "RetrofitManager - onFailure() called / t: $t")

                completion(RESPONSE_STATUS.FAIL, null)
            }

            // 응답 성공시
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d(TAG, "RetrofitManager - onResponse() called / response : ${response.body()}")


                if(response.isSuccessful) {
                    response.body()?.let {
                        val parsedCourseData: Course
                        val body = it.asJsonObject
                        val result = body.getAsJsonObject("data")
                        val message = body.get("message")

                        when (val status = body.get("status").asString) {
                            "OK" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                val courseId = result.get("id").asInt
                                val courseName = result.get("name").asString
                                val courseDistance = result.get("distance").asString
                                val courseMaxHeight= result.get("max_height").asString
                                val courseMinHeight = result.get("main_height").asString
                                val courseTime = result.get("time").asString
                                val courseDifficulty = result.get("difficulty").asString
                                val courseUrl = result.get("url").asString
                                val courseReviewCount = result.get("review_cnt").asString
                                val courseScore = result.get("score").asString
                                val courseThumbnailUrl = result.get("thumbnail").asString
                                val courseLocation = result.get("location").asString
                                val courseSpeed = result.get("speed").asString
                                val course = Course(
                                    id = courseId,
                                    name = courseName,
                                    distance = courseDistance + "km",
                                    height = courseHeight + "m",
                                    time = courseTime,
                                    diffiulty = courseDifficulty,
                                    url = courseUrl,
                                    review_cnt = "($courseReviewCount)",
                                    score = courseScore,
                                    thumbnail = courseThumbnailUrl,
                                    location = courseLocation,
                                    speed = courseSpeed + "km/h"
                                )
                                parsedCourseData = course
                                completion(RESPONSE_STATUS.OKAY, parsedCourseData)
                            }
                            "NO_CONTENT" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                completion(RESPONSE_STATUS.NO_CONTENT, null)
                            }
                            "BAD_REQUEST" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                completion(RESPONSE_STATUS.BAD_REQUEST, null)
                            }
                            "UNAUTHORIZED" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                completion(RESPONSE_STATUS.UNAUTHORIZED, null)
                            }
                        }
                    }
                }
                else {
                    Log.d(TAG, "RetrofitManager - onResponse() called / 404 NOT FOUND")
                    completion(RESPONSE_STATUS.NOT_FOUND, null)
                }
            }
        })
    }

//    // 마이페이지 프로필/통계 로드
//    fun profileLoad(
//        completion: (RESPONSE_STATUS, Profile?) -> Unit
//    ) {
//        val call = iRetrofit?.profileLoad() ?: return
//
//        call.enqueue(object : retrofit2.Callback<JsonElement> {
//            // 응답 실패시
//            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
//                Log.d(TAG, "RetrofitManager - onFailure() called / t: $t")
//
//                completion(RESPONSE_STATUS.FAIL, null)
//            }
//
//            // 응답 성공시
//            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
//                Log.d(TAG, "RetrofitManager - onResponse() called / response : ${response.body()}")
//
//                if(response.isSuccessful) {
//                    response.body()?.let {
//                        //val parsedProfileDataArray = ArrayList<Course>()
//                        val body = it.asJsonObject
//                        val results = body.getAsJsonObject("data")
//                        val message = body.get("message")
//
//                        when (val status = body.get("status").asString) {
//                            "OK" -> {
//                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
//                                val profilePicture = results.get("picture").asString
//                                val profileNickname = results.get("nickname").asString
//                                //val profileHeightWeight = results.get("user_height_weight").asString
//                                val profileHeight = results.get("user_height").asString
//                                val profileWeight = results.get("user_weight").asString
//                                val profileTotalDistance = results.get("total_distance").asString
//                                val profileAvgDistance = results.get("avg_distance").asString
//                                val profileMaxDistance = results.get("max_distance").asString
//                                val profileTotalTime = results.get("total_total_time").asString
//                                val profileAvgTime = results.get("avg_total_time").asString
//                                val profileMaxTime = results.get("max_total_time").asString
//
//                                val profileTotalMTime = results.get("total_moving_time").asString
//                                val profileAvgMTime = results.get("avg_moving_time").asString
//                                val profileMaxMTime = results.get("max_moving_time").asString
//
//                                val profileTotalUphill = results.get("total_total_uphill").asString
//                                val profileMaxUphill = results.get("avg_total_uphill").asString
//                                val profileAvgUphill = results.get("max_total_uphill").asString
//
//                                val profileTotalDownhill = results.get("total_total_downhill").asString
//                                val profileMaxDownhill = results.get("avg_total_downhill").asString
//                                val profileAvgDownhill = results.get("max_total_downhill").asString
//
//                                //val profileTotalHeight = results.get("total_height").asString
//                                val profileMaxHeight = results.get("max_height").asString
//                                val profileAvgHeight = results.get("avg_height").asString
//
//                                val profileMaxSpeed = results.get("max_speed").asString
//                                val profileAvgSpeed = results.get("avg_speed").asString
//                                val profileMaxPace = results.get("max_pace").asString
//                                val profileAvgPace = results.get("avg_pace").asString
//                                val profileTotalCalories = results.get("total_calories").asString
//                                val profileAvgCalores = results.get("avg_calories").asString
//
//                                val profileItem = Profile(
////                                    picture = profilePicture,
////                                    nickname = profileNickname,
////                                    user_height_weight = "{$profileHeight}cm / {$profileWeight}kg",
////                                    total_distance = "{$profileTotalDistance}km",
////                                    avg_distance = "{$profileAvgDistance}km",
////                                    total_time = profileTotalTime,
////                                    avg_time = profileAvgTime,
////                                    total_height = "{$profileTotalHeight}m",
////                                    max_height = "{$profileMaxHeight}m",
////                                    avg_height = "{$profileAvgHeight}m",
////                                    max_speed = "{$profileMaxSpeed}km/h",
////                                    avg_speed = "{$profileAvgSpeed}km/h",
////                                    total_calories = "{$profileTotalCalories}kcal",
////                                    avg_calories = "{$profileAvgCalores}kcal"
//                                    user_id = "",
//                                    picture = profilePicture,
//                                    nickname = profileNickname,
//                                    //user_height_weight = profileHeightWeight,
//                                    user_height = profileHeight,
//                                    user_weight = profileWeight,
//                                    total_distance = profileTotalDistance,
//                                    avg_distance = profileAvgDistance,
////                                    total_time = profileTotalTime,
////                                    avg_time = profileAvgTime,
////                                    total_height = profileTotalHeight,
//                                    max_height = profileMaxHeight,
//                                    avg_height = profileAvgHeight,
//                                    max_speed = profileMaxSpeed,
//                                    avg_speed = profileAvgSpeed,
//                                    total_calories = profileTotalCalories,
//                                    avg_calories = profileAvgCalores
//                                )
//                                completion(RESPONSE_STATUS.OKAY, profileItem)
//                            }
//                            "BAD_REQUEST" -> {
//                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
//                                completion(RESPONSE_STATUS.BAD_REQUEST, null)
//                            }
//                            "UNAUTHORIZED" -> {
//                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
//                                completion(RESPONSE_STATUS.UNAUTHORIZED, null)
//                            }
//                        }
//                    }
//                }
//                else {
//                    Log.d(TAG, "RetrofitManager - onResponse() called / 404 NOT FOUND")
//                    completion(RESPONSE_STATUS.NOT_FOUND, null)
//                }
//            }
//        })
//    }

    // 프로필 업데이트
    fun profileUpdate(
        newprofile: NewProfile,
        completion: (RESPONSE_STATUS) -> Unit
    ) {
//        val nick = nickname ?: ""
//        val hei = height ?: ""
//        val wei = weight ?: ""
        val call = iRetrofit?.profileUpdate(body = newprofile) ?: return

        call.enqueue(object : retrofit2.Callback<JsonElement> {
            // 응답 실패시
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "RetrofitManager - onFailure() called / t: $t")

                completion(RESPONSE_STATUS.FAIL)
            }

            // 응답 성공시
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d(TAG, "RetrofitManager - onResponse() called / response : ${response.body()}")

                if(response.isSuccessful) {
                    response.body()?.let {
                        //val parsedProfileDataArray = ArrayList<Course>()
                        val body = it.asJsonObject
//                        val results = body.getAsJsonObject("data")
                        val message = body.get("message")

                        when (val status = body.get("status").asString) {
                            "CREATED" -> { // 업데이트 성공
                                completion(RESPONSE_STATUS.OKAY)
                            }
                            "BAD_REQUEST" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                completion(RESPONSE_STATUS.BAD_REQUEST)
                            }
                            "UNAUTHORIZED" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                completion(RESPONSE_STATUS.UNAUTHORIZED)
                            }
                        }
                    }
                }
                else {
                    Log.d(TAG, "RetrofitManager - onResponse() called / 404 NOT FOUND")
                    completion(RESPONSE_STATUS.NOT_FOUND)
                }
            }
        })
    }

    // 네이버 로그인
    fun naverLogin(
        accessToken: JsonToken,
        completion: (RESPONSE_STATUS, JsonToken?) -> Unit
    ) {
        //val token = accessToken

        val call = iRetrofit?.requestNaverLogin(body = accessToken) ?: return

        call.enqueue(object : retrofit2.Callback<JsonElement> {
            // 응답 실패시
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "RetrofitManager - onFailure() called / t: $t")
                completion(RESPONSE_STATUS.FAIL, null)
            }

            // 응답 성공시
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d(TAG, "RetrofitManager - onResponse() called / response : ${response.body()}")

                if(response.isSuccessful) {
                        response.body()?.let {
                            val parsedJsonTokenData = JsonToken("")
                            val body = it.asJsonObject
                            val message = body.get("message")
                            val result = body.getAsJsonObject("data")

                            when (val status = body.get("status").asString) {
                                "OK" -> {
                                    Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                    parsedJsonTokenData.access_token =
                                        result.get("access_token").asString
                                    completion(RESPONSE_STATUS.OKAY, parsedJsonTokenData)
                                }
                                "NO_CONTENT" -> {
                                    Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                    completion(RESPONSE_STATUS.NO_CONTENT, null)
                                }
                                "BAD_REQUEST" -> {
                                    Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                    completion(RESPONSE_STATUS.BAD_REQUEST, null)
                                }
                                "UNAUTHORIZED" -> {
                                    Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                    completion(RESPONSE_STATUS.UNAUTHORIZED, null)
                                }
                            }
                        }
                }
                else {
                    Log.d(TAG, "RetrofitManager - onResponse() called / 404 NOT FOUND")
                    completion(RESPONSE_STATUS.NOT_FOUND, null)
                }
            }
        })
    }

    // 구글 로그인
    fun googleLogin(
        accessToken: String?,
        completion: (RESPONSE_STATUS, JsonToken?) -> Unit
    ) {
        val token = accessToken ?: ""

        val call = iRetrofit?.requestGoogleLogin(access_token = token) ?: return

        call.enqueue(object : retrofit2.Callback<JsonElement> {
            // 응답 실패시
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "RetrofitManager - onFailure() called / t: $t")
                completion(RESPONSE_STATUS.FAIL, null)
            }

            // 응답 성공시
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d(TAG, "RetrofitManager - onResponse() called / response : ${response.body()}")

                if(response.isSuccessful) {
                    response.body()?.let {
                        val parsedJsonTokenData = JsonToken("")
                        val body = it.asJsonObject
                        val message = body.get("message")
                        val result = body.getAsJsonObject("data")

                        when (val status = body.get("status").asString) {
                            "OK" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                parsedJsonTokenData.access_token =
                                    result.get("access_token").asString
                                completion(RESPONSE_STATUS.OKAY, parsedJsonTokenData)
                            }
                            "NO_CONTENT" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                completion(RESPONSE_STATUS.NO_CONTENT, null)
                            }
                            "BAD_REQUEST" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                completion(RESPONSE_STATUS.BAD_REQUEST, null)
                            }
                            "UNAUTHORIZED" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                completion(RESPONSE_STATUS.UNAUTHORIZED, null)
                            }
                        }
                    }
                }
                else {
                    Log.d(TAG, "RetrofitManager - onResponse() called / 404 NOT FOUND")
                    completion(RESPONSE_STATUS.NOT_FOUND, null)
                }
            }
        })
    }

    // 카카오 로그인
    fun kakaoLogin(
        kakaoUserInfo: KakaoLogin,
        completion: (RESPONSE_STATUS, JsonToken?) -> Unit
    ) {

        val call = iRetrofit?.requestKakaoLogin(body = kakaoUserInfo) ?: return

        call.enqueue(object : retrofit2.Callback<JsonElement> {
            // 응답 실패시
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "RetrofitManager - onFailure() called / t: $t")
                completion(RESPONSE_STATUS.FAIL, null)
            }

            // 응답 성공시
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d(TAG, "RetrofitManager - onResponse() called / response : ${response.body()}")

                if(response.isSuccessful) {
                    response.body()?.let {
                        val parsedJsonTokenData = JsonToken("") //
                        val body = it.asJsonObject
                        val message = body.get("message")
                        val result = body.getAsJsonObject("data")

                        when (val status = body.get("status").asString) {
                            "OK" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                parsedJsonTokenData.access_token =
                                    result.get("access_token").asString
                                completion(RESPONSE_STATUS.OKAY, parsedJsonTokenData)
                            }
                            "NO_CONTENT" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                completion(RESPONSE_STATUS.NO_CONTENT, null)
                            }
                            "BAD_REQUEST" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                completion(RESPONSE_STATUS.BAD_REQUEST, null)
                            }
                            "UNAUTHORIZED" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                completion(RESPONSE_STATUS.UNAUTHORIZED, null)
                            }
                        }
                    }
                }
                else {
                    Log.d(TAG, "RetrofitManager - onResponse() called / 404 NOT FOUND")
                    completion(RESPONSE_STATUS.NOT_FOUND, null)
                }
            }
        })
    }
}
