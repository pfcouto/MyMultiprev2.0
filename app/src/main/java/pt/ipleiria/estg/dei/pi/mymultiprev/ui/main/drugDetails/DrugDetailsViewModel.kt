package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.drugDetails

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Intake
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.DrugRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.IntakeRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.PrescriptionItemsRepository
import javax.inject.Inject

@HiltViewModel
class DrugDetailsViewModel @Inject constructor(
    private val prescriptionItemsRepository: PrescriptionItemsRepository,
    private val drugRepository: DrugRepository,
    private val intakeRepository: IntakeRepository
) : ViewModel() {

    private val TAG = "SeeDetailsViewModel"

    private var _photoUri: MutableLiveData<Uri?> = MutableLiveData()
    val photoUri: LiveData<Uri?>
        get() = _photoUri

    private var _prescriptionItem: MutableLiveData<PrescriptionItem> = MutableLiveData()
    val prescriptionItem: LiveData<PrescriptionItem>
        get() = _prescriptionItem

    private var _drug: MutableLiveData<Drug> = MutableLiveData()
    val drug: LiveData<Drug>
        get() = _drug

    private var _intakes: MutableLiveData<Resource<List<Intake>>> = MutableLiveData()
    val intakes: LiveData<Resource<List<Intake>>>
        get() = _intakes

//    lateinit var intakes: LiveData<Resource<List<Intake>>>

    fun getPrescriptionItemPhoto(id: String) {
        viewModelScope.launch {
            _photoUri.value = prescriptionItemsRepository.getPrescriptionItemPhoto(id)
        }
    }

    fun setPrescriptionItemDrugPair(pair: Pair<PrescriptionItem, Drug?>) {
        _prescriptionItem.value = pair.first!!
        _drug.value = pair.second!!
    }

    suspend fun getIntakes() {
        _intakes.postValue(
            intakeRepository.getIntakesByPrescriptionItemId(_prescriptionItem.value!!.id).first()
        )
    }

    fun setPrescriptionItemAlias(id: String, alias: String) {
        _drug.value?.alias = alias
        viewModelScope.launch {
            drugRepository.setDrugAlias(id, alias)
        }
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

    fun getPrescription(prescriptionId: String) {
        viewModelScope.launch {
            try {
                _prescriptionItem.value =
                    prescriptionItemsRepository.getPrescriptionItemById(prescriptionId)
                        .first().data!!
            } catch (e: Exception) {
                Log.d(TAG, "EXCEPTION ${e.message}")
            }
        }
    }
}