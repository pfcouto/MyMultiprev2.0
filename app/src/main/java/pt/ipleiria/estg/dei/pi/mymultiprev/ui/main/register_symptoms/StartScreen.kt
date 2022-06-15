package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.register_symptoms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.theme.Teal200

@Composable
fun StartScreen(onNext: () -> Unit) {
    Text(
        text = "Registar Sintomas",
        fontSize = 38.sp,
        textAlign = TextAlign.Center,
    )
    Text(
        modifier = Modifier.padding(horizontal = 36.dp),
        text = "Diga-nos quais os sintomas secundários que teve",
        textAlign = TextAlign.Center,
        fontSize = 18.sp
    )
    Spacer(modifier = Modifier.size(48.dp))
    Button(
        colors = ButtonDefaults.buttonColors(backgroundColor = Teal200),
        border = BorderStroke(1.dp, Teal200),
        onClick = { onNext() }) {
        Text(text = "Começar", fontSize = 26.sp)
    }
}