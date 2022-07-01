package pt.ipleiria.estg.dei.pi.mymultiprev

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.receiver.AlarmReceiverN
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.SharedPreferencesRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import java.io.File
import java.time.Instant
import java.util.*
import kotlin.time.ExperimentalTime


class NotificationsManager() {


    @RequiresApi(Build.VERSION_CODES.O)
    fun addAlarm(context: Context, na_instant: String, na_id: String, na_drugName: String) {
        writeLog(context, "NOTIFICATIONS", "addAlarm")

        val sharedPreferences = SharedPreferencesRepository(context)

//        sharedPreferences.getNextAlarms()?.forEach {
//            val instant = it.split(";")[0].toLong()
//            val id = it.split(";")[1]
//            val drugName = it.split(";")[2]
//            if (instant == na_instant.toLong() && id == na_id && drugName == na_drugName) {
//                writeLog(context,"NOTIFICATIONS", "Alarm already exists")
//                return
//            }
//        }
        sharedPreferences.addAlarm("$na_instant;$na_id;$na_drugName")
        writeLog(context, "NOTIFICATIONS", "Calling update next")
        updateNext(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun removeAlarms(context: Context, prescId: String) {
        val sharedPreferences = SharedPreferencesRepository(context)
        sharedPreferences.removeAllAlarm(prescId)
        updateNext(context)
    }

    fun removeAll(context: Context) {
        val sharedPreferences = SharedPreferencesRepository(context)
        sharedPreferences.clearAlarms()
        removeAlarmSet(context)
        writeLog(context, "NOTIFICATIONS", "WARNING!!! - ALL ALARMS CLEARED")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun removeAlarm(context: Context, instant: String, id: String) {
        writeLog(context, "NOTIFICATIONS", "REMOVING ALARM $instant;$id")

        val sharedPreferences = SharedPreferencesRepository(context)
        sharedPreferences.removeAlarm("$instant;$id")

        writeLog(context, "NOTIFICATIONS", "Calling update next")
        updateNext(context)
    }

    @OptIn(ExperimentalTime::class)
    @RequiresApi(Build.VERSION_CODES.O)
    fun addAlarms(context: Context, prescriptionItem: PrescriptionItem, drug: Drug) {
        writeLog(context, "NOTIFICATIONS", "INSIDE ADD ALARMS")

        if (prescriptionItem.drug != drug.id ||
            prescriptionItem.nextIntake == null ||
            prescriptionItem.intakesTakenCount == null ||
            prescriptionItem.expectedIntakeCount == null
        ) {
            writeLog(context, "NOTIFICATIONS", "ERROR")
            return
        }
        val sharedPreferences = SharedPreferencesRepository(context)

        var intakesTakenCount = prescriptionItem.intakesTakenCount!!
        val predictIntakes = prescriptionItem.intakesTakenCount!!

        var instant = prescriptionItem.nextIntake!!.toInstant(Constants.TIME_ZONE)

//        while (intakesTakenCount <= prescriptionItem.expectedIntakeCount!!) {
//        writeLog(context,"NOTIFICATIONS", "WHILE:  $intakesTakenCount to $predictIntakes")
        val nowInstant = Clock.System.now()
        var newAlarmsCount = 0
        while (intakesTakenCount <= predictIntakes) {
            if (instant >= nowInstant) {
                val alarmToAdd =
                    "${instant.toEpochMilliseconds()};${prescriptionItem.id};${drug.commercialName}"
                sharedPreferences.addAlarm(alarmToAdd)
                writeLog(context, "NOTIFICATIONS", "$alarmToAdd ADDED TO SHARED PREFERENCES")
                newAlarmsCount++
            }
//            instant = instant.plus(prescriptionItem.frequency, DateTimeUnit.HOUR)
            instant = instant.plus(prescriptionItem.frequency, DateTimeUnit.HOUR)
//            instant = instant.plus(prescriptionItem.frequency, DateTimeUnit.MINUTE)
            intakesTakenCount++
        }

        writeLog(context, "NOTIFICATIONS", "LEAVING ADD ALARMS - $newAlarmsCount NEW ALARMS ADDED")
        writeLog(
            context,
            "NOTIFICATIONS",
            "LEAVING ADD ALARMS - ${sharedPreferences.getNextAlarms()?.size} TOTAL"
        )
        writeLog(
            context,
            "NOTIFICATIONS",
            "LEAVING ADD ALARMS - ${sharedPreferences.getNextAlarms()}"
        )
        if (newAlarmsCount > 0) updateNext(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun removeExpired(context: Context) {
        writeLog(context, "NOTIFICATIONS", "removing expired")

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
        writeLog(context, "NOTIFICATIONS", "Updating alarms")
//        removeExpired(context)
        val sharedPreferences = SharedPreferencesRepository(context)
        val nextAlarms = sharedPreferences.getNextAlarms()?.toList()
        if (nextAlarms.isNullOrEmpty()) {
            writeLog(context, "NOTIFICATIONS", "no alarms found")
            return
        }
        var alarmID = ""
        var nextAlarmName = ""
        var nextAlarmTime = Long.MAX_VALUE
        writeLog(context, "NOTIFICATIONS", nextAlarms.toString())
        nextAlarms.forEach {
            val alarmParts = it.split(";")
            writeLog(context, "NOTIFICATIONS", "foreach - $alarmParts")

            val instant = alarmParts[0].toLong()
            val id = alarmParts[1]
            val drugName = alarmParts[2]

//            if (instant < Instant.now().toEpochMilli()) {
//                writeLog(context,"NOTIFICATIONS", "alarm outdated")
//                writeLog(context,"NOTIFICATIONS", "now: ${Instant.now()}")
//                writeLog(context,"NOTIFICATIONS", "alarm: ${Instant.ofEpochMilli(instant)}")
////                sharedPreferences.removeAllAlarm(id)
//                sharedPreferences.removeAlarm(id)
//            } else {
            if (instant < nextAlarmTime) {
                writeLog(context, "NOTIFICATIONS", "alarm newer then the previous")
                nextAlarmTime = instant
                alarmID = id
                nextAlarmName = drugName
            }
//            }
        }
        if (alarmID.isNotEmpty() && nextAlarmName.isNotEmpty()) {
            setAlarm(context, nextAlarmTime, alarmID, nextAlarmName)
            writeLog(
                context,
                "NOTIFICATIONS",
                "next alarm set - $nextAlarmTime;$alarmID;$nextAlarmName"
            )
        } else {
            removeAlarmSet(context)
        }
    }

    private fun removeAlarmSet(context: Context) {

        val sharedPreferences = SharedPreferencesRepository(context)
        val nextAlarms = sharedPreferences.getNextAlarms()
        if (nextAlarms == null || nextAlarms.size < 1) {
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiverN::class.java)

        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.cancel(pendingIntent)
    }

    private fun setAlarm(context: Context, instant: Long, id: String, drugName: String) {

//        val uniqueId = (Date().time / 1000L % Int.MAX_VALUE).toInt()
        val timeSec = System.currentTimeMillis()
        writeLog(context, "NOTIFICATIONS", "currentTime: $timeSec : ${Date(timeSec)}")
        writeLog(context, "NOTIFICATIONS", "nextAlarmTime: $instant : ${Date(instant)}")

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiverN::class.java)
        intent.putExtra(Constants.NOTIFICATIONS_DRUG_NAME, drugName)
        intent.putExtra(Constants.NOTIFICATIONS_ALARM_ID, id)
        intent.putExtra(Constants.NOTIFICATIONS_ALARM_INSTANT, instant.toString())


        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.cancel(pendingIntent)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, instant, pendingIntent)
    }

    fun writeLog(context: Context, code: String, text: String) {
        try {
            val filename = "NotificationsLog.txt"
            val outputFile = File(context.filesDir, filename)
            Log.d("TESTE", outputFile.absolutePath)
            outputFile.createNewFile()
            outputFile.appendText("$code:\t$text\n")
            Log.d("TESTE", "Permission: ${checkPermission(context)}")
            Log.d("TESTE", "Success: ${outputFile.path}")
        } catch (e: Exception) {
            Log.d("TESTE", "ERROR: $e")
        }
    }


    private fun checkPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val readCheck =
                ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE)
            val writeCheck =
                ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE)
            readCheck == PackageManager.PERMISSION_GRANTED && writeCheck == PackageManager.PERMISSION_GRANTED
        }
    }

}

