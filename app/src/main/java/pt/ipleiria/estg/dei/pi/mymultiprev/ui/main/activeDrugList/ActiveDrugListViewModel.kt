package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.activeDrugList

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.daos.AlarmDao
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Alarm
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.enums.LayoutPreferences
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.DrugRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.PrescriptionItemsRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.SharedPreferencesRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.service.AlarmService
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ActiveDrugListViewModel @Inject constructor(
    prescriptionItemsRepository: PrescriptionItemsRepository,
    drugRepository: DrugRepository,
    private val alarmService: AlarmService,
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    private val alarmDao: AlarmDao
) : ViewModel() {

    private val TAG = "ActiveDrugListViewModel"

    val patientId = sharedPreferencesRepository.getCurrentPatientId()
    val drugs = drugRepository.getDrugs(patientId)
        .asLiveData()

    val prescriptionItems =
        prescriptionItemsRepository.getActivePrescriptionItems(patientId)
            .asLiveData()

    private var _pairs: MutableLiveData<List<Pair<PrescriptionItem, Drug?>>> = MutableLiveData(
        listOf()
    )
    val pairs: LiveData<List<Pair<PrescriptionItem, Drug?>>>
        get() = _pairs

    private var _layoutPreference: MutableLiveData<LayoutPreferences> =
        MutableLiveData(LayoutPreferences.getLayoutPreferenceFromValue(sharedPreferencesRepository.getCurrentActivePrescriptionItemLayoutPreference()))

    val layoutPreference: LiveData<LayoutPreferences>
        get() = _layoutPreference


    fun setLayoutPreference(pref: Boolean) {
        _layoutPreference.value = LayoutPreferences.getLayoutPreferenceFromValue(pref)
    }

    fun saveLayoutPreference(pref: Boolean) {
        sharedPreferencesRepository.setCurrentActivePrescriptionItemLayoutPreference(pref)
    }


    fun updatePairs() {
        val prescriptions = prescriptionItems.value?.data
        val list: MutableList<Pair<PrescriptionItem, Drug?>> = mutableListOf()
        if (!prescriptions.isNullOrEmpty()) {
            prescriptions.forEach { prescription ->
                val drug = getDrugById(prescription.drug)
                list.add(prescription to drug)
            }
            _pairs.value = Collections.unmodifiableList(list)
            Log.i(TAG, "updatePairs() - ${_pairs.value}")
        }
    }

    private fun getDrugById(id: String): Drug? {
        if (drugs.value == null || drugs.value?.data.isNullOrEmpty()) {
            return null
        }
        return drugs.value?.data!!.find { drug -> drug.id == id }
    }

    fun setAlarm(alarmState: Boolean, prescriptionItemId: String) {
        viewModelScope.launch {
            alarmDao.setAlarmState(alarmState, prescriptionItemId)
        }
    }
}