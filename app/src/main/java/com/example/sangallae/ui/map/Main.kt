package com.example.sangallae.ui.map

import android.os.Build
import androidx.annotation.RequiresApi
import io.jenetics.jpx.GPX
import java.io.IOException

object Main {
    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(IOException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val read_path = "C:\\test.gpx"
        val write_path = "write.gpx"

        val myGPX1 = MyGPX()

        println("------ test readGPX ------")
        val gpx = myGPX1.read(read_path)
        val wayPoints = myGPX1.getWayPoints(gpx)

        myGPX1.printGPX(wayPoints)
        println("total distance: " + myGPX1.convertMeterToKillo(myGPX1.getTotalDistance(gpx)))

        val myGPX2 = MyGPX()
        val gpx2 = myGPX2.read(read_path)

        println("------ test addWayPoint & getDistance & getTotalTime ------")
        myGPX2.addWayPoint(37.43458, 127.19387666666667, 357.4)
        printInfo(myGPX2, gpx2)

        println("\n ------------")
        myGPX2.addWayPoint(37.434655, 127.19387, 355.9)
        printInfo(myGPX2, gpx2)

        println("\n ------------")
        myGPX2.addWayPoint(37.434725, 127.19382, 361.6)
        printInfo(myGPX2, gpx2)

        myGPX2.pause()
        println("------ pause() ------")
        Thread.sleep(10000)
        myGPX2.restart()
        println("------ restart() ------")

        println("\n ------------")
        myGPX2.addWayPoint(37.434796666666664, 127.193815, 361.5)
        printInfo(myGPX2, gpx2)

        println("\n ------------")
        myGPX2.addWayPoint(37.434873333333336, 127.19379333333333, 358.7)
        printInfo(myGPX2, gpx2)

        println("\n ------------")
        myGPX2.addWayPoint(37.44353666666667, 127.21908333333333, 358.2)
        printInfo(myGPX2, gpx2)

        println("------ test saveGPX ------")
        myGPX2.saveGPX(write_path)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(InterruptedException::class)
    private fun printInfo(myGPX2: MyGPX, gpx2: GPX) {
        println("progress: " + myGPX2.getProgress(gpx2))
        println("total time: " + myGPX2.convertSecondToTime(myGPX2.totalTimeSec))
        println("moving time: " + myGPX2.convertSecondToTime(myGPX2.movingTimeSec))
        println("moving distance: " + myGPX2.convertMeterToKillo(myGPX2.movingDistance))
        println("left distance: " + myGPX2.convertMeterToKillo(myGPX2.getLeftDistance(gpx2)))
        // m/s
        println("myGPX2.getExpectedTime(gpx2) = " + myGPX2.getExpectedTime(gpx2))
        Thread.sleep(1000)
    }
}