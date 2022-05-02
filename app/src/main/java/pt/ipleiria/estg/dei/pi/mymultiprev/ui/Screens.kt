package pt.ipleiria.estg.dei.pi.mymultiprev.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chargemap.compose.numberpicker.NumberPicker
import java.util.*

@Composable
fun ConfirmAcquisitionScreen() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(modifier = Modifier.padding(top = 32.dp), fontSize = 32.sp, text = "Amoxilina 500mg")
        Column(
            modifier = Modifier
                .scrollable(
                    state = rememberScrollState(),
                    orientation = Orientation.Vertical
                )
                .padding(start = 14.dp, top = 16.dp, bottom = 10.dp)
        ) {
            Text(
                modifier = Modifier.padding(start = 32.dp, top = 32.dp),
                text = "Insira a patologia associada:"
            )

            var patology by remember { mutableStateOf("") }
            var pickerValue by remember { mutableStateOf(0) }

            OutlinedTextField(
                modifier = Modifier.padding(start = 32.dp, top = 4.dp, end = 32.dp),
                value = patology,
                onValueChange = { patology = it },
                singleLine = true,
                label = {
                    Text(
                        text = "Patologia"
                    )
                })
            Row(
                modifier = Modifier.padding(top = 30.dp, bottom = 30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(start = 32.dp, end = 10.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    text = "Frequência:"
                )
                NumberPicker(
                    value = pickerValue,
                    onValueChange = { pickerValue = it },
                    range = 1..24
                )
                Text(
                    modifier = Modifier.padding(start = 10.dp, end = 32.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    text = "Frequência:"
                )

            }

            Button(
                modifier = Modifier.padding(start = 32.dp, end = 32.dp),
                onClick = { /*TODO*/ }) {
                Text(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    text = "AGENDAR A PRIIMEIRA TOMA"
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, top = 16.dp, end = 32.dp)
                    .weight(1f)
            ) {
                items(18) { index ->
                    Text(text = "Item $index")
                }
            }

            Row(modifier = Modifier.padding(top = 24.dp)) {
                OutlinedButton(
                    modifier = Modifier.padding(
                        start = 32.dp,
                        end = 4.dp,
                        bottom = 8.dp
                    ), onClick = { /*TODO*/ }) {
                    Text(fontSize = 18.sp, fontWeight = FontWeight.Bold, text = "CANCELAR")
                }

                OutlinedButton(
                    modifier = Modifier.padding(
                        start = 32.dp,
                        end = 4.dp,
                        bottom = 8.dp
                    ), onClick = { /*TODO*/ }) {
                    Text(fontSize = 18.sp, fontWeight = FontWeight.Bold, text = "CONFIRMAR")
                }
            }
        }
    }
}

@Composable
fun NewInTakeDetailsScreen() {


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
            text = "(Nome Droga)"
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

//@Composable
//fun DatePicker() {
//
//
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//
//        // Creating a button that on
//        // click displays/shows the DatePickerDialog
//        Button(onClick = {
//            mDatePickerDialog.show()
//        }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF0F9D58))) {
//            Text(text = "Open Date Picker", color = Color.White)
//        }
//
//        // Adding a space of 100dp height
//        Spacer(modifier = Modifier.size(100.dp))
//
//        // Displaying the mDate value in the Text
//
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun ConfirmAcquisitionScreenPreview() {
//    ConfirmAcquisitionScreen()
//}

@Preview(showBackground = true)
@Composable
fun NewInTakeDetailsScreenPreview() {
    NewInTakeDetailsScreen()
}
