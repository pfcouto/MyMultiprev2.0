package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.register_symptoms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.theme.Teal

@Composable
fun SuccessRegisterScreen(onNext: () -> Unit) {
    Text(
        text = "Registo efectuado com sucesso!",
        fontSize = 38.sp,
        textAlign = TextAlign.Center,
    )
    Text(
        modifier = Modifier.padding(horizontal = 36.dp),
        text = "A informação será reportada ao INFARMED. Em breve será contactado.",
        textAlign = TextAlign.Center,
        fontSize = 18.sp
    )
    Spacer(modifier = Modifier.size(48.dp))
    Icon(
        modifier = Modifier.size(124.dp),
        imageVector = Icons.Outlined.CheckCircle,
        contentDescription = "Right Checkmark"
    )
    Spacer(modifier = Modifier.size(48.dp))
    Button(
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Teal),
        border = BorderStroke(1.dp, Teal),
        onClick = { onNext() }) {
        Text(text = "Terminar", fontSize = 26.sp)
    }
}