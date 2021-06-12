package com.example.sangallae.utils

import AWS_ACCESS_KEY
import AWS_SECRET_KEY
import android.util.Log
import com.amazonaws.AmazonServiceException
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.AmazonS3URI
import com.example.sangallae.utils.API.S3_BUCKET
import java.io.*
import java.net.MalformedURLException
import java.net.URI

class S3FileManager {
    @Volatile var  fileName: String? = null
    fun uploadGPX(path: String, filename: String) {
        object : Thread() {
            override fun run() {
                try {
                    val awsCredentials: AWSCredentials = BasicAWSCredentials(
                        AWS_ACCESS_KEY,
                        AWS_SECRET_KEY
                    )
                    val s3Client = AmazonS3Client(
                        awsCredentials,
                        Region.getRegion(Regions.AP_NORTHEAST_2)
                    )
                    try {
                        var s3Directory = "save_test/"
                        val file: File = File(path)
                        s3Client.putObject(S3_BUCKET, s3Directory + filename, file)

                    } catch (e: AmazonServiceException) {
                        System.err.println(e.errorMessage)
                    }
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    fun downloadGPX(url: String, savepath: String): String? {
        object : Thread() {
            override fun run() {
                try {
                    val awsCredentials: AWSCredentials = BasicAWSCredentials(
                        AWS_ACCESS_KEY,
                        AWS_SECRET_KEY
                    )
                    val s3Client = AmazonS3Client(
                        awsCredentials,
                        Region.getRegion(Regions.AP_NORTHEAST_2)
                    )
                    try {
                        val fileToBeDownloaded = URI(url)
                        val s3URI = AmazonS3URI(fileToBeDownloaded)
                        Log.d(Constants.TAG, "s3 다운로드 테스트용입니다.")
                        Log.d(Constants.TAG, s3URI.key)
                        val s3Object = s3Client.getObject(s3URI.bucket, s3URI.key)
                        val reader = BufferedReader(
                            InputStreamReader(
                                `s3Object`.getObjectContent()
                            )
                        )
                        fileName = s3URI.key.split("/")[1]
                        Log.d(Constants.TAG, fileName!!)
                        val file = File(savepath+fileName)
                        val writer: Writer = OutputStreamWriter(FileOutputStream(file))

                        while (true) {
                            val line = reader.readLine() ?: break
                            writer.write(
                                """
            $line

            """.trimIndent()
                            )
                        }

                        writer.close()
                        Log.d(Constants.TAG, "성공적으로 저장되었습니다.")
                        //displayTextInputStream(s3Object.objectContent);
                    } catch (e: AmazonServiceException) {
                        System.err.println(e.errorMessage)
                    }
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                }
            }
        }.start()
        return fileName
    }
}