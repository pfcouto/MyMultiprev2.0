package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.register_symptoms

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipleiria.estg.dei.pi.mymultiprev.responses.SymptomTypeItemResponse

@Composable
fun SymptomsScreen(
    symptomsTypes: List<SymptomTypeItemResponse>,
    symptoms: SnapshotStateList<Pair<String, Boolean>>,
    onNext: () -> Unit
) {
//    if (symptoms.size < symptomsTypes.size) {
//        symptomsTypes.forEachIndexed { idx, element ->
//            symptoms.add(idx, element.id to false)
//        }
//    }


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
        symptomsTypes.forEachIndexed { idx, element ->
            Log.i("SymptomsScreen","idx: $idx")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 32.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                if (symptoms.getOrNull(idx) == null) {
                                    Log.i("SymptomsScreen", "if: $idx")
                                    Log.i("SymptomsScreen", "if: ${symptoms.indices}")
                                    symptoms[idx] = element.id to true
                                } else {
                                    Log.i("SymptomsScreen", "else: $idx")
                                    Log.i("SymptomsScreen", "else: ${symptoms.indices}")

                                    symptoms[idx] = element.id to !symptoms[idx].second
                                }
                            }
                        )
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    element.name,
                    color = if (symptoms.getOrNull(idx) != null && symptoms[idx].second) Color.Cyan else Color.DarkGray,
                    fontSize = 28.sp
                )
                if (symptoms.getOrNull(idx) != null && symptoms[idx].second) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "Selected",
                        tint = Color.Cyan
                    )
                }
            }
            if (idx + 1 < symptomsTypes.size) {
                Divider(color = Color.Gray, thickness = 1.dp)
            }
        }
    }
    Spacer(modifier = Modifier.size(48.dp))
    Button(
        border = BorderStroke(1.dp, Color.Gray),
        onClick = { onNext() }) {
        Text(text = "Seguinte", fontSize = 26.sp)
    }
}