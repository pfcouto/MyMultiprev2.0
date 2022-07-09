package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.confirmAcquisition

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
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.dtos.PrescriptionItemDTO
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.DrugRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.PrescriptionItemsRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ConfirmAcquisitionViewModel @Inject constructor(
    private val prescriptionItemsRepository: PrescriptionItemsRepository,
    private val drugRepository: DrugRepository,
) : ViewModel() {

    private val TAG = "ConfirmAcquisitionViewModel"

    private val _drug: MutableState<Drug?> = mutableStateOf(null)
    val drug: State<Drug?> = _drug

    private val _prescriptionItem: MutableState<PrescriptionItem?> = mutableStateOf(null)
    val prescriptionItem: State<PrescriptionItem?> = _prescriptionItem

    private var _scheduleIntakeDateTime: MutableLiveData<LocalDateTime> =
        MutableLiveData(Clock.System.now().toLocalDateTime(Constants.TIME_ZONE))
    val scheduleIntakeDateTime: LiveData<LocalDateTime> get() = _scheduleIntakeDateTime

    private var _response: MutableLiveData<Resource<PrescriptionItemDTO>> = MutableLiveData()
    val response: LiveData<Resource<PrescriptionItemDTO>>
        get() = _response

    private var _predictDates: MutableLiveData<List<LocalDateTime>> = MutableLiveData(listOf())
    val predictDates: LiveData<List<LocalDateTime>>
        get() = _predictDates

    fun setPrescriptionItemDrugPair(pair: Pair<PrescriptionItem, Drug?>) {
        _prescriptionItem.value = pair.first
        if (pair.second != null) {
            _drug.value = pair.second!!
        }
    }

    fun setTime(year: Int, month: Int, dayOfMonth: Int, hourOfDay: Int, minute: Int) {
        Log.i(TAG, "Time = $hourOfDay/$minute")
        _scheduleIntakeDateTime.value = LocalDateTime(
            year,
            month,
            dayOfMonth,
            hourOfDay,
            minute
        )

    }

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
        _response = MutableLiveData()
        _predictDates = MutableLiveData()
    }

    fun selectDateTime(context: Context, frequency: Int) {
        var time = ""
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(context, { _, year, month, day ->
            TimePickerDialog(context, { _, hour, minute ->
                Log.d("ConfirmAcquisitionScreen", "Mes -> $month")
                setTime(year, month + 1, day, hour, minute)
                recalculatePredictionDates(frequency)
            }, startHour, startMinute, true).show()
        }, startYear, startMonth, startDay).show()

    }

    fun recalculatePredictionDates(frequency: Int) {
        if (_scheduleIntakeDateTime.value == null)
            return

        var aux = _scheduleIntakeDateTime.value?.toInstant(Constants.TIME_ZONE)
        val predictionDates = ArrayList<LocalDateTime>()
        val numberOfPredictions = Constants.HOURS_OF_DAY / frequency

        if (aux != null) {
            predictionDates.add(aux.toLocalDateTime(Constants.TIME_ZONE))
        }
        for (i in 1..numberOfPredictions) {
            if (aux != null) {
                aux = aux.plus(frequency, DateTimeUnit.HOUR)
                predictionDates.add(aux.toLocalDateTime(Constants.TIME_ZONE))
            }
        }
        _predictDates.value = predictionDates
    }

    fun confirmAcquisition(pathology: String?, frequency: Int) {
        val updatedPrescriptionItem = _prescriptionItem
        updatedPrescriptionItem.value?.pathology = pathology
        updatedPrescriptionItem.value?.nextIntake = _scheduleIntakeDateTime.value
        updatedPrescriptionItem.value?.acquiredAt =
            Clock.System.now().toLocalDateTime(Constants.TIME_ZONE)
        updatedPrescriptionItem.value?.frequency = frequency
        viewModelScope.launch {
            _response.value = prescriptionItemsRepository.updatePrescription(
                _prescriptionItem.value!!.id,
                updatedPrescriptionItem.value!!
            )
        }
    }
}