package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.register_symptoms

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun RegisterSymptomsScreen(viewModel: RegisterSymptomsViewModel = hiltViewModel()) {

    var surveyScreenNumber by remember { mutableStateOf(0) }

    viewModel.getSymptomTypeItems()

    val symptoms = remember { mutableStateListOf<Pair<String, Boolean>>() }

    fun clearSurvey() {
        surveyScreenNumber = 0
        symptoms.clear()
    }

    if (surveyScreenNumber > 0) {
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

            Button(onClick = { clearSurvey() }) {
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
                StartScreen { surveyScreenNumber++ }
            }
            1 -> {
                SymptomsScreen(
                    viewModel.symptomTypesListResponse.value!!,
                    symptoms
                ) { surveyScreenNumber++ }
            }
            2 -> {
                EvolutionScreen { surveyScreenNumber++ }
            }
            3 -> {
                DrugsScreen { surveyScreenNumber++ }
            }
            4 -> {
                AgeScreen { surveyScreenNumber++ }
            }
            5 -> {
                DoctorScreen { surveyScreenNumber++ }
            }
            6 -> {
                SuccessRegisterScreen { surveyScreenNumber = 0 }
            }
        }

    }

}
