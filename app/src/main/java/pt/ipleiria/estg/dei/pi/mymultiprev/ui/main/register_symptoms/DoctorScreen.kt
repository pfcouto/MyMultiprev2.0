package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.register_symptoms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.theme.Teal200

@Composable
fun DoctorScreen(
    responseTypes: SnapshotStateList<String>,
    activeResponse: MutableState<Int>,
    onNext: () -> Unit
) {
    Text(
        text = "Contactou algum médico?",
        fontSize = 38.sp,
        textAlign = TextAlign.Center,
    )
    Text(
        modifier = Modifier.padding(horizontal = 36.dp),
        text = "Quando ocorreu, recorreu a algum médico?",
        textAlign = TextAlign.Center,
        fontSize = 18.sp
    )
    Spacer(modifier = Modifier.size(32.dp))
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Divider(color = Color.Gray, thickness = 1.dp)
        responseTypes.forEachIndexed { idx, eTypeName ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 32.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                if (activeResponse.value == idx) {
                                    activeResponse.value = -1
                                } else {
                                    activeResponse.value = idx
                                }
                            }
                        )
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    eTypeName,
                    color = if (idx == activeResponse.value) Color.Cyan else Color.DarkGray,
                    fontSize = 28.sp
                )
                if (idx == activeResponse.value) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "Selected",
                        tint = Color.Cyan
                    )
                }
            }
            Divider(color = Color.Gray, thickness = 1.dp)
        }
    }
    Spacer(modifier = Modifier.size(48.dp))
    Button(
        enabled = activeResponse.value > -1,
        colors = ButtonDefaults.buttonColors(backgroundColor = Teal200),
        border = BorderStroke(1.dp, Teal200),
        onClick = { onNext() }) {
        Text(text = "Seguinte", fontSize = 26.sp)
    }
}