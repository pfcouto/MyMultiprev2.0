package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.confirmNewIntake

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug
import java.util.*

@Composable
fun ConfirmIntakeDetailsScreen(
    navController: NavHostController,
    drugId: String,
    prescriptionItemId: String,
    viewModel: ConfirmIntakeViewModel = hiltViewModel()
) {

    DisposableEffect(key1 = Unit) {
        if (!drugId.isNullOrBlank()) {

            viewModel.getDrug(drugId)
        }

        if (!prescriptionItemId.isNullOrBlank()) {

            viewModel.prescriptionItem(prescriptionItemId)
        }
        onDispose { }
    }

    val drug by remember { viewModel.drug }

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

    // Fetching current hour and minute
    val mHour = mCalendar[Calendar.HOUR_OF_DAY]
    val mMinute = mCalendar[Calendar.MINUTE]

    var mTime by remember { mutableStateOf("") }
    val fillTime = "$mHour:$mMinute"

    mCalendar.time = Date()

    val mTimePickerDialog = TimePickerDialog(
        mContext,
        {_, mHour : Int, mMinute: Int ->
            mTime = "$mHour:$mMinute"
        }, mHour, mMinute, true
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            modifier = Modifier.padding(start = 32.dp, top = 32.dp, end = 32.dp),
            fontSize = 18.sp,
            text = drug?.name ?: "(Nome Antibiótico)"
        )
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
                text = "data"
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
                text = "dosagem"
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
                text = "estado"
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

        if (showDatePicker) {
            mDatePickerDialog.show()
            Log.d("showDatePicker", mDate.value)
        }

        // TODO Funciona mas talvez seja melhor procurar alternativ aahhaah
        // serve para voltar poder a selecionar uma data
        showDatePicker = false

        if (showTimePicker){
            mTimePickerDialog.show()
            Log.d("showTimePicker", mTime)
        }

        // TODO Funciona mas talvez seja melhor procurar alternativ aahhaah
        // serve para voltar poder a selecionar uma data
        showTimePicker = false

    }
}