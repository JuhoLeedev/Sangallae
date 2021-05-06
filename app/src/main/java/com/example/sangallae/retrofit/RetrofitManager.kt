//package com.jeongdaeri.unsplash_app_tutorial.retrofit
//
//import android.util.Log
//import com.example.sangallae.models.Course
//import com.example.sangallae.retrofit.RetrofitClient
//import com.example.sangallae.retrofit.RetrofitInterface
//import com.example.sangallae.utils.API
//import com.example.sangallae.utils.Constants.TAG
//import com.example.sangallae.utils.RESPONSE_STATUS
//import com.google.gson.JsonElement
//import retrofit2.Call
//import retrofit2.Response
//
//
//class RetrofitManager {
//
//    companion object {
//        val instance = RetrofitManager()
//    }
//
//    // 레트로핏 인터페이스 가져오기
//    private val iRetrofit: RetrofitInterface? =
//        RetrofitClient.getClient(API.BASE_URL)?.create(RetrofitInterface::class.java)
//
//
//    // 사진 검색 api 호출
//    fun searchCourses(
//        keyword: String?, order: String?,
//        completion: (RESPONSE_STATUS, ArrayList<Course>?) -> Unit
//    ) {
//
//        val term = keyword.let {
//            it
//        } ?: ""
//
////        val term = searchTerm ?: ""
//
////        val call = iRetrofit?.searchCourses(keyword = term, order = "").let {
////            it
////        } ?: return
//////        val call = iRetrofit?.searchCourses(keyword = term, order = "") ?: return
//        val call = iRetrofit?.searchCourses(term,"")
//
//        iRetrofit?.searchCourses(term,"").enqueue(object : retrofit2.Callback<JsonElement> {
//
//            // 응답 실패시
//            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
//                Log.d(TAG, "RetrofitManager - onFailure() called / t: $t")
//
//                completion(RESPONSE_STATUS.FAIL, null)
//
//            }
//
//            // 응답 성공시
//            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
//                Log.d(TAG, "RetrofitManager - onResponse() called / response : ${response.body()}")
//
//
//                when (response.code()) {
//                    200 -> {
//
//                        response.body()?.let {
//
//                            var parsedCourseDataArray = ArrayList<Course>()
//
//                            val body = it.asJsonObject
//
//                            val results = body.getAsJsonArray("data")
//
//                            val total = body.get("total").asInt
//
//                            Log.d(TAG, "RetrofitManager - onResponse() called / total: $total")
//
//                            // 데이터가 없으면 no_content 로 보낸다.
//                            if (total == 0) {
//                                completion(RESPONSE_STATUS.NO_CONTENT, null)
//
//                            } else { // 데이터가 있다면
//
//                                results.forEach { resultItem ->
//                                    val resultItemObject = resultItem.asJsonObject
//                                    val courseName = resultItemObject.get("name").asString
//                                    val courseDistance = resultItemObject.get("distance").asString
//                                    val courseHeight =
//                                        resultItemObject.get("height").asJsonObject.get("thumb").asString
//                                    val courseTime = resultItemObject.get("time").asString
//                                    val courseDifficultyInt =
//                                        resultItemObject.get("difficulty").asInt
//                                    val courseDifficulty: Int
//                                    if (courseDifficultyInt < 30)
//                                        courseDifficulty = 0
//                                    else if (courseDifficultyInt >= 30 || courseDifficultyInt < 70)
//                                        courseDifficulty = 1
//                                    else
//                                        courseDifficulty = 2
//                                    val courseReviewCount =
//                                        resultItemObject.get("review_cnt").asString
//                                    val courseScore = resultItemObject.get("score").asString
//                                    val courseThumbnailUrl =
//                                        resultItemObject.get("thumbnail").asString
//
//
//                                    //Log.d(TAG, "RetrofitManager - outputDateString : $outputDateString")
//
//                                    val courseItem = Course(
//                                        id = "",
//                                        name = courseName,
//                                        distance = courseDistance,
//                                        height = courseHeight,
//                                        time = courseTime,
//                                        diffiulty = courseDifficulty,
//                                        url = "",
//                                        review_cnt = courseReviewCount,
//                                        score = courseScore,
//                                        thumbnail = courseThumbnailUrl,
//                                    )
//                                    parsedCourseDataArray.add(courseItem)
//
//                                }
//
//                                completion(RESPONSE_STATUS.OKAY, parsedCourseDataArray)
//                            }
//                        }
//
//
//                    }
//                }
//
//
//            }
//
//        })
//    }
//
//
//}
