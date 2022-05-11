package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.confirmNewIntake

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.dtos.IntakeDTO
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.DrugRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.IntakeRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.PrescriptionItemsRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.SharedPreferencesRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import javax.inject.Inject


@HiltViewModel
class ConfirmIntakeViewModel @Inject constructor(
    private val intakeRepository: IntakeRepository,
    private val drugRepository: DrugRepository,
    private val prescriptionItemsRepository: PrescriptionItemsRepository,
    private val sharedPreferencesRepository: SharedPreferencesRepository
) : ViewModel() {

    private val TAG = "ConfirmIntakeViewModel"

    private val _drug: MutableState<Drug?> = mutableStateOf(null)
    val drug: State<Drug?> = _drug

    private val _prescriptionItem: MutableState<PrescriptionItem?> = mutableStateOf(null)
    val prescriptionItem: State<PrescriptionItem?> = _prescriptionItem

//    private val _scheduleIntakeDate: MutableState<LocalDate> = mutableStateOf(LocalDate)
//    val scheduleIntakeDate: State<LocalDate> = _scheduleIntakeDate

//    lateinit var prescriptionItem: PrescriptionItem
//    lateinit var drug: Drug
//    lateinit var scheduleIntakeDate: LocalDate

    private var _registrationIntakeDateTime: MutableLiveData<LocalDateTime> =
        MutableLiveData(Clock.System.now().toLocalDateTime(Constants.TIME_ZONE))
    val registrationIntakeDateTime: LiveData<LocalDateTime> get() = _registrationIntakeDateTime

    private var _response: MutableLiveData<Resource<IntakeDTO>?> = MutableLiveData()
    val response: LiveData<Resource<IntakeDTO>?>
        get() = _response



    fun getDrug(drugID: String) {
        viewModelScope.launch {
            try {
                _drug.value = drugRepository.getDrugById(drugId = drugID).first().data!!
            } catch (e: Exception) {
                Log.d(TAG, "EXCEPTION ${e.message}")
            }
        }
    }

    fun getPrescriptionItem(prescriptionItemID: String) {
        viewModelScope.launch {
            try {
                _prescriptionItem.value =
                    prescriptionItemsRepository.getPrescriptionItemById(prescriptionItemId = prescriptionItemID)
                        .first().data!!
            } catch (e: Exception) {
                Log.d(TAG, "EXCEPTION ${e.message}")
            }
        }
    }

    fun clearResponse() {
        Log.i(TAG, "Clearing Response")
        //TODO ver isto
        _response.value = null
        _registrationIntakeDateTime.value = Clock.System.now().toLocalDateTime(Constants.TIME_ZONE)
    }

    fun setTime(year: Int, month: Int, dayOfMonth: Int, hourOfDay: Int, minute: Int) {
        Log.d(
            "Data",
            year.toString() + "/" + month.toString() + "/" + dayOfMonth.toString() + " " + hourOfDay.toString() + ":" + minute.toString()
        )
        Log.i(TAG, "Time = $hourOfDay/$minute")
        _registrationIntakeDateTime.value = LocalDateTime(
            year,
            month,
            dayOfMonth,
            hourOfDay,
            minute
        )
    }

    fun verifyRegistrationDateTime(): Boolean {

        if (_registrationIntakeDateTime.value!! < prescriptionItem.value?.nextIntake!!.toInstant(Constants.TIME_ZONE)
                .minus(2, DateTimeUnit.HOUR).toLocalDateTime(Constants.TIME_ZONE)
        ) {
            return false;
        }
        return true;
    }

    fun verifyRange(): Boolean {

        val first = prescriptionItem.value?.nextIntake!!.toInstant(Constants.TIME_ZONE)
            .minus(2, DateTimeUnit.HOUR).toLocalDateTime(Constants.TIME_ZONE)
        val second = prescriptionItem.value?.nextIntake!!.toInstant(Constants.TIME_ZONE)
            .plus(2, DateTimeUnit.HOUR).toLocalDateTime(Constants.TIME_ZONE)
        if (_registrationIntakeDateTime.value!! in first..second) {
            return true
        }
        return false
    }

    fun registerIntake() {
        viewModelScope.launch {
            Log.d("Aqui5", prescriptionItem.value?.id!!)
            Log.d("Aqui5", prescriptionItem.value?.nextIntake.toString())
            Log.d("Aqui5", sharedPreferencesRepository.getCurrentPatientId())
            Log.d("Aqui5", registrationIntakeDateTime.value.toString())
            _response.value = intakeRepository.doIntake(
                IntakeDTO(
                    id = null,
                    prescriptionItemId = prescriptionItem.value?.id!!,
                    expectedAt = prescriptionItem.value?.nextIntake.toString(),
                    patientId = sharedPreferencesRepository.getCurrentPatientId(),
                    intakeDate = registrationIntakeDateTime.value.toString(),
                    took = true
                )
            )
            Log.d("Aqui4", _response.value.toString())
        }
    }
}