package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.register_symptoms

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.datetime.Clock
import kotlinx.datetime.toLocalDateTime
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Symptom
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.enums.SymptomRegistrationSituation
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.BottomBarScreen
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.register_drugs.DrugsScreen
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants

@Composable
fun RegisterSymptomsScreen(
    viewModel: RegisterSymptomsViewModel = hiltViewModel(),
    navHostController: NavHostController
) {

    var surveyScreenNumber by remember { mutableStateOf(0) }

    DisposableEffect(Unit) {
        viewModel.getSymptomTypeItems()
        viewModel.getPatient()
        viewModel.getSpecificPrescriptionItem()
        viewModel.getDrugs()
        onDispose {}
    }

    val prescriptionItems = viewModel.prescriptionItems.observeAsState()


    val symptoms = remember { mutableStateListOf<Pair<String, Boolean>>() }
    val activeEvolutionType = remember { mutableStateOf(-1) }
    val activeResponse = remember { mutableStateOf(-1) }
    val evolutionTypes = remember { mutableStateListOf<String>() }
    val responseTypes = remember { mutableStateListOf<String>() }
    val activeDrug = remember { mutableStateOf(-1) }
    val age = remember { mutableStateOf(35) }


    fun clearSurvey() {
        surveyScreenNumber = 0
        activeEvolutionType.value = -1
        activeResponse.value = -1
        activeDrug.value = -1
        age.value = 35
        evolutionTypes.clear()
        evolutionTypes.addAll(setOf("Em Recuperação", "Curado"))
        responseTypes.clear()
        responseTypes.addAll(setOf("Sim", "Não"))
        symptoms.clear()
    }

    fun taskComplete() {
        val symptomsToRegister = mutableListOf<Symptom>()

        val drugIdToSend: String
        val registrationSituation: SymptomRegistrationSituation
        if (viewModel.specificPrescriptionItemId.isEmpty()) {
            registrationSituation = SymptomRegistrationSituation.ThroughOutTheDay
            drugIdToSend = prescriptionItems.value?.data!![activeDrug.value].drug
        } else {
            registrationSituation = SymptomRegistrationSituation.DuringIntake
            drugIdToSend = viewModel.specificPrescriptionItem.value!!.drug
        }
        symptoms.forEach {
            Log.i("REGISTANDO", it.first)
            if (!it.second) return@forEach
            Log.i("REGISTANDO", it.second.toString())
            symptomsToRegister.add(
                Symptom(
                    patientId = viewModel.patientId,
                    typeId = it.first,
                    registratedSituation = registrationSituation,
                    drugId = drugIdToSend,
                    registeredAt = Clock.System.now()
                        .toLocalDateTime(Constants.TIME_ZONE).toString()
                )
            )
        }

        viewModel.addSymptomsToRegister(symptomsToRegister)
        viewModel.registerSymptoms()
        viewModel.clearData()
//        findNavController().navigate(R.id.action_registerSymptomsFragment_to_activeDrugListFragment)
    }


    if (surveyScreenNumber in 1..5) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = { surveyScreenNumber-- }) {
                Icon(
                    modifier = Modifier
                        .size(38.dp),
                    tint = Color.Gray,
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Return"
                )

            }

            Button(onClick = { surveyScreenNumber = 0 }) {
                Text(
                    text = "Cancelar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (surveyScreenNumber) {
            0 -> {
                clearSurvey()
                val sympState = viewModel.symptomTypesListResponse.observeAsState()
                val prescState = viewModel.prescriptionItems.observeAsState()
                val drugsState = viewModel.drugs.observeAsState()
                if (sympState.value == null
                    || prescState.value == null
                    || drugsState.value == null
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(68.dp)
                            .fillMaxSize()
                    )
                } else {
                    StartScreen { surveyScreenNumber++ }
                }
            }
            1 -> {
                SymptomsScreen(
                    viewModel.symptomTypesListResponse.value!!,
                    symptoms
                ) { surveyScreenNumber++ }
            }
            2 -> {
                EvolutionScreen(
                    evolutionTypes,
                    activeEvolutionType,
                ) { surveyScreenNumber++ }
            }
            3 -> {
                DrugsScreen(
                    viewModel.drugs.value!!,
                    prescriptionItems.value?.data!!,
                    activeDrug
                ) {
                    surveyScreenNumber++
                }
            }
            4 -> {
                AgeScreen(age) { surveyScreenNumber++ }
            }
            5 -> {
                DoctorScreen(
                    responseTypes,
                    activeResponse,
                ) { surveyScreenNumber++ }
            }
            6 -> {
                taskComplete()
                SuccessRegisterScreen {
                    navHostController.navigate(BottomBarScreen.Sintomas.route)
                }
            }
        }
    }


//    if (viewModel.specificPrescriptionItemId.isEmpty()) {
//        viewModel.prescriptionItems.value!!.data!!
////    viewModel.findDrugById(prescriptionItem.drug)!!.id
//    }
}

