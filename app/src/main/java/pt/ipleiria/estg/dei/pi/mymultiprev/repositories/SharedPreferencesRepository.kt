package pt.ipleiria.estg.dei.pi.mymultiprev.repositories

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import javax.inject.Inject

class SharedPreferencesRepository @Inject constructor(@ApplicationContext context: Context) {
    private val spAlarmService =
        context.getSharedPreferences(Constants.MY_PREFS, Context.MODE_PRIVATE)
    private val spAlarmServiceEditor = spAlarmService.edit()

    private val spAuth = context.getSharedPreferences(Constants.AUTH_SP, Context.MODE_PRIVATE)
    private val spAuthEditor = spAuth.edit()

    private val spUserPreferences =
        context.getSharedPreferences(Constants.USER_PREFERENCES, Context.MODE_PRIVATE)
    private val spUserPreferencesEditor = spUserPreferences.edit()

    //Alarm
//    fun saveAlarm(nextAlarm: Long) {
//        spAlarmServiceEditor.putLong(Constants.SP_NEXT_ALARM, nextAlarm)
//        spAlarmServiceEditor.apply()
//    }
//
//    fun getAlarm() = spAlarmService.getLong(Constants.SP_NEXT_ALARM, Constants.SP_DEFAULT_LONG)
//
//
//    fun removeAlarm() {
//        spAlarmServiceEditor.remove(Constants.SP_NEXT_ALARM)
//        spAlarmServiceEditor.apply()
//    }


    //Alarms V2
    fun setNextAlarms(nextAlarms: Set<String>) {
        spAlarmServiceEditor.putStringSet(Constants.SP_NEXT_ALARMS, nextAlarms)
        spAlarmServiceEditor.commit()
    }

    fun getNextAlarms(): MutableSet<String>? =
        spAlarmService.getStringSet(Constants.SP_NEXT_ALARMS, null)

    fun clearAlarms() {
        spAlarmServiceEditor.remove(Constants.SP_NEXT_ALARMS)
        spAlarmServiceEditor.commit()
    }

    fun removeAlarm(instantAndId: String) {
        val nextAlarms = getNextAlarms() ?: return
        Log.d("NOTIFICATIONS", "removeAlarm - $instantAndId")
        Log.d("NOTIFICATIONS", "removeAlarm before remove  alarms - $nextAlarms")
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
            nextAlarms.remove(alarmToRemove)
            spAlarmServiceEditor.putStringSet(Constants.SP_NEXT_ALARMS, nextAlarms)
            spAlarmServiceEditor.commit()
        }
        Log.d("NOTIFICATIONS", "removeAlarm after remove alarms - $nextAlarms")
    }

    fun removeAllAlarm(id: String) {
        val nextAlarms = getNextAlarms() ?: return
        Log.d("NOTIFICATIONS", "removeAlarm - $id")
        Log.d("NOTIFICATIONS", "removeAlarm before remove  alarms - $nextAlarms")

        var nextAlarmsClean = mutableListOf<String>()
        nextAlarms.forEach {
            if (it.split(";")[1] != id) {
                nextAlarmsClean.add(it)
            }
        }
        spAlarmServiceEditor.putStringSet(Constants.SP_NEXT_ALARMS, nextAlarmsClean.toSet())
        spAlarmServiceEditor.commit()
        Log.d("NOTIFICATIONS", "removeAlarm after remove alarms - $nextAlarmsClean")
    }

    fun addAlarm(newAlarm: String) {
        Log.d("NOTIFICATIONS", "Shared preferences - add alarm")

        val stringSet = spAlarmService.getStringSet(Constants.SP_NEXT_ALARMS, null)

        val newSet = mutableSetOf<String>()
        if (stringSet != null) {
            Log.d("NOTIFICATIONS", "Shared preferences - adding previous alarms")
            newSet.addAll(stringSet)
        }

        newSet.forEach {
            Log.d("NOTIFICATIONS", "Adding - Verifying Duplicated alarms")
            Log.d("NOTIFICATIONS", "$it - $newAlarm")
            if (it == newAlarm) {
                Log.d("NOTIFICATIONS", "equals found - newAlarm not being added")
                return
            }
        }

        Log.d("NOTIFICATIONS", "equals not found - newAlarm being added")
        newSet.add(newAlarm)

        spAlarmServiceEditor.putStringSet(Constants.SP_NEXT_ALARMS, newSet.toSet())
        spAlarmServiceEditor.commit()
        Log.d("NOTIFICATIONS", "Shared preferences - Alarm added '$newAlarm'")
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