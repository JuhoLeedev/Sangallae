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

    private var wayPoints: MutableList<WayPoint> = ArrayList()
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
    val gpx: GPX? = null

    /**
     * GPX File Functions
     */
    @Throws(IOException::class)
    fun read(path: String?): GPX {
        return GPX.read(path)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(IOException::class, InterruptedException::class)
    fun addWayPoint(lat: Double, lon: Double, ele: Double) {
        val newWayPoint = WayPoint.builder().lat(lat).lon(lon).ele(ele).time(ZonedDateTime.now().plusHours(9)).build()
        if (startTime == null) {
            startTime = LocalDateTime.now()
        }
        if (wayPoints.size > 0) {
            val lastWayPoint = wayPoints[wayPoints.size - 1]
            movingDistance += Geoid.WGS84.distance(lastWayPoint, newWayPoint).toDouble()
        }
        wayPoints.add(newWayPoint)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(IOException::class)
    fun saveGPX(path: String?) {
        val gpx = GPX.builder()
                .addTrack { track: Track.Builder ->
                    track
                            .addSegment { segment: TrackSegment.Builder -> segment.points(wayPoints) }
                }
                .build()


        GPX.write(gpx, Paths.get(path))
    }

    @Throws(IOException::class)
    fun getWayPoints(gpx: GPX): List<WayPoint> {
        wayPoints = gpx.tracks()
                .flatMap { obj: Track -> obj.segments() }
                .flatMap { obj: TrackSegment -> obj.points() }
                .collect(Collectors.toList())
        return wayPoints
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
    fun getTotalDistance(gpx: GPX): Double {
        return gpx.tracks()
                .flatMap { obj: Track -> obj.segments() }
                .findFirst()
                .map { obj: TrackSegment -> obj.points() }.orElse(Stream.empty())
                .collect(Geoid.WGS84.toPathLength())
                .toDouble()
    }

    fun getLeftDistance(gpx: GPX): Double {
        return getTotalDistance(gpx) - movingDistance
    }

    fun getProgress(gpx: GPX): String {
        return Math.round(movingDistance / getTotalDistance(gpx) * 100).toString() + "%"
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

    /**
     * Convert Functions
     */
    fun convertSecondToTime(second: Long): String {
        var second = second
        val hour = second / 3600
        second %= 3600
        val minute = second / 60
        second %= 600
        return String.format("%02d:%02d:%02d", hour, minute, second)
    }

    fun convertMeterToKillo(distance: Double): String {
        return (Math.round(distance * 100 / 1000.0) / 100.0).toString() + "km"
    }

    val speed: Double
        get() = movingDistance / movingTimeSec

    fun getLeftTime(gpx: GPX): Double {
        return getLeftDistance(gpx) / speed
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getExpectedTime(gpx: GPX): String {
        var second = Math.round(getLeftTime(gpx))
        val hour = second / 3600
        second %= 3600
        val minute = second / 60
        second %= 600
        val localDateTime = LocalDateTime.now().plusHours(hour).plusMinutes(minute)
        var hour1 = localDateTime.hour
        val minute1 = localDateTime.minute
        val ampm = if (hour1 < 12) "AM " else "PM "
        hour1 = if (hour1 > 12) hour1 % 12 else hour1
        return ampm + hour1 + "H " + minute1 + "M"
    }
}