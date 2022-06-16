package pt.ipleiria.estg.dei.pi.mymultiprev

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.receiver.AlarmReceiverN
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.SharedPreferencesRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import java.time.Instant
import java.util.*

class NotificationsManager() {

//    fun addalarms(context:Context, prescriptionItem: PrescriptionItem){
//        prescriptionItem.
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addAlarm(context: Context, na_instant: String, na_id: String, na_drugName: String) {
        Log.d("NOTIFICATIONS", "addAlarm")

        val sharedPreferences = SharedPreferencesRepository(context)

//        sharedPreferences.getNextAlarms()?.forEach {
//            val instant = it.split(";")[0].toLong()
//            val id = it.split(";")[1]
//            val drugName = it.split(";")[2]
//            if (instant == na_instant.toLong() && id == na_id && drugName == na_drugName) {
//                Log.d("NOTIFICATIONS", "Alarm already exists")
//                return
//            }
//        }
        sharedPreferences.addAlarm("$na_instant;$na_id;$na_drugName")
        Log.d("NOTIFICATIONS", "Calling update next")
        updateNext(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun removeAlarms(context: Context, prescId: String) {
        val sharedPreferences = SharedPreferencesRepository(context)
        sharedPreferences.removeAllAlarm(prescId)
        updateNext(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addAlarms(context: Context, prescriptionItem: PrescriptionItem, drug: Drug) {
        if (prescriptionItem.drug != drug.id ||
            prescriptionItem.nextIntake == null ||
            prescriptionItem.intakesTakenCount == null ||
            prescriptionItem.expectedIntakeCount == null
        ) {
            Log.d("NOTIFICATIONS", "Error")
            return
        }
        val sharedPreferences = SharedPreferencesRepository(context)

        var intakesTakenCount = prescriptionItem.intakesTakenCount!!
        var instant = prescriptionItem.nextIntake!!.toInstant(Constants.TIME_ZONE)

//        Log.d("NOTIFICATIONS", "from $intakesTakenCount to ${prescriptionItem.expectedIntakeCount} cycle")
//        while (intakesTakenCount <= prescriptionItem.expectedIntakeCount!!) {
        while (intakesTakenCount <= prescriptionItem.intakesTakenCount!!) {
            sharedPreferences.addAlarm("${instant.toEpochMilliseconds()};${prescriptionItem.id};${drug.commercialName}")
            Log.d("NOTIFICATIONS", "alarm set to $instant")
            instant = instant.plus(prescriptionItem.frequency, DateTimeUnit.HOUR)
            intakesTakenCount++
        }
        updateNext(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun removeExpired(context: Context) {
        Log.d("NOTIFICATIONS", "removing expired")

        val sharedPreferences = SharedPreferencesRepository(context)
        val alarms = sharedPreferences.getNextAlarms() ?: return
        val newAlarmList = mutableListOf<String>()
        alarms.forEach {
            val instant = it.split(";")[0].toLong()
//            val id = it.split(";")[1]
//            val drugName = it.split(";")[2]
            if (instant > Instant.now().toEpochMilli()) {
                newAlarmList.add(it)
            }
        }
        sharedPreferences.setNextAlarms(newAlarmList.toSet())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateNext(context: Context) {
        Log.d("NOTIFICATIONS", "Updating alarms")
        removeExpired(context)
        val sharedPreferences = SharedPreferencesRepository(context)
        val nextAlarms = sharedPreferences.getNextAlarms()?.toList()
        if (nextAlarms.isNullOrEmpty()) {
            Log.d("NOTIFICATIONS", "no alarms found")
            return
        }
        var alarmID = ""
        var nextAlarmName = ""
        var nextAlarmTime = Long.MAX_VALUE
        Log.d("NOTIFICATIONS", nextAlarms.toString())
        nextAlarms.forEach {
            val alarmParts = it.split(";")
            Log.d("NOTIFICATIONS", "foreach - $alarmParts")

            val instant = alarmParts[0].toLong()
            val id = alarmParts[1]
            val drugName = alarmParts[2]

            if (instant < Instant.now().toEpochMilli()) {
                Log.d("NOTIFICATIONS", "alarm outdated")
//                sharedPreferences.removeAllAlarm(id)
                sharedPreferences.removeAlarm(id)
            } else {
                if (instant < nextAlarmTime) {
                    Log.d("NOTIFICATIONS", "alarm newer then the previous")
                    nextAlarmTime = instant
                    alarmID = id
                    nextAlarmName = drugName
                }
            }
        }
        if (alarmID.isNotEmpty() && nextAlarmName.isNotEmpty()) {
            setAlarm(context, nextAlarmTime, nextAlarmName)
            Log.d("NOTIFICATIONS", "next alarm set - $nextAlarmTime;$alarmID;$nextAlarmName")
        }
    }


    private fun setAlarm(context: Context, instant: Long, drugName: String) {

//        val uniqueId = (Date().time / 1000L % Int.MAX_VALUE).toInt()
        val timeSec = System.currentTimeMillis()
        Log.d("NOTIFICATIONS", "currentTime: $timeSec : ${Date(timeSec)}")
        Log.d("NOTIFICATIONS", "nextAlarmTime: $instant : ${Date(instant)}")

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiverN::class.java)
        intent.putExtra(Constants.NOTIFICATIONS_DRUG_NAME, drugName)

        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, instant, pendingIntent)
    }
}