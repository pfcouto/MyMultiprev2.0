package pt.ipleiria.estg.dei.pi.mymultiprev.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.daos.AlarmDao
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.DrugRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.PrescriptionItemsRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.SharedPreferencesRepository
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver :
    BroadcastReceiver() {
    @Inject
    lateinit var prescriptionItemsRepository: PrescriptionItemsRepository

    @Inject
    lateinit var drugRepository: DrugRepository

    @Inject
    lateinit var sharedPreferencesRepository: SharedPreferencesRepository

    @Inject
    lateinit var alarmDao: AlarmDao


    override fun onReceive(context: Context, intent: Intent) {

        val title = intent.getStringExtra("title")
        val message = intent.getStringExtra("message")
        val uniqueId = intent.getIntExtra("uniqueId", 0)

        showNotification(context, title!!, message!!, uniqueId)


    }
}


private fun showNotification(context: Context, title: String, desc: String, uniqueId: Int) {
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channelId = "message_channel"
    val channelName = "message_name"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        manager.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, channelId)
        .setContentTitle(title)
        .setContentText(desc)
        .setSmallIcon(R.drawable.ic_baseline_notifications_24)
    manager.notify(uniqueId, builder.build())
}
