package pt.ipleiria.estg.dei.pi.mymultiprev

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
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
import java.util.*


class NotificationsManager(private val context: Context) {
    private val sharedPreferences = SharedPreferencesRepository(context)

    fun addAlarm(na_instant: String, na_id: String, na_drugName: String) {
        writeLog("NOTIFICATIONS", "addAlarm")

//        sharedPreferences.getNextAlarms()?.forEach {
//            val instant = it.split(";")[0].toLong()
//            val id = it.split(";")[1]
//            val drugName = it.split(";")[2]
//            if (instant == na_instant.toLong() && id == na_id && drugName == na_drugName) {
//                writeLog("NOTIFICATIONS", "Alarm already exists")
//                return
//            }
//        }
        sharedPreferences.addAlarm("$na_instant;$na_id;$na_drugName")
        writeLog("NOTIFICATIONS", "Calling update next")
        updateNext()
    }

    fun removeAlarms(prescId: String) {
        sharedPreferences.removeAllAlarm(prescId)
        updateNext()
    }

    fun removeAll() {
        sharedPreferences.clearAlarms()
        removeAlarmSet()
        writeLog("NOTIFICATIONS", "WARNING!!! - ALL ALARMS CLEARED")
    }

    fun removeAlarm(instant: String, id: String, removeExpired: Boolean = false) {
        writeLog("NOTIFICATIONS", "REMOVING ALARM $instant;$id")

        sharedPreferences.removeAlarm("$instant;$id")

        writeLog("NOTIFICATIONS", "Calling update next")
        if (removeExpired) removeExpired(instant.toLong(), true) else updateNext()
    }

    fun addAlarms(prescriptionItem: PrescriptionItem, drug: Drug) {
        writeLog("NOTIFICATIONS", "INSIDE ADD ALARMS")

        if (prescriptionItem.drug != drug.id ||
            prescriptionItem.nextIntake == null ||
            prescriptionItem.intakesTakenCount == null ||
            prescriptionItem.expectedIntakeCount == null
        ) {
            writeLog("NOTIFICATIONS", "ERROR")
            return
        }

        var intakesTakenCount = prescriptionItem.intakesTakenCount!!
        val predictIntakes =
            if (prescriptionItem.expectedIntakeCount == null || prescriptionItem.expectedIntakeCount == 0) {
                prescriptionItem.intakesTakenCount!!
            } else {
                prescriptionItem.expectedIntakeCount!!
            }

        var instant = prescriptionItem.nextIntake!!.toInstant(Constants.TIME_ZONE)

//        while (intakesTakenCount <= prescriptionItem.expectedIntakeCount!!) {
//        writeLog("NOTIFICATIONS", "WHILE:  $intakesTakenCount to $predictIntakes")
        val nowInstant = Clock.System.now()
        var newAlarmsCount = 0
        while (intakesTakenCount <= predictIntakes) {
            if (instant >= nowInstant) {
                val alarmToAdd =
                    "${instant.toEpochMilliseconds()};${prescriptionItem.id};${drug.commercialName}"
                sharedPreferences.addAlarm(alarmToAdd)
                writeLog("NOTIFICATIONS", "$alarmToAdd ADDED TO SHARED PREFERENCES")
                newAlarmsCount++
            }
//            instant = instant.plus(prescriptionItem.frequency, DateTimeUnit.HOUR)
            instant = instant.plus(prescriptionItem.frequency, DateTimeUnit.HOUR)
//            instant = instant.plus(prescriptionItem.frequency, DateTimeUnit.MINUTE)
            intakesTakenCount++
        }

        writeLog("NOTIFICATIONS", "LEAVING ADD ALARMS - $newAlarmsCount NEW ALARMS ADDED")
        writeLog(
            "NOTIFICATIONS",
            "LEAVING ADD ALARMS - ${sharedPreferences.getNextAlarms()?.size} TOTAL"
        )
        writeLog(
            "NOTIFICATIONS",
            "LEAVING ADD ALARMS - ${sharedPreferences.getNextAlarms()}"
        )
        if (newAlarmsCount > 0) updateNext()
    }

    fun removeExpired(
        timeLimit: Long = Clock.System.now().toEpochMilliseconds(),
        updateNext: Boolean = false
    ) {
        writeLog("NOTIFICATIONS", "removing expired - before $timeLimit")

        val alarms = sharedPreferences.getNextAlarms() ?: return
        val newAlarmList = mutableListOf<String>()
        alarms.forEach {
            val instant = it.split(";")[0].toLong()
            if (instant >= timeLimit) {
                newAlarmList.add(it)
            }
        }
        sharedPreferences.setNextAlarms(newAlarmList.toSet())
        if (updateNext) updateNext()
    }

    fun updateNext() {
        writeLog("NOTIFICATIONS", "Updating alarms")
        val nextAlarms = sharedPreferences.getNextAlarms()?.toList()
        if (nextAlarms.isNullOrEmpty()) {
            writeLog("NOTIFICATIONS", "no alarms found")
            return
        }
        var alarmID = ""
        var nextAlarmName = ""
        var nextAlarmTime = Long.MAX_VALUE
        writeLog("NOTIFICATIONS", nextAlarms.toString())
        nextAlarms.forEach {
            val alarmParts = it.split(";")
            writeLog("NOTIFICATIONS", "foreach - $alarmParts")

            val instant = alarmParts[0].toLong()
            val id = alarmParts[1]
            val drugName = alarmParts[2]

            if (instant < nextAlarmTime) {
                writeLog("NOTIFICATIONS", "alarm newer then the previous")
                nextAlarmTime = instant
                alarmID = id
                nextAlarmName = drugName
            }
        }
        if (alarmID.isNotEmpty() && nextAlarmName.isNotEmpty()) {
            setAlarm(nextAlarmTime, alarmID, nextAlarmName)
            writeLog(
                "NOTIFICATIONS",
                "next alarm set - $nextAlarmTime;$alarmID;$nextAlarmName"
            )
        } else {
            removeAlarmSet()
        }
    }

    private fun removeAlarmSet() {
        val nextAlarms = sharedPreferences.getNextAlarms()
        if (nextAlarms == null || nextAlarms.size < 1) {
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiverN::class.java)

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        alarmManager.cancel(pendingIntent)
    }


    private fun setAlarm(instant: Long, id: String, drugName: String) {

//        val uniqueId = (Date().time / 1000L % Int.MAX_VALUE).toInt()
        val timeSec = System.currentTimeMillis()
        writeLog("NOTIFICATIONS", "currentTime: $timeSec : ${Date(timeSec)}")
        writeLog("NOTIFICATIONS", "nextAlarmTime: $instant : ${Date(instant)}")

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiverN::class.java)
        intent.putExtra(Constants.NOTIFICATIONS_DRUG_NAME, drugName)
        intent.putExtra(Constants.NOTIFICATIONS_ALARM_ID, id)
        intent.putExtra(Constants.NOTIFICATIONS_ALARM_INSTANT, instant.toString())


        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        alarmManager.cancel(pendingIntent)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            instant,
            pendingIntent
        )
    }

    private fun writeLog(code: String, text: String) {
        try {
            val filename = "NotificationsLog.txt"
            val outputFile = File(context.filesDir, filename)
            Log.d("TESTE", outputFile.absolutePath)
//            val created = outputFile.createNewFile()
//            if (!created) {
//                Log.d("TESTE", "FILE: ${outputFile.readText()}")
//            }
            outputFile.appendText("${Clock.System.now()}\t| $code:\t$text\n")
        } catch (e: Exception) {
            Log.d("TESTE", "ERROR: $e")
        }
    }


//    private fun checkPermission(context: Context): Boolean {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            Environment.isExternalStorageManager()
//        } else {
//            val readCheck =
//                ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE)
//            val writeCheck =
//                ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE)
//            readCheck == PackageManager.PERMISSION_GRANTED && writeCheck == PackageManager.PERMISSION_GRANTED
//        }
//    }

}

