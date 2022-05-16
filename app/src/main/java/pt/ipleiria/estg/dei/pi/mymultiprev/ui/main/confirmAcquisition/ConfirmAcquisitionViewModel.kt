package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.confirmAcquisition

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.dtos.PrescriptionItemDTO
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.PrescriptionItemsRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import javax.inject.Inject

@HiltViewModel
class ConfirmAcquisitionViewModel @Inject constructor(
    private val prescriptionItemsRepository: PrescriptionItemsRepository
) : ViewModel() {

    //TODO FALTA ECRA

    private val TAG = "ConfirmAcquisitionViewModel"

    private val _drug: MutableState<Drug?> = mutableStateOf(null)
    val drug: State<Drug?> = _drug

    private val _prescriptionItem: MutableState<PrescriptionItem?> = mutableStateOf(null)
    val prescriptionItem: State<PrescriptionItem?> = _prescriptionItem

//    lateinit var prescriptionItem: PrescriptionItem
//    lateinit var drug: Drug
//    lateinit var scheduleIntakeDate: LocalDate
//    private lateinit var scheduleIntakeDateTime: LocalDateTime

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
        Log.d(
            "Data",
            year.toString() + "/" + month.toString() + "/" + dayOfMonth.toString() + " " + hourOfDay.toString() + ":" + minute.toString()
        )
        Log.i(TAG, "Time = $hourOfDay/$minute")
        _scheduleIntakeDateTime.value = LocalDateTime(
            year,
            month,
            dayOfMonth,
            hourOfDay,
            minute
        )
    }

    fun clearResponse() {
        Log.i(TAG, "Clearing Response")
        _response = MutableLiveData()
        _predictDates = MutableLiveData()
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