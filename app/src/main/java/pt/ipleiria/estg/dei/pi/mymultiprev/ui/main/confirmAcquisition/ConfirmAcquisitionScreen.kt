package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.confirmAcquisition

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.chargemap.compose.numberpicker.NumberPicker
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.dtos.PrescriptionItemDTO
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.BottomBarScreen
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.theme.Teal
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Util

@Composable
fun ConfirmAcquisitionScreen(
    navController: NavHostController,
    drugId: String,
    prescriptionItemId: String,
    viewModel: ConfirmAcquisitionViewModel = hiltViewModel()
) {

    val TAG = "ConfirmAcquisitionScreen"

    DisposableEffect(key1 = Unit) {
        if (drugId.isNotBlank()) {

            viewModel.getDrug(drugId)
        }

        if (prescriptionItemId.isNotBlank()) {

            viewModel.getPrescriptionItem(prescriptionItemId)
        }
        onDispose { }
    }

    val drug by remember { viewModel.drug }
    val prescriptionItem by remember { viewModel.prescriptionItem }

    Log.d(TAG, prescriptionItem.toString())

    if (prescriptionItem == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Teal,
                modifier = Modifier
                    .size(68.dp)
                    .fillMaxSize()
            )
        }
    } else {

        var response = viewModel.response.observeAsState()
        val predictDates = viewModel.predictDates.observeAsState()
        var patology by remember { mutableStateOf("") }
        var pickerValue by remember { mutableStateOf(prescriptionItem!!.frequency) }
        val context = LocalContext.current

        DisposableEffect(key1 = Unit) {
            Log.d(TAG, "Esteve aqui !!!!")
            viewModel.recalculatePredictionDates(pickerValue)
            onDispose { }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(modifier = Modifier.padding(top = 32.dp), fontSize = 32.sp, text = drug!!.name)
            Column(
                modifier = Modifier
                    .scrollable(
                        state = rememberScrollState(),
                        orientation = Orientation.Vertical
                    )
                    .padding(start = 8.dp, top = 16.dp, end = 8.dp, bottom = 10.dp)
                    .fillMaxWidth()
            ) {

                Text(
                    modifier = Modifier.padding(start = 32.dp, top = 32.dp),
                    text = "Insira a patologia associada:"
                )

                val customTextSelectionColors = TextSelectionColors(
                    handleColor = Teal,
                    backgroundColor = Teal
                )

                CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                    OutlinedTextField(
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Teal,
                            cursorColor = Teal,
                            focusedLabelColor = Teal
                        ),
                        modifier = Modifier
                            .padding(start = 32.dp, top = 4.dp, end = 32.dp)
                            .fillMaxWidth(),
                        value = patology,
                        onValueChange = { patology = it },
                        singleLine = true,
                        label = {
                            Text(
                                text = "Patologia"
                            )
                        })
                }

                Row(
                    modifier = Modifier
                        .padding(top = 30.dp, bottom = 30.dp)
                        .fillMaxWidth(),

                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier.padding(start = 32.dp, end = 10.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        text = "Frequência:"
                    )

                    NumberPicker(
                        dividersColor = Teal,
                        value = pickerValue,
                        onValueChange = {
                            pickerValue = it;
                            viewModel.recalculatePredictionDates(pickerValue)
                        },
                        range = prescriptionItem!!.frequency - 2..prescriptionItem!!.frequency + 2
                    )

                    Text(
                        modifier = Modifier.padding(start = 10.dp, end = 32.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        text = "Horas"
                    )

                }

                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = Teal),
                    border = BorderStroke(1.dp, Teal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp, end = 32.dp),
                    onClick = { viewModel.selectDateTime(context = context, pickerValue) }) {
                    Text(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        text = "AGENDAR A PRIMEIRA TOMA"
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp, top = 16.dp, end = 32.dp)
                        .weight(1f)
                ) {
                    itemsIndexed(items = predictDates.value!!) { index, date ->
                        Text(text = "Toma ${index + 1}: ${Util.formatDateTime(date)}")
                    }
                }

                Column(modifier = Modifier.padding(start = 32.dp, end = 32.dp, top = 24.dp)) {

                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = Teal),
                        border = BorderStroke(1.dp, Teal),
                        modifier = Modifier.fillMaxWidth(), onClick = {
                            viewModel.confirmAcquisition(
                                patology,
                                pickerValue
                            )
                            navController.navigate(BottomBarScreen.Antibioticos.route)
                        }) {
                        Text(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.surface,
                            text = "CONFIRMAR"
                        )
                    }

                    OutlinedButton(
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Teal),
                        border = BorderStroke(1.dp, Teal),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { navController.popBackStack() }) {
                        Text(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.onSurface,
                            text = "CANCELAR"
                        )
                    }
                }
            }
        }

        when (response.value) {
            is Resource.Success -> {
                Log.i(TAG, "Resource Success")
                viewModel.clearResponse()
                navController.popBackStack()
            }
            is Resource.Loading -> {
                Log.i(TAG, "Resource Loading")
            }
            is Resource.Error -> {
                Log.i(TAG, "Resource Error")
                Util.handleError(response.value as Resource.Error<PrescriptionItemDTO>)
            }
        }
    }
}