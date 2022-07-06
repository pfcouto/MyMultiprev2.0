package pt.ipleiria.estg.dei.pi.mymultiprev.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.datetime.Clock
import pt.ipleiria.estg.dei.pi.mymultiprev.MainActivity
import pt.ipleiria.estg.dei.pi.mymultiprev.NotificationsManager
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import java.io.File


@AndroidEntryPoint
class AlarmReceiverN : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        fun writeLog(code: String, text: String) {
            try {
                val filename = "NotificationsLog.txt"
                val outputFile = File(context.filesDir, filename)
                Log.d("TESTE", outputFile.absolutePath)
                outputFile.appendText("${Clock.System.now()}\t| $code:\t$text\n")
            } catch (e: Exception) {
                Log.d("TESTE", "ERROR: $e")
            }
        }

        try {
            Log.d("RECEIVING NOTIFICATIONS", "RECEIVING ALARM BY ${Clock.System.now()}")

            val drugName = intent.getStringExtra(Constants.NOTIFICATIONS_DRUG_NAME)
            val alarmID = intent.getStringExtra(Constants.NOTIFICATIONS_ALARM_ID)
            val alarmInstant = intent.getStringExtra(Constants.NOTIFICATIONS_ALARM_INSTANT)

            Log.d("RECEIVING NOTIFICATIONS", "RECEIVING ALARM $alarmInstant;$alarmID;$drugName")

            writeLog(
                "RECEIVING NOTIFICATIONS",
                "RECEIVING ALARM $alarmInstant;$alarmID;$drugName AT ${Clock.System.now()}"
            )
            writeLog(
                "RECEIVING NOTIFICATIONS",
                "NOTIFICATION ID (HASH) : ${(alarmInstant + alarmID).hashCode()}"
            )
            writeLog("RECEIVING NOTIFICATIONS", "Context: $context")

            val notificationsManager = NotificationsManager(context)
            if (alarmID != null && alarmInstant != null) {
                showNotification(
                    context,
                    alarmID,
                    alarmInstant,
                    "MultiPrev - $drugName",
                    drugName.toString()
                )
                notificationsManager.removeAlarm(
                    alarmInstant,
                    alarmID,
                    true
                )
            }

        } catch (ex: Exception) {
            Log.d("Receive Ex", "onReceive: ${ex.printStackTrace()}")
            writeLog("EXCEPTION", "onReceive: ${ex.message}")
            writeLog("EXCEPTION", "onReceive: ${ex.printStackTrace()}")
        }
    }
}


private fun showNotification(
    context: Context,
    instant: String,
    id: String,
    title: String,
    desc: String
) {
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channelId = Constants.NOTIFICATIONS_CHANNEL_ID
    val channelName = Constants.NOTIFICATIONS_CHANNEL_NAME

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        manager.createNotificationChannel(channel)
    }

    val resultIntent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        resultIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

//    val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    val notification = NotificationCompat.Builder(context, channelId)
        .setContentTitle(title)
        .setContentText("Hey! Está na hora de tomar $desc")
        .setSmallIcon(R.drawable.ic_baseline_notifications_24)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
//        .setSound(alarmSound)
//        .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
        .build()


//    val requestID = System.currentTimeMillis().toInt()
    val notificationID = (instant + id).hashCode()
    manager.notify(notificationID, notification)
}

