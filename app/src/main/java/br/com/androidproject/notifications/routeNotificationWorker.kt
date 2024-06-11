package br.com.androidproject.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import br.com.androidproject.R

class RouteNotificationWorker(appContext: Context, workerParams: WorkerParameters)
    : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val context = applicationContext

        // Receba os dados da corrida da tarefa agendada
        val distance = inputData.getString(KEY_DISTANCE) ?: ""
        val elapsedTime = inputData.getString(KEY_ELAPSED_TIME) ?: ""
        val pace = inputData.getString(KEY_PACE) ?: ""

        // Mostre a notificação
        showRouteNotification(context, distance, elapsedTime, pace)

        return Result.success()
    }

    private fun showRouteNotification(context: Context, distance: String, elapsedTime: String, pace: String) {
        val channelId = "route_notification_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Route Notifications", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationContent = "Distance: $distance meters\nElapsed Time: $elapsedTime\nPace: $pace min/km"

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Race Information")
            .setContentText(notificationContent)
            .setSmallIcon(R.drawable.ic_notification) // Altere para o ícone apropriado
            .build()

        notificationManager.notify(1, notification)
    }

    companion object {
        const val KEY_DISTANCE = "distance"
        const val KEY_ELAPSED_TIME = "elapsed_time"
        const val KEY_PACE = "pace"
    }
}
