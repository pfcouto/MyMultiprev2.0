package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.confirmNewIntake

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource
import pt.ipleiria.estg.dei.pi.mymultiprev.receiver.AlarmReceiver
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.BottomBarScreen
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.register_symptoms.RegisterSymptomsViewModel
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.theme.Teal
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.theme.myColors
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Util
import java.util.*

@Composable
fun ConfirmIntakeDetailsScreen(
    navController: NavHostController,
    drugId: String,
    prescriptionItemId: String,
    viewModel: ConfirmIntakeViewModel = hiltViewModel(),
    registerSymptomsViewModel: RegisterSymptomsViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    DisposableEffect(key1 = Unit) {
        if (!drugId.isNullOrBlank()) {

            viewModel.getDrug(drugId)
        }

        if (!prescriptionItemId.isNullOrBlank()) {

            viewModel.getPrescriptionItem(prescriptionItemId)
        }
        onDispose { }
    }

    val drug by remember { viewModel.drug }
    val prescriptionItem by remember { viewModel.prescriptionItem }
    val dateTime by viewModel.registrationIntakeDateTime.observeAsState()


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

        var showError by remember { mutableStateOf(false) }
        var estadoMessage by remember { mutableStateOf("") }
        var estadoColorVerde by remember { mutableStateOf(true) }
        var ultimaTomaVisible by remember { mutableStateOf(false) }

        if (prescriptionItem?.expectedIntakeCount == prescriptionItem?.intakesTakenCount!! + 1) {
            ultimaTomaVisible = true
        }


        showError = !viewModel.verifyRegistrationDateTime()
        if (viewModel.verifyRange()) {
            estadoMessage = "Dentro da recomendação"
            estadoColorVerde = true
        } else {
            estadoMessage = "Fora da recomendação"
            estadoColorVerde = false
        }


        val response = viewModel.response.observeAsState()
        val openDialog = remember { mutableStateOf(false) }

        if (response.value != null) {
            when (response.value!!) {
                is Resource.Success -> {

//                loadingDialog.dismissDialog()

                    if (openDialog.value) {
                        AlertDialog(onDismissRequest = {
                            openDialog.value = false
                            navController.popBackStack()
                        },
                            title = {
                                Text(text = "Sintomas")
                            },
                            text = {
                                Text(text = "Sentiu algum sintoma secundário durante a toma?")
                            },
                            confirmButton = {
                                OutlinedButton(
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Teal
                                    ),
                                    border = BorderStroke(1.dp, Teal),
                                    onClick = {

                                        viewModel.clearResponse()
                                        openDialog.value = false;

                                        navController.navigate(BottomBarScreen.Sintomas.route + "/$prescriptionItemId")
                                    }) {
                                    Text(text = "Sim")
                                }
                            },
                            dismissButton = {
                                OutlinedButton(
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Teal
                                    ),
                                    border = BorderStroke(1.dp, Teal),
                                    onClick = {
                                        viewModel.clearResponse(); openDialog.value =
                                        false; navController.navigate(BottomBarScreen.Antibioticos.route)
                                    }) {
                                    Text(text = "Não")
                                }
                            }
                        )
                    }
                }
                is Resource.Error -> {
                    //TODO VER ISTO
//                loadingDialog.dismissDialog()
//                    Util.handleError(response)
                }
                else -> {
//                loadingDialog.dismissDialog()
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier.padding(start = 32.dp, top = 32.dp, end = 32.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                text = drug!!.name
            )

            if (ultimaTomaVisible) {
                Text(
                    modifier = Modifier.padding(start = 32.dp, top = 32.dp, end = 32.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.myColors.darkRed,
                    text = "Ultima Toma"
                )
            }

            Row() {
                Text(
                    modifier = Modifier.padding(start = 32.dp, top = 24.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    text = "Data prevista:"
                )

                Text(
                    modifier = Modifier
                        .padding(top = 24.dp, end = 32.dp)
                        .weight(1f),
                    textAlign = TextAlign.End,
                    fontSize = 18.sp,
                    text = prescriptionItem!!.formattedNextIntake()
                )
            }

            Row() {
                Text(
                    modifier = Modifier.padding(start = 32.dp, top = 24.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    text = "Dosagem:"
                )

                Text(
                    modifier = Modifier
                        .padding(top = 24.dp, end = 32.dp)
                        .weight(1f),
                    textAlign = TextAlign.End,
                    fontSize = 18.sp,
                    text = prescriptionItem!!.dosage
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.padding(start = 32.dp, top = 24.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    text = "Estado:"
                )

                Text(
                    modifier = Modifier
                        .padding(top = 24.dp, end = 32.dp)
                        .weight(1f),
                    textAlign = TextAlign.End,
                    fontSize = 18.sp,
                    color = if (estadoColorVerde)
                        MaterialTheme.myColors.darkGreen
                    else
                        MaterialTheme.myColors.darkRed,
                    text = estadoMessage
                )
            }
            Row() {
                Text(
                    modifier = Modifier.padding(start = 32.dp, top = 24.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    text = "Data de Registo:"
                )
                // TODO VERIFICAR SE A GUARDAR A DATA quando a mudamos esta certo, nao testei
                Text(
                    modifier = Modifier
                        .padding(top = 24.dp, end = 32.dp)
                        .weight(1f),
                    textAlign = TextAlign.End,
                    fontSize = 18.sp,
                    text = Util.formatDateTime(dateTime!!)
                )
            }

            Row() {
                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = Teal),
                    border = BorderStroke(1.dp, Teal),
                    modifier = Modifier.padding(start = 32.dp, top = 24.dp, end = 32.dp),
                    onClick = { viewModel.selectDateTime(context) }) {
                    Text(fontSize = 15.sp, text = "EDITAR DATA E HORA")
                }
            }


            Spacer(modifier = Modifier.weight(1f))

            if (showError) {
                Text(
                    modifier = Modifier.padding(
                        start = 32.dp,
                        top = 32.dp,
                        end = 32.dp,
                        bottom = 8.dp
                    ),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.myColors.darkRed,
                    text = "A hora selecionada é inválida"
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 32.dp, end = 32.dp, bottom = 32.dp)
                    .fillMaxWidth()

            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = Teal),
                    border = BorderStroke(1.dp, Teal),
//                    enabled = !showError,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !showError,
                    onClick = {
                        openDialog.value = true
                        viewModel.registerIntake()
                        Log.d("Alarmes", "${prescriptionItem!!.alarm}")
//                        if (prescriptionItem!!.alarm) {
//
//                            setAlarm(context, drug!!.name, prescriptionItem!!)
//                        }
                    }) {

                    Text(
                        color = MaterialTheme.colors.surface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        text = "CONFIRMAR"
                    )
                }

                OutlinedButton(
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Teal),
                    border = BorderStroke(1.dp, Teal),
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = { navController.popBackStack() }) {
                    Text(
                        color = MaterialTheme.colors.onSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        text = "CANCELAR"
                    )
                }
            }
        }
    }
}

private fun setAlarm(context: Context, drugName: String, prescriptionItem: PrescriptionItem) {
    val uniqueId = (Date().time / 1000L % Int.MAX_VALUE).toInt()
//    val timeSec = System.currentTimeMillis() + 10000
    val timeSec = prescriptionItem.nextIntake!!.toInstant(TimeZone.UTC).toEpochMilliseconds()
    val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java)
    intent.putExtra("title", "Toma de Medicamentos")
    intent.putExtra("message", "Tomar o medicamento: $drugName")
    intent.putExtra("uniqueId", uniqueId)
    Log.d("Alarmes", "$drugName")
    val pendingIntent = PendingIntent.getBroadcast(context, uniqueId, intent, 0)
    alarmManager.set(AlarmManager.RTC_WAKEUP, timeSec, pendingIntent)
    Log.d("Alarmes", "Passou aqui")
}