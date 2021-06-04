package com.example.sangallae.utils

class S3FileManager {
//    private fun downloadGPX(url: String, savepath: String) {
//        object : Thread() {
//            override fun run() {
//                try {
//                    val awsCredentials: AWSCredentials = BasicAWSCredentials(
//                        AWS_ACCESS_KEY,
//                        AWS_SECRET_KEY
//                    )
//                    val s3Client = AmazonS3Client(
//                        awsCredentials,
//                        Region.getRegion(Regions.AP_NORTHEAST_2)
//                    )
//                    try {
//                        val fileToBeDownloaded = URI(url)
//                        val s3URI = AmazonS3URI(fileToBeDownloaded)
//                        Log.d(Constants.TAG, "s3 다운로드 테스트용입니다.")
//                        Log.d(Constants.TAG, s3URI.key)
//                        val s3Object = s3Client.getObject(s3URI.bucket, s3URI.key)
//                        val reader = BufferedReader(
//                            InputStreamReader(
//                                `s3Object`.getObjectContent()
//                            )
//                        )
//                        val file = File(savepath)
//                        val writer: Writer = OutputStreamWriter(FileOutputStream(file))
//
//                        while (true) {
//                            val line = reader.readLine() ?: break
//                            writer.write(
//                                """
//            $line
//
//            """.trimIndent()
//                            )
//                        }
//
//                        writer.close()
//                        Log.d(Constants.TAG, "성공적으로 저장되었습니다.")
//                        //displayTextInputStream(s3Object.objectContent);
//                    } catch (e: AmazonServiceException) {
//                        System.err.println(e.errorMessage)
//                    }
//                } catch (e: MalformedURLException) {
//                    e.printStackTrace()
//                }
//            }
//        }.start()
//    }
}