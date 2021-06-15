package com.example.sangallae.ui.map

import android.os.Build
import androidx.annotation.RequiresApi
import io.jenetics.jpx.WayPoint
import io.jenetics.jpx.GPX
import io.jenetics.jpx.Track
import kotlin.Throws
import io.jenetics.jpx.geom.Geoid
import io.jenetics.jpx.TrackSegment
import java.io.IOException
import java.nio.file.Paths
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.ArrayList
import java.util.stream.Collectors
import java.util.stream.Stream

@RequiresApi(Build.VERSION_CODES.N)
class MyGPX {

    private var wayPointsRead: MutableList<WayPoint> = ArrayList()
    private var wayPointsWrite: MutableList<WayPoint> = ArrayList()
    var movingDistance = 0.0
        private set
    private var startTime: LocalDateTime? = null
    private var pauseTime: LocalDateTime? = null
    private var restTimeSec: Long = 0
    var movingTimeSec: Long = 0
        get() {
            field = totalTimeSec - restTimeSec
            return field
        }
        private set
    lateinit var courseGPX: GPX

    var maxEle: Double = 0.0
    var minEle: Double = 0.0
    var prevEle: Double = 0.0
    var upHill:Double = 0.0
    var downHill:Double = 0.0
    var minLat: Double = 0.0
    var maxLat: Double = 0.0
    var minLon: Double = 0.0
    var maxLon: Double = 0.0

    /**
     * GPX File Functions
     */
    @Throws(IOException::class)
    fun read(path: String?) {
        courseGPX = GPX.read(path)
    }

    fun getGPX(): GPX {
        return courseGPX
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(IOException::class, InterruptedException::class)
    fun addWayPoint(lat: Double, lon: Double, ele: Double) {
        val newWayPoint = WayPoint.builder().lat(lat).lon(lon).ele(ele).time(ZonedDateTime.now().plusHours(9)).build()

        // 기록된 데이터가 없음
        if (startTime == null) {
            startTime = LocalDateTime.now()
            prevEle = ele
            maxEle = ele
            minEle = ele
        }

        // 기록된 데이터가 있음
        else {
            if(prevEle > ele)
                downHill += (prevEle - ele)
            else if (prevEle < ele)
                upHill += (ele - prevEle)
            prevEle = ele
            val lastWayPoint = wayPointsWrite[wayPointsWrite.size - 1]
            movingDistance += Geoid.WGS84.distance(lastWayPoint, newWayPoint).toDouble()
        }

        // 좌표 추가
        wayPointsWrite.add(newWayPoint)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(IOException::class)
    fun saveGPX(path: String?) {
        val gpx = GPX.builder()
            .addTrack { track: Track.Builder ->
                track
                    .addSegment { segment: TrackSegment.Builder -> segment.points(wayPointsWrite) }
            }
            .build()


        GPX.write(gpx, Paths.get(path))
    }

    @Throws(IOException::class)
    fun getWayPoints(): List<WayPoint> {
        wayPointsRead = courseGPX.tracks()
            .flatMap { obj: Track -> obj.segments() }
            .flatMap { obj: TrackSegment -> obj.points() }
            .collect(Collectors.toList())
        return wayPointsRead
    }

    fun printGPX(wayPoints: List<WayPoint>) {
        for (wayPoint in wayPoints) {
            print("latitude = " + wayPoint.latitude)
            print(", longitude = " + wayPoint.longitude)
            print(", elevation = " + wayPoint.elevation.get())
            println(", time = " + wayPoint.time.get())
        }
    }

    /**
     * Distance Functions
     */
    fun getTotalDistance(): Double {
        return courseGPX.tracks()
            .flatMap { obj: Track -> obj.segments() }
            .findFirst()
            .map { obj: TrackSegment -> obj.points() }.orElse(Stream.empty())
            .collect(Geoid.WGS84.toPathLength())
            .toDouble()
    }

    fun getLeftDistance(): Double {
        return getTotalDistance() - movingDistance
    }

    fun getProgress(): Long {
        return Math.round(movingDistance / getTotalDistance() * 100)
    }

    /**
     * Time Functions
     */
    val totalTimeSec: Long
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            val endTime = LocalDateTime.now()
            return Duration.between(startTime, endTime).seconds
        }

    @RequiresApi(Build.VERSION_CODES.O)
    fun pause() {
        pauseTime = LocalDateTime.now()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun restart() {
        restTimeSec += Duration.between(pauseTime, LocalDateTime.now()).seconds
    }
    fun getStartTime(): LocalDateTime?{
        return startTime
    }

    /**
     * Convert Functions
     */
    fun convertSecondToTime(second: Long): String {
        var second = second
        val hour = second / 3600
        second %= 3600
        val minute = second / 60
        second %= 60
        return String.format("%02d:%02d:%02d", hour, minute, second)
    }

    fun convertMeterToKillo(distance: Double): String {
        return (Math.round(distance * 100 / 1000.0) / 100.0).toString() + "km"
    }

    val speed
    : Double
        get() = movingDistance / movingTimeSec

    fun getLeftTime(): Double {
        return getLeftDistance() / speed
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getExpectedTime(): String {
        // 남은 시간
        var left_second = Math.round(getLeftTime())
        var left_hour = left_second / 3600
        left_second %= 3600
        var left_minute = left_second / 60
        left_second %= 60

        var day: Long = 0


        val now = LocalDateTime.now()
        if(now.minute + left_minute > 59) {
            left_hour++
            left_minute %= 60
        }

        if(now.hour + left_hour > 23){
            day++
            left_hour %= 24
        }
        val ampm = if (left_hour < 12) "오전 " else "오후 "
        left_hour = if (left_hour > 12) left_hour % 12 else left_hour
        return ampm + left_hour + "시 " + left_minute + "분"
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    fun getExpectedTime(): String {
//        var second = Math.round(getLeftTime())
//        val hour = second / 3600
//        second %= 3600
//        val minute = second / 60
//        second %= 600
//        val localDateTime = LocalDateTime.now().plusHours(hour).plusMinutes(minute)
//        var hour1 = localDateTime.hour
//        val minute1 = localDateTime.minute
//        val ampm = if (hour1 < 12) "AM " else "PM "
//        hour1 = if (hour1 > 12) hour1 % 12 else hour1
//        return ampm + hour1 + "H " + minute1 + "M"
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(InterruptedException::class)
    fun printInfo(gpx2: GPX): RecordInfo {
        return RecordInfo(
            progress = getProgress(),
            total_time = convertSecondToTime(totalTimeSec),
            moving_time = convertSecondToTime(movingTimeSec),
            moving_distance = convertMeterToKillo(movingDistance),
            left_distance = convertMeterToKillo(getLeftDistance()),
            expected_time = getExpectedTime()
        )
    }
}