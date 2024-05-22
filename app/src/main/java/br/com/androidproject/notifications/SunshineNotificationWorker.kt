package br.com.androidproject.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import br.com.androidproject.res.getWeatherIcon
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import fetchWeather

class SunshineNotificationWorker(appContext: Context, workerParams: WorkerParameters)
    : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val context = applicationContext
        val locationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                val lastLocation = Tasks.await(locationClient.lastLocation)
                lastLocation?.let { location ->
                    val weatherData = fetchWeather(location.latitude, location.longitude)
                    weatherData?.let {
                        if (it.description.lowercase().contains("clear")) {
                            showSunshineNotification(context, it.cityName)
                        }
                    }
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
                return Result.failure()
            }
        } else {
            return Result.failure()
        }

        return Result.success()
    }

    private fun showSunshineNotification(context: Context, cityName: String) {
        val channelId = "sunshine_notification_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Sunshine Notifications", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Great Day Outside!")
            .setContentText("It's sunny in $cityName Go outside and enjoy.")
            .setSmallIcon(getWeatherIcon("sunny"))
            .build()

        notificationManager.notify(1, notification)
    }
}
