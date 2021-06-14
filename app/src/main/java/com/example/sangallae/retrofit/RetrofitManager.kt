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
                                    val courseMovingTime = resultItemObject.get("moving_time").asString
                                    val courseElevation = resultItemObject.get("ele_dif").asString
                                    val courseDifficulty = resultItemObject.get("difficulty").asString
                                    val courseThumbnailUrl = resultItemObject.get("thumbnail").asString

                                    val courseItem = CourseItem(
                                        id = courseId,
                                        name = courseName,
                                        distance = courseDistance + "km",
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

    // 찜 목록
    fun favoriteCourses(
        completion: (RESPONSE_STATUS, ArrayList<CourseItem>?) -> Unit
    ) {
        val call = iRetrofit?.favoriteCourses() ?: return

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
                        val data = body.getAsJsonObject("data")
                        val message = body.get("message")

                        when (val status = body.get("status").asString) {
                            "OK" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                val results = data.getAsJsonArray("content")
                                results.forEach { resultItem ->
                                    val resultItemObject = resultItem.asJsonObject
                                    val courseId = resultItemObject.get("id").asInt
                                    val courseName = resultItemObject.get("name").asString
                                    val courseDistance = resultItemObject.get("distance").asString
                                    val courseMovingTime = resultItemObject.get("moving_time").asString
                                    val courseElevation = resultItemObject.get("ele_dif").asString
                                    val courseDifficulty = resultItemObject.get("difficulty").asString
                                    val courseThumbnailUrl = resultItemObject.get("thumbnail").asString

                                    val courseItem = CourseItem(
                                        id = courseId,
                                        name = courseName,
                                        distance = courseDistance + "km",
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
                                val courseMinHeight = result.get("min_height").asString
                                val courseEleDif = result.get("ele_dif").asString
                                val courseMovingTime = result.get("moving_time_str").asString
                                val courseTotalTime = result.get("total_time_str").asString
                                val courseDifficulty = result.get("difficulty").asString
                                val courseUrl = result.get("url").asString
                                val courseThumbnailUrl = result.get("thumbnail").asString
                                val courseLocation = result.get("location").asString
                                val courseAvgSpeed = result.get("avg_speed").asString
                                val courseAvgPace = result.get("avg_pace").asString
                                val courseDate = result.get("date").asString
                                val courseUphill = result.get("total_uphill").asString
                                val courseDownhill = result.get("total_downhill").asString
                                val course = Course(
                                    id = courseId,
                                    name = courseName,
                                    distance = courseDistance + "km",
                                    moving_time = courseMovingTime,
                                    total_time = courseTotalTime,
                                    max_height = courseMaxHeight + "m",
                                    min_height = courseMinHeight + "m",
                                    avg_speed = courseAvgSpeed + "km/h",
                                    avg_pace = courseAvgPace + "min/h",
                                    ele_dif = courseEleDif,
                                    difficulty = courseDifficulty,
                                    url = courseUrl,
                                    date = courseDate,
                                    uphill = courseUphill + "m",
                                    downhill = courseDownhill + "m",
                                    thumbnail = courseThumbnailUrl,
                                    location = courseLocation,
                                    score = "",
                                    review_cnt = ""
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

    // 마이페이지 프로필/통계 로드
    fun profileLoad(
        completion: (RESPONSE_STATUS, Profile?) -> Unit
    ) {
        val call = iRetrofit?.profileLoad() ?: return

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
                        //val parsedProfileDataArray = ArrayList<Course>()
                        val body = it.asJsonObject
                        val results = body.getAsJsonObject("data")
                        val message = body.get("message")

                        when (val status = body.get("status").asString) {
                            "OK" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                val profilePicture = results.get("picture").asString ?: "no_image"
                                val profileNickname = results.get("nickname").asString
                                //val profileHeightWeight = results.get("user_height_weight").asString
                                val profileHeight = results.get("user_height").asString
                                val profileWeight = results.get("user_weight").asString
                                val profileTotalDistance = results.get("total_distance").asString
                                val profileAvgDistance = results.get("avg_distance").asString
                                val profileMaxDistance = results.get("max_distance").asString
                                val profileTotalTime = results.get("total_total_time").asString
                                val profileAvgTime = results.get("avg_total_time").asString
                                val profileMaxTime = results.get("max_total_time").asString
                                val profileTotalMTime = results.get("total_moving_time").asString
                                val profileAvgMTime = results.get("avg_moving_time").asString
                                val profileMaxMTime = results.get("max_moving_time").asString
                                val profileTotalUphill = results.get("total_total_uphill").asString
                                val profileMaxUphill = results.get("avg_total_uphill").asString
                                val profileAvgUphill = results.get("max_total_uphill").asString
                                val profileTotalDownhill = results.get("total_total_downhill").asString
                                val profileMaxDownhill = results.get("avg_total_downhill").asString
                                val profileAvgDownhill = results.get("max_total_downhill").asString
                                val profileMaxHeight = results.get("max_height").asString
                                val profileAvgHeight = results.get("avg_height").asString
                                val profileMaxSpeed = results.get("max_speed").asString
                                val profileAvgSpeed = results.get("avg_speed").asString
                                val profileMaxPace = results.get("max_pace").asString
                                val profileAvgPace = results.get("avg_pace").asString
                                val profileTotalCalories = results.get("total_calories").asString
                                val profileAvgCalores = results.get("avg_calories").asString

                                val profileItem = Profile(
                                    user_id = "",
                                    picture = profilePicture,
                                    nickname = profileNickname,
                                    user_height = profileHeight,
                                    user_weight = profileWeight,
                                    total_distance = profileTotalDistance,
                                    avg_distance = profileAvgDistance,
                                    max_distance = profileMaxDistance,
                                    total_total_time = profileTotalTime,
                                    max_total_time = profileMaxTime,
                                    avg_total_time = profileAvgTime,
                                    total_moving_time = profileTotalMTime,
                                    max_moving_time = profileMaxMTime,
                                    avg_moving_time = profileAvgMTime,
                                    max_height = profileMaxHeight,
                                    avg_height = profileAvgHeight,
                                    max_speed = profileMaxSpeed,
                                    avg_speed = profileAvgSpeed,
                                    max_pace = profileMaxPace,
                                    avg_pace = profileAvgPace,
                                    total_total_uphill = profileTotalUphill,
                                    avg_total_uphill = profileAvgUphill,
                                    max_total_uphill = profileMaxUphill,
                                    avg_total_downhill = profileAvgDownhill,
                                    max_total_downhill = profileMaxDownhill,
                                    total_total_downhill = profileTotalDownhill,
                                    total_calories = profileTotalCalories,
                                    avg_calories = profileAvgCalores
                                )
                                completion(RESPONSE_STATUS.OKAY, profileItem)
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

    //홈화면 목록 업데이트
    fun homeLoad(
        lat: Double, lon: Double,
        completion: (RESPONSE_STATUS, ArrayList<Home>?, ArrayList<Home>?, ArrayList<Mountain>?, ArrayList<Mountain>?) -> Unit
    ) {
        val call = iRetrofit?.homeLoad(lat = lat, lon = lon) ?: return

        call.enqueue(object : retrofit2.Callback<JsonElement> {
            // 응답 실패시
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "RetrofitManager - onFailure() called / t: $t")
                completion(RESPONSE_STATUS.FAIL, null, null, null, null)
            }

            // 응답 성공시
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d(TAG, "RetrofitManager - onResponse() called / response : ${response.body()}")

                if(response.isSuccessful) {
                    response.body()?.let {
                        val parsedCourseDataArray1 = ArrayList<Home>()
                        val parsedCourseDataArray2 = ArrayList<Home>()
                        val parsedCourseDataArray3 = ArrayList<Mountain>()
                        val parsedCourseDataArray4 = ArrayList<Mountain>()
                        val body = it.asJsonObject
                        //val results = body.getAsJsonArray("data")

                        val data = body.getAsJsonObject("data")
                        val rec = data.getAsJsonArray("recommendCourse")
                        val hot = data.getAsJsonArray("hotCourse")
                        val hotM = data.getAsJsonArray("hotMountain")
                        val nearM = data.getAsJsonArray("nearMountain")

                        val message = body.get("message")

                        when (val status = body.get("status").asString) {
                            "OK" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                rec.forEach { resultItem ->
                                    val resultItemObject = resultItem.asJsonObject
                                    val courseId = resultItemObject.get("id").asInt
                                    val courseName = resultItemObject.get("name").asString
                                    val courseDistance = resultItemObject.get("distance").asString
                                    val courseMovingTime = resultItemObject.get("moving_time").asString
                                    //val courseElevation = resultItemObject.get("ele_dif").asString
                                    val courseDifficulty = resultItemObject.get("difficulty").asString
                                    val courseThumbnailUrl = resultItemObject.get("thumbnail").asString

                                    val courseItem = Home(
                                        id = courseId,
                                        name = courseName,
                                        distance = courseDistance,
                                        moving_time = courseMovingTime,
                                        //ele_dif = courseElevation + "m",
                                        thumbnail = courseThumbnailUrl,
                                        difficulty = courseDifficulty
                                    )
                                    parsedCourseDataArray1.add(courseItem)
                                }
                                hot.forEach { resultItem ->
                                    val resultItemObject = resultItem.asJsonObject
                                    val courseId = resultItemObject.get("id").asInt
                                    val courseName = resultItemObject.get("name").asString
                                    val courseDistance = resultItemObject.get("distance").asString
                                    val courseMovingTime = resultItemObject.get("moving_time").asString
                                    //val courseElevation = resultItemObject.get("ele_dif").asString
                                    val courseDifficulty = resultItemObject.get("difficulty").asString
                                    val courseThumbnailUrl = resultItemObject.get("thumbnail").asString

                                    val courseItem = Home(
                                        id = courseId,
                                        name = courseName,
                                        distance = courseDistance,
                                        moving_time = courseMovingTime,
                                        //ele_dif = courseElevation + "m",
                                        thumbnail = courseThumbnailUrl,
                                        difficulty = courseDifficulty
                                    )
                                    parsedCourseDataArray2.add(courseItem)
                                }
                                hotM.forEach { resultItem ->
                                    val resultItemObject = resultItem.asJsonObject
                                    val courseId = resultItemObject.get("id").asInt
                                    val courseName = resultItemObject.get("name").asString
                                    val courseThumbnailUrl = resultItemObject.get("thumbnail").asString

                                    val courseItem = Mountain(
                                        id = courseId,
                                        name = courseName,
                                        thumbnail = courseThumbnailUrl,
                                        location="",
                                        height=""
                                    )
                                    parsedCourseDataArray3.add(courseItem)
                                }
                                nearM.forEach { resultItem ->
                                    val resultItemObject = resultItem.asJsonObject
                                    val courseId = resultItemObject.get("id").asInt
                                    val courseName = resultItemObject.get("name").asString
                                    val courseThumbnailUrl = resultItemObject.get("thumbnail").asString

                                    val courseItem = Mountain(
                                        id = courseId,
                                        name = courseName,
                                        thumbnail = courseThumbnailUrl,
                                        location="",
                                        height=""
                                    )
                                    parsedCourseDataArray4.add(courseItem)
                                }
                                completion(RESPONSE_STATUS.OKAY, parsedCourseDataArray1, parsedCourseDataArray2, parsedCourseDataArray3, parsedCourseDataArray4)
                            }
                            "NO_CONTENT" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                completion(RESPONSE_STATUS.NO_CONTENT, null, null, null, null)
                            }
                            "BAD_REQUEST" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                completion(RESPONSE_STATUS.BAD_REQUEST, null, null, null, null)
                            }
                            "UNAUTHORIZED" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                completion(RESPONSE_STATUS.UNAUTHORIZED, null, null, null, null)
                            }
                        }
                    }
                }
                else {
                    Log.d(TAG, "RetrofitManager - onResponse() called / 404 NOT FOUND")
                    completion(RESPONSE_STATUS.NOT_FOUND, null, null, null, null)
                }
            }
        })
    }
    
    //추천 등산로 더보기
    fun recCourseList(
        completion: (RESPONSE_STATUS, ArrayList<CourseItem>?) -> Unit
    ) {
        val call = iRetrofit?.recCourseList() ?: return

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
                        val result1 = body.getAsJsonObject("data")
                        val results = result1.getAsJsonArray("content")
                       // val results = body.getAsJsonArray("data")
                        val message = body.get("message")

                        when (val status = body.get("status").asString) {
                            "OK" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                results.forEach { resultItem ->
                                    val resultItemObject = resultItem.asJsonObject
                                    val courseId = resultItemObject.get("id").asInt
                                    val courseName = resultItemObject.get("name").asString
                                    val courseDistance = resultItemObject.get("distance").asString
                                    val courseMovingTime = resultItemObject.get("moving_time").asString
                                    val courseElevation = resultItemObject.get("ele_dif").asString
                                    val courseDifficulty = resultItemObject.get("difficulty").asString
                                    val courseThumbnailUrl = resultItemObject.get("thumbnail").asString

                                    val courseItem = CourseItem(
                                        id = courseId,
                                        name = courseName,
                                        distance = courseDistance,
                                        moving_time = courseMovingTime,
                                        ele_dif = courseElevation + "m",
                                        thumbnail = courseThumbnailUrl,
                                        difficulty = courseDifficulty
                                    )
                                    parsedCourseDataArray.add(courseItem)
                                }
                                completion(RESPONSE_STATUS.OKAY, parsedCourseDataArray)
                            }
//                            "NO_CONTENT" -> {
//                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
//                                completion(RESPONSE_STATUS.NO_CONTENT, null)
//                            }
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

    //인기 등산로 더보기
    fun hotCourseList(
        completion: (RESPONSE_STATUS, ArrayList<CourseItem>?) -> Unit
    ) {
        val call = iRetrofit?.hotCourseList() ?: return

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
                        val result1 = body.getAsJsonObject("data")
                        val results = result1.getAsJsonArray("content")
                        // val results = body.getAsJsonArray("data")
                        val message = body.get("message")

                        when (val status = body.get("status").asString) {
                            "OK" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                results.forEach { resultItem ->
                                    val resultItemObject = resultItem.asJsonObject
                                    val courseId = resultItemObject.get("id").asInt
                                    val courseName = resultItemObject.get("name").asString
                                    val courseDistance = resultItemObject.get("distance").asString
                                    val courseMovingTime = resultItemObject.get("moving_time").asString
                                    val courseElevation = resultItemObject.get("ele_dif").asString
                                    val courseDifficulty = resultItemObject.get("difficulty").asString
                                    val courseThumbnailUrl = resultItemObject.get("thumbnail").asString

                                    val courseItem = CourseItem(
                                        id = courseId,
                                        name = courseName,
                                        distance = courseDistance,
                                        moving_time = courseMovingTime,
                                        ele_dif = courseElevation + "m",
                                        thumbnail = courseThumbnailUrl,
                                        difficulty = courseDifficulty
                                    )
                                    parsedCourseDataArray.add(courseItem)
                                }
                                completion(RESPONSE_STATUS.OKAY, parsedCourseDataArray)
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

    //인기 산 더보기
    fun hotMountainList(
        completion: (RESPONSE_STATUS, ArrayList<Mountain>?) -> Unit
    ) {
        val call = iRetrofit?.hotMountainList() ?: return

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
                        val parsedCourseDataArray = ArrayList<Mountain>()
                        val body = it.asJsonObject
                        val result1 = body.getAsJsonObject("data")
                        val results = result1.getAsJsonArray("content")
                        // val results = body.getAsJsonArray("data")
                        val message = body.get("message")

                        when (val status = body.get("status").asString) {
                            "OK" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                results.forEach { resultItem ->
                                    val resultItemObject = resultItem.asJsonObject
                                    val courseId = resultItemObject.get("id").asInt
                                    val courseName = resultItemObject.get("name").asString
                                    //val courseDistance = resultItemObject.get("distance").asString
                                    //val courseMovingTime = resultItemObject.get("moving_time").asString
                                    //val courseElevation = resultItemObject.get("ele_dif").asString
                                    //val courseDifficulty = resultItemObject.get("difficulty").asString
                                    val location = resultItemObject.get("location").asString
                                    val height = resultItemObject.get("height").asString
                                    val courseThumbnailUrl = resultItemObject.get("thumbnail").asString

                                    val courseItem = Mountain(
                                        id = courseId,
                                        name = courseName,
                                        //distance = "",
                                        //moving_time = "",
                                        //ele_dif = "",
                                        thumbnail = courseThumbnailUrl,
                                        location = location,
                                        height = height
                                        //difficulty = ""
                                    )
                                    parsedCourseDataArray.add(courseItem)
                                }
                                completion(RESPONSE_STATUS.OKAY, parsedCourseDataArray)
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

    // 등산기록 목록
    fun recordList(
        completion: (RESPONSE_STATUS, ArrayList<RecordItem>?) -> Unit
    ) {
        val call = iRetrofit?.recordList() ?: return

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
                        val parsedCourseDataArray = ArrayList<RecordItem>()
                        val body = it.asJsonObject
                        val result1 = body.getAsJsonObject("data")
                        val results = result1.getAsJsonArray("content")
                        // val results = body.getAsJsonArray("data")
                        val message = body.get("message")

                        when (val status = body.get("status").asString) {
                            "OK" -> {
                                Log.d(TAG, "RetrofitManager - onResponse() called / status: $status, message: $message")
                                results.forEach { resultItem ->
                                    val resultItemObject = resultItem.asJsonObject
                                    val recordId = resultItemObject.get("id").asInt
                                    val recordFileName = resultItemObject.get("name").asString
                                    val recordDistance = resultItemObject.get("distance").asString
                                    val recordTime = resultItemObject.get("time").asString
                                    val recordElevation = resultItemObject.get("height").asString
                                    val recordCalorie = resultItemObject.get("calorie").asString
                                    val recordThumbnailUrl = resultItemObject.get("thumbnail").asString
                                    val recordDate = resultItemObject.get("date").asString

                                    val recordItem = RecordItem(
                                        id = recordId,
                                        fileName = recordFileName,
                                        distance = recordDistance,
                                        time = recordTime,
                                        height = recordElevation,
                                        thumbnail = recordThumbnailUrl,
                                        calorie = recordCalorie,
                                        date = recordDate
                                    )
                                    parsedCourseDataArray.add(recordItem)
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


    // 찜 목록 토글
    fun toggleFavorite(
        favorite: Favorite,
        completion: (RESPONSE_STATUS) -> Unit
    ) {
        val call = iRetrofit?.toggleFavorite(body = favorite) ?: return

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
                        val body = it.asJsonObject
                        val message = body.get("message")

                        when (val status = body.get("status").asString) {
                            "ADDED" -> { // 추가 성공
                                completion(RESPONSE_STATUS.OKAY)
                            }
                            "REMOVED" -> { // 제거 성공
                                completion(RESPONSE_STATUS.DELETE_SUCCESS)
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

    // 레코드 업로드
    fun uploadRecord(
        record: Record,
        completion: (RESPONSE_STATUS) -> Unit
    ) {
        val call = iRetrofit?.uploadRecord(body = record) ?: return

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
                        val body = it.asJsonObject
                        val message = body.get("message")

                        when (val status = body.get("status").asString) {
                            "OK" -> { // 등록 성공
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
}
