package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.confirmNewIntake

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.datetime.Clock
import kotlinx.datetime.toInstant
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource
import pt.ipleiria.estg.dei.pi.mymultiprev.receiver.AlarmReceiver
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.BottomBarScreen
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.register_symptoms.RegisterSymptomsViewModel
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Constants
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


    if (prescriptionItem == null) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(68.dp)
                .fillMaxSize()
        )
    } else {

        var buttonEnabled by remember { mutableStateOf(false) }
        var showError by remember { mutableStateOf(false) }
        var estadoMessage by remember { mutableStateOf("") }
        var estadoColorVerde by remember { mutableStateOf(true) }
        var ultimaTomaVisible by remember { mutableStateOf(false) }

        if (prescriptionItem?.expectedIntakeCount == prescriptionItem?.intakesTakenCount!! + 1) {
            ultimaTomaVisible = true
        }

        val registrationIntake = viewModel.registrationIntakeDateTime.observeAsState()

        if (registrationIntake != null) {
            buttonEnabled = viewModel.verifyRegistrationDateTime()
            showError = !buttonEnabled
            if (viewModel.verifyRange()) {
                estadoMessage = "Dentro da recomendação"
                estadoColorVerde = true
            } else {
                estadoMessage = "Fora da recomendação"
                estadoColorVerde = false
            }
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
                                OutlinedButton(onClick = {
                                    // TODO validar isto!!!!!!!! Pode nao funcionar
//                                    registerSymptomsViewModel.specificPrescriptionItemId =
//                                        response.value!!.data!!.prescriptionItemId

                                    viewModel.clearResponse()
                                    openDialog.value = false;

                                    navController.navigate(BottomBarScreen.Sintomas.route + "/$prescriptionItemId")
                                }) {
                                    Text(text = "Sim")
                                }
                            },
                            dismissButton = {
                                OutlinedButton(onClick = {
                                    viewModel.clearResponse(); openDialog.value =
                                    false; navController.popBackStack()
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


        var estadoCor = if (estadoColorVerde)
            Color.Green
        else
            Color.Red


//    viewModel.displayStatus()
        // Fetching the Local Context
        val mContext = LocalContext.current

        // Declaring integer values
        // for year, month and day
        val mYear: Int
        val mMonth: Int
        val mDay: Int

        // Initializing a Calendar
        val mCalendar = Calendar.getInstance()
        // Fetching current year, month and day
        mYear = mCalendar.get(Calendar.YEAR)
        mMonth = mCalendar.get(Calendar.MONTH)
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH)


        // Declaring a string value to
        // store date in string format
        val mDate = remember { mutableStateOf("") }
        val fillDate = "$mDay/${mMonth + 1}/$mYear"

        // Declaring DatePickerDialog and setting
        // initial values as current values (present year, month and day)
        val mDatePickerDialog = DatePickerDialog(
            mContext,
            { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
                mDate.value = "$mDayOfMonth/${mMonth + 1}/$mYear"

            }, mYear, mMonth, mDay
        )
        // TODO Ainda tentar fazer
//    mDatePickerDialog.datePicker.apply {
//        minDate = prescriptionItem?.nextIntake?.toInstant(
//            Constants.TIME_ZONE
//        )?.toEpochMilliseconds()
//        maxDate = System.currentTimeMillis()
//    }

        // Fetching current hour and minute
        val mHour = mCalendar[Calendar.HOUR_OF_DAY]
        val mMinute = mCalendar[Calendar.MINUTE]

        var mTime by remember { mutableStateOf("") }
        val fillTime = "$mHour:$mMinute"

        mCalendar.time = Date()

        val mTimePickerDialog = TimePickerDialog(
            mContext,
            { _, mHour: Int, mMinute: Int ->
                mTime = "$mHour:$mMinute"
            }, mHour, mMinute, true
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier.padding(start = 32.dp, top = 32.dp, end = 32.dp),
                fontSize = 18.sp,
                text = drug!!.name
            )

            if (ultimaTomaVisible) {
                Text(
                    modifier = Modifier.padding(start = 32.dp, top = 32.dp, end = 32.dp),
                    fontSize = 18.sp,
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
                    color = estadoCor,
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
                    text = "${if (mDate.value.isNullOrBlank()) fillDate else mDate.value} ${if (mTime.isNullOrBlank()) fillTime else mTime}"
                )
            }

            var showDatePicker by remember { mutableStateOf(false) }
            var showTimePicker by remember { mutableStateOf(false) }


            Row() {
                Button(
                    modifier = Modifier.padding(start = 32.dp, top = 24.dp, end = 16.dp),
                    onClick = { showDatePicker = true }) {
                    Text(fontSize = 15.sp, text = "EDITAR DATA")
                }

                Button(
                    modifier = Modifier.padding(start = 16.dp, top = 24.dp, end = 32.dp),
                    onClick = { showTimePicker = true }) {
                    Text(fontSize = 15.sp, text = "EDITAR HORA")
                }
            }

            if (showError) {
                Text(
                    modifier = Modifier.padding(start = 32.dp, top = 24.dp, end = 32.dp),
                    fontSize = 16.sp,
                    color = Color.Red,
                    text = "A hora selecionada é inválida"
                )
            }

            Spacer(modifier = Modifier.height(270.dp))

            Column(
                modifier = Modifier
                    .padding(start = 32.dp, end = 32.dp, bottom = 8.dp)
                    .fillMaxWidth()
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = if(prescriptionItem!!.nextIntake!!.toInstant(Constants.TIME_ZONE).toEpochMilliseconds() < Clock.System.now().toEpochMilliseconds()) true else false,
                    onClick = { openDialog.value = true; /*viewModel.registerIntake();*/ setAlarm(context, prescriptionItem!!) }) {

                    Text(
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        text = "Confirmar"
                    )
                }

                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(),
                    onClick = { navController.popBackStack() }) {
                    Text(
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        text = "CANCELAR"
                    )
                }
            }

            if (showDatePicker) {
                mDatePickerDialog.show()
            }

            // TODO Funciona mas talvez seja melhor procurar alternativ aahhaah
            // serve para voltar poder a selecionar uma data
            showDatePicker = false

            if (showTimePicker) {
                mTimePickerDialog.show()
            }

            // TODO Funciona mas talvez seja melhor procurar alternativ aahhaah
            // serve para voltar poder a selecionar uma data
            showTimePicker = false

            viewModel.setTime(mYear, mMonth + 1, mDay, mHour, mMinute)

        }

    }
}

private fun setAlarm(context: Context, prescriptionItem: PrescriptionItem) {
    val timeSec = System.currentTimeMillis() + 10000
    val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java)
    intent.putExtra("title", "Toma de Medicamentos")
    intent.putExtra("message", "Está na hora de tomar o medicamento: ${prescriptionItem.drug}")
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
    alarmManager.set(AlarmManager.RTC_WAKEUP, timeSec, pendingIntent)
    Log.d("Alarmes", "Passou aqui")
}