package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.register_drugs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.theme.Teal

@Composable
fun DrugsScreen(
    drugTypes: List<Drug>,
    prescriptionItems: List<PrescriptionItem>,
    activeDrug: MutableState<Int>,
    onNext: () -> Unit
) {

    Text(
        text = "Medicamento",
        fontSize = 38.sp,
        textAlign = TextAlign.Center,
    )
    Text(
        modifier = Modifier.padding(horizontal = 36.dp),
        text = "Indique qual o medicamento associado ao sintoma",
        textAlign = TextAlign.Center,
        fontSize = 18.sp
    )
    Spacer(modifier = Modifier.size(32.dp))
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Divider(color = Color.Gray, thickness = 1.dp)
        prescriptionItems.forEachIndexed { idx, element ->
            val drug = drugTypes.firstOrNull {
                it.id == element.drug
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 32.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                if (activeDrug.value == idx) {
                                    activeDrug.value = -1
                                } else {
                                    activeDrug.value = idx
                                }
                            }
                        )
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    drug!!.alias.ifEmpty { drug.commercialName },
                    color = if (idx == activeDrug.value) Teal else Color.DarkGray,
                    fontSize = 28.sp
                )
                if (idx == activeDrug.value) {
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
        enabled = activeDrug.value > -1,
        colors = ButtonDefaults.buttonColors(backgroundColor = Teal),
        border = BorderStroke(1.dp, Teal),
        onClick = { onNext() }) {
        Text(text = "Seguinte", fontSize = 26.sp)
    }
}