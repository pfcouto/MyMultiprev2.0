package pt.ipleiria.estg.dei.pi.mymultiprev.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.datetime.Clock
import pt.ipleiria.estg.dei.pi.mymultiprev.MainActivity
import pt.ipleiria.estg.dei.pi.mymultiprev.NotificationsManager
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants

@AndroidEntryPoint
class AlarmReceiverN : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        try {
            Log.d("RECEIVING NOTIFICATIONS", "RECEIVING ALARM BY ${Clock.System.now()}")

            val drugName = intent.getStringExtra(Constants.NOTIFICATIONS_DRUG_NAME)
            val alarmID = intent.getStringExtra(Constants.NOTIFICATIONS_ALARM_ID)
            val alarmInstant = intent.getStringExtra(Constants.NOTIFICATIONS_ALARM_INSTANT)

            Log.d("RECEIVING NOTIFICATIONS", "RECEIVING ALARM $alarmInstant;$alarmID;$drugName")

            val notificationsManager = NotificationsManager(context)
            notificationsManager.writeLog(
                "RECEIVING NOTIFICATIONS",
                "RECEIVING ALARM $alarmInstant;$alarmID;$drugName AT ${Clock.System.now()}"
            )
            notificationsManager.writeLog("RECEIVING NOTIFICATIONS", "Context: $context")
            if (alarmID != null && alarmInstant != null) {
                showNotification(context, "MultiPrev - $drugName", drugName.toString())
                notificationsManager.removeAlarm(
                    alarmInstant,
                    alarmID
                )
            }

        } catch (ex: Exception) {
            Log.d("Receive Ex", "onReceive: ${ex.printStackTrace()}")
        }
    }
}


private fun showNotification(context: Context, title: String, desc: String) {
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channelId = Constants.NOTIFICATIONS_CHANNEL_ID
    val channelName = Constants.NOTIFICATIONS_CHANNEL_NAME

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        manager.createNotificationChannel(channel)
    }


    val resultIntent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, 0)


    val notification = NotificationCompat.Builder(context, channelId)
        .setContentTitle(title)
        .setContentText("Hey! It is time for you to take your $desc")
        .setSmallIcon(R.drawable.ic_baseline_notifications_24)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    val requestID = System.currentTimeMillis().toInt()
    manager.notify(requestID, notification)

}

