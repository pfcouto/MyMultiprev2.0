package pt.ipleiria.estg.dei.pi.mymultiprev.receiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.SharedPreferencesRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import java.time.Instant

@AndroidEntryPoint
class AlarmReceiverN : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        try {
            val drugName = intent.getStringExtra(Constants.NOTIFICATIONS_DRUG_NAME)
            showNotification(context, "MultiPrev", drugName.toString())
            updateNext(context)
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

    val builder = NotificationCompat.Builder(context, channelId)
        .setContentTitle(title)
        .setContentText("Hey! It is time for you to take your $desc")
        .setSmallIcon(R.drawable.ic_baseline_notifications_24)

    manager.notify(1, builder.build())
}

@RequiresApi(Build.VERSION_CODES.O)
private fun updateNext(context: Context) {
    val sharedPreferences = SharedPreferencesRepository(context)
    val nextAlarms = sharedPreferences.getNextAlarms() ?: return
    var alarmID = ""
    var nextAlarmName = ""
    var nextAlarmTime = Long.MAX_VALUE
    nextAlarms.forEach {
        val instant = it.split(";")[0].toLong()
        val id = it.split(";")[1]
        val drugName = it.split(";")[2]
        if (instant < Instant.now().toEpochMilli()) {
            sharedPreferences.removeAlarm(id)
        } else {
            if (instant < nextAlarmTime) {
                nextAlarmTime = instant
                alarmID = id
                nextAlarmName = drugName
            }
        }
    }
    setAlarm(context, nextAlarmTime, nextAlarmName)
}


private fun setAlarm(context: Context, instant: Long, drugName: String) {
//    val timeSec = instant
    val timeSec = System.currentTimeMillis() + 10000
    val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiverN::class.java)
    intent.putExtra(Constants.NOTIFICATIONS_DRUG_NAME,drugName)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
    alarmManager.set(AlarmManager.RTC_WAKEUP, timeSec, pendingIntent)
}