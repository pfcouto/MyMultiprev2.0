package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.register_symptoms

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipleiria.estg.dei.pi.mymultiprev.responses.SymptomTypeItemResponse
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.theme.Teal

@Composable
fun SymptomsScreen(
    symptomsTypes: List<SymptomTypeItemResponse>,
    symptoms: SnapshotStateList<Pair<String, Boolean>>,
    onNext: () -> Unit
) {
    Text(
        text = "Efeitos Secundários",
        fontSize = 38.sp,
        textAlign = TextAlign.Center,
    )
    Text(
        modifier = Modifier.padding(horizontal = 36.dp),
        text = "Diga quais os efeitos secundários que sentiu",
        textAlign = TextAlign.Center,
        fontSize = 18.sp
    )
    Spacer(modifier = Modifier.size(32.dp))
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Divider(color = Color.Gray, thickness = 1.dp)
        symptomsTypes.forEachIndexed { idx, element ->
            Log.i("SymptomsScreen", "idx: $idx")
            if (symptoms.getOrNull(idx) == null) {
                symptoms.add(idx, element.id to false)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 32.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                symptoms[idx] = element.id to !symptoms[idx].second
                            }
                        )
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    element.name,
                    color = if (symptoms.getOrNull(idx) != null && symptoms[idx].second) Teal else Color.DarkGray,
                    fontSize = 28.sp
                )
                if (symptoms.getOrNull(idx) != null && symptoms[idx].second) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "Selected",
                        tint = Teal
                    )
                }
            }
            Divider(color = Color.Gray, thickness = 1.dp)
        }
    }
    Spacer(modifier = Modifier.size(48.dp))
    Button(
        enabled = symptoms.firstOrNull() { it.second } != null,
        colors = ButtonDefaults.buttonColors(backgroundColor = Teal),
        border = BorderStroke(1.dp, Teal),
        onClick = { onNext() }) {
        Text(text = "Seguinte", fontSize = 26.sp)
    }
}