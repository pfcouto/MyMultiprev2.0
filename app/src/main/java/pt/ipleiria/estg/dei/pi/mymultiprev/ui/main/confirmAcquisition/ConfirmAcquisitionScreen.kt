package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.confirmAcquisition

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
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
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.theme.Teal
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Util
import java.util.*

@Composable
fun ConfirmAcquisitionScreen(
    navController: NavHostController,
    viewModel: ConfirmAcquisitionViewModel = hiltViewModel()
) {

    val TAG = "ConfirmAcquisitionScreen"


    var prescriptionItem = viewModel.prescriptionItem
    var drug = viewModel.drug
    var response = viewModel.response.observeAsState()
    val predictDates = viewModel.predictDates.observeAsState()

    var patology by remember { mutableStateOf("") }
    var pickerValue by remember { mutableStateOf(prescriptionItem.value!!.frequency) }

    var showDatePicker by remember { mutableStateOf(false) }

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

    val mDate = remember { mutableStateOf("") }

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

    mCalendar.time = Date()

    val mTimePickerDialog = TimePickerDialog(
        mContext,
        { _, mHour: Int, mMinute: Int ->
            mTime = "$mHour:$mMinute"
        }, mHour, mMinute, true
    )

    viewModel.setTime(mYear, mMonth + 1, mDay, mHour, mMinute)


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
                    dividersColor = Teal,
                    value = pickerValue,
                    onValueChange = {
                        pickerValue = it;
                        //TODO provavelmente nao pode ficar aqui
                        // fazer como no ConfirmIntake
                        viewModel.recalculatePredictionDates(pickerValue)
                    },
                    range = prescriptionItem.value!!.frequency - 2..prescriptionItem.value!!.frequency + 2
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
                modifier = Modifier.padding(start = 32.dp, end = 32.dp),
                onClick = { showDatePicker = true }) {
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

            Row(modifier = Modifier.padding(top = 24.dp)) {
                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = Teal),
                    border= BorderStroke(1.dp, Teal),
                    modifier = Modifier.padding(
                        start = 32.dp,
                        end = 4.dp,
                        bottom = 8.dp
                    ), onClick = {
                        viewModel.confirmAcquisition(
                            patology,
                            pickerValue
                        )
                    }) {
                    Text(fontSize = 18.sp, fontWeight = FontWeight.Bold, text = "CONFIRMAR")
                }

                OutlinedButton(
                    colors = ButtonDefaults.outlinedButtonColors(contentColor =  Teal),
                    border= BorderStroke(1.dp, Teal),
                    modifier = Modifier.padding(
                        start = 32.dp,
                        end = 4.dp,
                        bottom = 8.dp
                    ), onClick = { navController.popBackStack() }) {
                    Text(fontSize = 18.sp, fontWeight = FontWeight.Bold, text = "CANCELAR")
                }
            }
        }
    }

    if (showDatePicker) {
        mDatePickerDialog.show()
        showDatePicker = false
        mTimePickerDialog.show()
        viewModel.setTime(mYear, mMonth + 1, mDay, mHour, mMinute)
        viewModel.recalculatePredictionDates(pickerValue)
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