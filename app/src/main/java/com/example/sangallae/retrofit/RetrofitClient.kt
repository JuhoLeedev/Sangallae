package com.example.sangallae.retrofit

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.sangallae.GlobalApplication
import com.example.sangallae.utils.API
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.concurrent.TimeUnit
import com.example.sangallae.utils.Constants.TAG
import com.example.sangallae.utils.Usage
import com.example.sangallae.utils.isJsonArray
import com.example.sangallae.utils.isJsonObject


// 싱글턴
object RetrofitClient {
    // 레트로핏 클라이언트 선언

    private var retrofitClient: Retrofit? = null
//    private lateinit var retrofitClient: Retrofit


    // 레트로핏 클라이언트 가져오기
    fun getClient(baseUrl: String, usage: Usage): Retrofit? {
        Log.d(TAG, "RetrofitClient - getClient() called")

        // okhttp 인스턴스 생성
        val client = OkHttpClient.Builder()

        // 로그를 찍기 위해 로깅 인터셉터 설정
        val loggingInterceptor = HttpLoggingInterceptor(object: HttpLoggingInterceptor.Logger{

            override fun log(message: String) {
                Log.d(TAG, "RetrofitClient - log() called / message: $message")
                when {
                    message.isJsonObject() ->
                        Log.d(TAG, JSONObject(message).toString(4))
                    message.isJsonArray() ->
                        Log.d(TAG, JSONObject(message).toString(4))
                    else -> {
                        try {
                            Log.d(TAG, JSONObject(message).toString(4))
                        } catch (e: Exception) {
                            Log.d(TAG, message)
                        }
                    }
                }
            }
        })

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        // 위에서 설정한 로깅 인터셉터를 okhttp 클라이언트에 추가한다.
        client.addInterceptor(loggingInterceptor)


        // 기본 파라매터 인터셉터 설정
        if(usage != Usage.LOGIN) {
            val baseParameterInterceptor: Interceptor = (object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    Log.d(TAG, "RetrofitClient - intercept() called")
                    // 오리지날 리퀘스트
                    val token = "Bearer ${API.ADMIN_JWT}"

                    // ?client_id=asdfadsf
                    // 쿼리 파라매터 추가하기
                    val addedRequest = chain.request().newBuilder().addHeader("Authorization", token).build()

//                val finalRequest = addedRequestBody.method(originalRequest.method, originalRequest.body)
//                    .build()


                    val response = chain.proceed(addedRequest)

                    if (response.code != 200) {
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(
                                GlobalApplication.instance,
                                "${response.code} 에러 입니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    return response
                }
            })
            // 위에서 설정한 기본파라매터 인터셉터를 okhttp 클라이언트에 추가한다.
            client.addInterceptor(baseParameterInterceptor)
        }

        // 커넥션 타임아웃
        client.connectTimeout(10, TimeUnit.SECONDS)
        client.readTimeout(10, TimeUnit.SECONDS)
        client.writeTimeout(10, TimeUnit.SECONDS)
        client.retryOnConnectionFailure(true)


        if(retrofitClient == null){
            // 레트로핏 빌더를 통해 인스턴스 생성
            retrofitClient = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                // 위에서 설정한 클라이언트로 레트로핏 클라이언트를 설정한다.
                .client(client.build())
                .build()
        }
        return retrofitClient
    }
}
