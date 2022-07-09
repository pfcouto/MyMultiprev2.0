package pt.ipleiria.estg.dei.pi.mymultiprev.repositories

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.datetime.Clock
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import java.io.File
import javax.inject.Inject

class SharedPreferencesRepository @Inject constructor(@ApplicationContext val context: Context) {
    private val spAlarmService =
        context.getSharedPreferences(Constants.MY_PREFS, Context.MODE_PRIVATE)
    private var spAlarmServiceEditor = spAlarmService.edit()

    private val spAuth = context.getSharedPreferences(Constants.AUTH_SP, Context.MODE_PRIVATE)
    private val spAuthEditor = spAuth.edit()

    private val spUserPreferences =
        context.getSharedPreferences(Constants.USER_PREFERENCES, Context.MODE_PRIVATE)
    private val spUserPreferencesEditor = spUserPreferences.edit()

    private fun writeLog(code: String, text: String) {
        try {
            val filename = "NotificationsLog.txt"
            val outputFile = File(context.filesDir, filename)
            Log.d("TESTE", outputFile.absolutePath)
            outputFile.appendText("${Clock.System.now()}\t| $code:\t$text\n")
        } catch (e: Exception) {
            Log.d("TESTE", "ERROR: $e")
        }
    }

    //Alarms V2
    fun setNextAlarms(nextAlarms: Set<String>) {
        spAlarmServiceEditor =
            spAlarmServiceEditor.putStringSet(Constants.SP_NEXT_ALARMS, nextAlarms)
        spAlarmServiceEditor.commit()
    }

    fun getNextAlarms(): MutableSet<String>? =
        spAlarmService.getStringSet(Constants.SP_NEXT_ALARMS, null)

    fun clearAlarms() {
        spAlarmServiceEditor = spAlarmServiceEditor.remove(Constants.SP_NEXT_ALARMS)
        spAlarmServiceEditor.commit()
    }

    fun removeAlarm(instantAndId: String) {
        val nextAlarms = getNextAlarms() ?: return
        writeLog("NOTIFICATIONS", "removeAlarm - $instantAndId")
        writeLog("NOTIFICATIONS", "removeAlarm before remove  alarms - $nextAlarms")
        var alarmToRemove: String? = null
        nextAlarms.forEach {
            val instantEach = it.split(";")[0]
            val idEach = it.split(";")[1]
            if ("$instantEach;$idEach" == instantAndId) {
                alarmToRemove = it
                return@forEach
            }
        }
        if (alarmToRemove != null) {
            val removed = nextAlarms.remove(alarmToRemove)
            if (removed) {
                writeLog("NOTIFICATIONS", "alarm removed")
            } else {
                writeLog(
                    "NOTIFICATIONS",
                    "AN ERROR OCCURRED WHILE TRYING TO REMOVE THE ALARM"
                )
            }
            spAlarmServiceEditor =
                spAlarmServiceEditor.putStringSet(Constants.SP_NEXT_ALARMS, nextAlarms)
            spAlarmServiceEditor.commit()
        }
        writeLog("NOTIFICATIONS", "removeAlarm after remove alarms - $nextAlarms")
    }

    fun removeAllAlarm(id: String) {
        val nextAlarms = getNextAlarms() ?: return
        writeLog("NOTIFICATIONS", "removeAlarm - $id")
        writeLog("NOTIFICATIONS", "removeAlarm before remove  alarms - $nextAlarms")

        var nextAlarmsClean = mutableListOf<String>()
        nextAlarms.forEach {
            if (it.split(";")[1] != id) {
                nextAlarmsClean.add(it)
            }
        }
        spAlarmServiceEditor =
            spAlarmServiceEditor.putStringSet(Constants.SP_NEXT_ALARMS, nextAlarmsClean.toSet())
        spAlarmServiceEditor.commit()
        writeLog("NOTIFICATIONS", "removeAlarm after remove alarms - $nextAlarmsClean")
    }

    fun addAlarm(newAlarm: String) {
        writeLog("NOTIFICATIONS", "Shared preferences - add alarm")

        val stringSet = spAlarmService.getStringSet(Constants.SP_NEXT_ALARMS, null)

        val newSet = mutableSetOf<String>()
        if (stringSet != null) {
            writeLog("NOTIFICATIONS", "Shared preferences - adding previous alarms")
            newSet.addAll(stringSet)
        }

        newSet.forEach {
            writeLog("NOTIFICATIONS", "Adding - Verifying Duplicated alarms")
            writeLog("NOTIFICATIONS", "$it - $newAlarm")
            if (it == newAlarm) {
                writeLog("NOTIFICATIONS", "equals found - newAlarm not being added")
                return
            }
        }

        writeLog("NOTIFICATIONS", "equals not found - newAlarm being added")
        newSet.add(newAlarm)

        spAlarmServiceEditor =
            spAlarmServiceEditor.putStringSet(Constants.SP_NEXT_ALARMS, newSet.toSet())
        spAlarmServiceEditor.commit()
        writeLog("NOTIFICATIONS", "Shared preferences - Alarm added '$newAlarm'")
    }

    //Auth
    fun getCurrentPatientId() = spAuth.getString(Constants.PATIENT_ID, "")!!

    fun getCurrentActivePrescriptionItemLayoutPreference() =
        spUserPreferences.getBoolean(Constants.ACTIVE_PRESCRIPTION_ITEMS_LAYOUT, false)

    fun setCurrentActivePrescriptionItemLayoutPreference(pref: Boolean) =
        spUserPreferencesEditor.putBoolean(Constants.ACTIVE_PRESCRIPTION_ITEMS_LAYOUT, pref).apply()

    fun saveToken(token: String) {
        spAuthEditor.putString(Constants.AUTH_TOKEN, token).apply()
    }

    fun getToken(): String? = spAuth.getString(Constants.AUTH_TOKEN, null)

    fun isLoggedIn(): Boolean = spAuth.getBoolean(Constants.FIRST_TIME_LOGIN, false)

    fun deleteAll() {
        spAuthEditor.clear().commit()
        spAlarmServiceEditor.clear().commit()
        spUserPreferencesEditor.clear().commit()
    }

}