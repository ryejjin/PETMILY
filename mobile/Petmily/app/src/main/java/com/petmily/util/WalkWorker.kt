package com.petmily.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.petmily.R
import com.petmily.presentation.view.walk.WalkFragment
import kotlinx.coroutines.delay

private const val TAG = "Fetmily_WalkWorker"
class WalkWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    private val locationManager by lazy {
        applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    private lateinit var location1: Location
    private lateinit var location2: Location

    private var totalDist = 0F
    private var time = 0

    private val handler = Handler(Looper.getMainLooper())

    private val notificationContent = "산책 시작!"

    override suspend fun doWork(): Result {
        Log.d(TAG, "doWork: 산책 시작")

        val foregroundInfo = createForegroundInfo(notificationContent)
        setForegroundAsync(foregroundInfo)

        handler.post {
            getProviders()
        }

        simulateLocationUpdates()

        return Result.success()
    }

    private fun createForegroundInfo(progress: String): ForegroundInfo {
        createNotificationChannel()

        val title = applicationContext.getString(R.string.app_name)

        val notification = NotificationCompat.Builder(applicationContext, FOREGROUND_SERVICE_ID.toString())
            .setContentTitle("지금은 산책중입니다~")
            .setTicker(title)
            .setContentText(progress)
            .setSmallIcon(R.drawable.main_logo_small)
            .setOngoing(true)
            .build()

        return ForegroundInfo(FOREGROUND_SERVICE_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager: NotificationManager =
                applicationContext.getSystemService(NotificationManager::class.java)
            val serviceChannel = NotificationChannel(
                FOREGROUND_SERVICE_ID.toString(),
                applicationContext.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_LOW,
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private suspend fun simulateLocationUpdates() {
        while (true) {
            delay(1000) // Simulate delay between updates
            if (this::location1.isInitialized) {
                totalDist += location1.distanceTo(location2)
                WalkFragment.walkDist = totalDist

                Log.d(TAG, "simulateLocationUpdates: 이동거리 $totalDist")
            }
            if (this::location2.isInitialized) {
                location1 = location2
            }
            time++
            WalkFragment.walkTime = time
            updateNotification("산책 거리 : ${StringFormatUtil.distanceIntToString(totalDist.toInt())} \n산책 시간 : ${StringFormatUtil.timeIntToString(time)}")
        }
    }

    private fun updateNotification(content: String) {
        val foregroundInfo = createForegroundInfo(content)
        setForegroundAsync(foregroundInfo)
    }

    companion object {
        const val FOREGROUND_SERVICE_ID = 12345
    }

    @SuppressLint("MissingPermission")
    private fun getProviders() {
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0f,
            listener,
        )
    }

    private val listener = object : LocationListener {
        // 위치가 변경될때 호출될 method
        override fun onLocationChanged(location: Location) {
            when (location.provider) {
                LocationManager.GPS_PROVIDER -> {
                    location2 = location
                }
            }
        }

        @SuppressLint("MissingPermission")
        override fun onProviderEnabled(provider: String) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
        }
    }
}
