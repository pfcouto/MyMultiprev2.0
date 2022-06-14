package pt.ipleiria.estg.dei.pi.mymultiprev.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import pt.ipleiria.estg.dei.pi.mymultiprev.NotificationsManager
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants

@AndroidEntryPoint
class AlarmReceiverN : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        try {
            val drugName = intent.getStringExtra(Constants.NOTIFICATIONS_DRUG_NAME)
            showNotification(context, "MultiPrev - $drugName", drugName.toString())
            val notificationsManager = NotificationsManager()
            Log.d("NOTIFICATIONS", "Calling update next")
            notificationsManager.updateNext(context)
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