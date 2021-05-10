package com.jeongdaeri.unsplash_app_tutorial.retrofit

import android.util.Log
import com.example.sangallae.retrofit.models.Course
import com.example.sangallae.retrofit.RetrofitClient
import com.example.sangallae.retrofit.RetrofitService
import com.example.sangallae.retrofit.models.JsonToken
import com.example.sangallae.retrofit.models.KakaoLogin
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

    // 사진 검색 api 호출
    fun searchCourses(
        keyword: String?, order: String?,
        completion: (RESPONSE_STATUS, ArrayList<Course>?) -> Unit
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
                        val parsedCourseDataArray = ArrayList<Course>()
                        val body = it.asJsonObject
                        val results = body.getAsJsonArray("data")
                        val message = body.get("message")

                        when (val status = body.get("status").asString) {
                            "OK" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                results.forEach { resultItem ->
                                    val resultItemObject = resultItem.asJsonObject
                                    val courseName = resultItemObject.get("name").asString
                                    val courseDistance = resultItemObject.get("distance").asString
                                    val courseHeight = resultItemObject.get("height").asString
                                    val courseTime = resultItemObject.get("time").asString
                                    val courseDifficulty = resultItemObject.get("difficulty").asString
                                    val courseReviewCount = resultItemObject.get("review_cnt").asString
                                    val courseScore = resultItemObject.get("score").asString
                                    val courseThumbnailUrl = resultItemObject.get("thumbnail").asString

                                    val courseItem = Course(
                                        id = "",
                                        name = courseName,
                                        distance = courseDistance,
                                        height = courseHeight,
                                        time = courseTime,
                                        diffiulty = courseDifficulty,
                                        url = "",
                                        review_cnt = "($courseReviewCount)",
                                        score = courseScore,
                                        thumbnail = courseThumbnailUrl,
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

    // 네이버 로그인
    fun naverLogin(
        accessToken: String?,
        completion: (RESPONSE_STATUS, JsonToken?) -> Unit
    ) {
        val token = accessToken ?: ""

        val call = iRetrofit?.requestNaverLogin(access_token = token) ?: return

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
                            val parsedJsonTokenData = JsonToken()
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
                        val parsedJsonTokenData = JsonToken()
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
                        val parsedJsonTokenData = JsonToken()
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
