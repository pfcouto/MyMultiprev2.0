package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.register_symptoms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chargemap.compose.numberpicker.NumberPicker

@Composable
fun AgeScreen(
    age: MutableState<Int>,
    onNext: () -> Unit
) {
    Text(
        text = "Idade",
        fontSize = 38.sp,
        textAlign = TextAlign.Center,
    )
    Text(
        modifier = Modifier.padding(horizontal = 36.dp),
        text = "Idade que tinha quando ocorreu",
        textAlign = TextAlign.Center,
        fontSize = 18.sp
    )
    Spacer(modifier = Modifier.size(32.dp))
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NumberPicker(
            value = age.value,
            range = 0..150,
            onValueChange = {
                age.value = it
            }
        )
    }
    Spacer(modifier = Modifier.size(48.dp))
    Button(
        border = BorderStroke(1.dp, Color.Gray),
        onClick = { onNext() }) {
        Text(text = "Seguinte", fontSize = 26.sp)
    }
}