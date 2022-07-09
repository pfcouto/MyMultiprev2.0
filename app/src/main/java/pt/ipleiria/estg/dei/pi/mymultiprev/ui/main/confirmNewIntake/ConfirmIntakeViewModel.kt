package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.confirmNewIntake

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
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
import java.util.*
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
        _response.value = null
        _registrationIntakeDateTime.value = Clock.System.now().toLocalDateTime(Constants.TIME_ZONE)
    }

    fun selectDateTime(context: Context) {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        val datePickerDialog = DatePickerDialog(context, { _, year, month, day ->
            TimePickerDialog(context, { _, hour, minute ->
                Log.d("ConfirmAcquisitionScreen", "Mes -> $month")
                setTime(year, month + 1, day, hour, minute)
            }, startHour, startMinute, true).show()
        }, startYear, startMonth, startDay)

        datePickerDialog.datePicker.minDate = _prescriptionItem.value?.nextIntake?.toInstant(Constants.TIME_ZONE)!!
            .toEpochMilliseconds()
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        datePickerDialog.show()
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

        if (_registrationIntakeDateTime.value!! < prescriptionItem.value?.nextIntake!!.toInstant(
                Constants.TIME_ZONE
            )
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
            _response.value = intakeRepository.doIntake(
                IntakeDTO(
                    id = null,
                    prescriptionItemId = prescriptionItem.value?.id!!,
                    expectedAt = prescriptionItem.value?.nextIntake.toString(),
                    patientId = sharedPreferencesRepository.getCurrentPatientId(),
                    intakeDate = _registrationIntakeDateTime.value.toString(),
                    took = true
                )
            )
        }
    }
}