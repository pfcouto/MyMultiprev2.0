package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.register_symptoms

import android.content.Context
import android.util.Log
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Patient
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Symptom
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.ServiceBuilder
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.services.SymptomsService
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.AuthRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.DrugRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.PrescriptionItemsRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.SharedPreferencesRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.responses.SymptomTypeItemResponse
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
import javax.inject.Inject

@HiltViewModel
class RegisterSymptomsViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val prescriptionItemsRepository: PrescriptionItemsRepository,
    private val drugRepository: DrugRepository,
    private val authRepository: AuthRepository,
    sharedPreferencesRepository: SharedPreferencesRepository
) :
    ViewModel() {

    private val TAG = "RegisterSymptomsViewModel"

    private val sp = context.getSharedPreferences(Constants.AUTH_SP, Context.MODE_PRIVATE)
    val patientId
        get() = sp.getString(Constants.PATIENT_ID, "")!!

    private var _symptomTypesListResponse: MutableLiveData<List<SymptomTypeItemResponse>> =
        MutableLiveData(listOf())
    val symptomTypesListResponse: LiveData<List<SymptomTypeItemResponse>>
        get() = _symptomTypesListResponse

    private val multiPrevServices =
        ServiceBuilder(sharedPreferencesRepository).buildService(SymptomsService::class.java)

    fun getSymptomTypeItems() {
        viewModelScope.launch {
            _symptomTypesListResponse.value = multiPrevServices.getAllSymptomTypes()
        }
    }

    var specificPrescriptionItemId: String = ""
    private var _specificPrescriptionItem: MutableLiveData<PrescriptionItem> = MutableLiveData()
    val specificPrescriptionItem: LiveData<PrescriptionItem>
        get() = _specificPrescriptionItem

    private var _patient: MutableLiveData<Patient> = MutableLiveData()
    val patient: LiveData<Patient>
        get() = _patient

    fun getPatient() {
        viewModelScope.launch {
            try {
                _patient.value = authRepository.getPatient(patientId).first().data!!
            } catch (e: Exception) {
                Log.d(TAG, "EXCEPTION ${e.message}")
            }
        }
    }

    private var _drugs: MutableLiveData<List<Drug>> = MutableLiveData()
    val drugs: LiveData<List<Drug>>
        get() = _drugs

    private var _drug: MutableLiveData<Drug?> = MutableLiveData()
    val drug: MutableLiveData<Drug?>
        get() = _drug

    val prescriptionItems =
        prescriptionItemsRepository.getActivePrescriptionItems(patientId)
            .asLiveData()

    fun getDrugs() {
        viewModelScope.launch {
            _drugs.value = drugRepository.getDrugs(patientId).first().data!!
        }
    }


    var isFetched = false

    fun findDrugById(id: String): MutableLiveData<Drug?> {
        viewModelScope.launch {
            drugRepository.getDrugById(id).collect {
                _drug.value = it.data
            }
        }
        return _drug
    }


    private var _symptomsToRegister: MutableLiveData<List<Symptom>> =
        MutableLiveData(listOf())
    val symptomsToRegister: LiveData<List<Symptom>>
        get() = _symptomsToRegister

    fun addSymptomsToRegister(symptomRequestList: List<Symptom>) {
        _symptomsToRegister.value = symptomRequestList
    }

    fun registerSymptoms() {
        viewModelScope.launch {
            multiPrevServices.registerSymptoms(symptomsToRegister.value!!)
        }
    }

    fun clearData() {
        Log.i(TAG, "Clearing Data")
        _symptomsToRegister = MutableLiveData(listOf())
        _symptomTypesListResponse = MutableLiveData(listOf())
        isFetched = false
        _specificPrescriptionItem = MutableLiveData()
        specificPrescriptionItemId = ""
    }

    fun getSpecificPrescriptionItem() {
        viewModelScope.launch {
            _specificPrescriptionItem.value =
                prescriptionItemsRepository.getLocalPrescriptionItem(specificPrescriptionItemId)
        }
    }

}