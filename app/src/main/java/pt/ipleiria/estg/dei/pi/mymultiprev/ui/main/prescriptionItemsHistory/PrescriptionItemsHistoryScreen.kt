package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.prescriptionItemsHistory

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource

@Composable
fun PrescriptionItemsHistoryScreen(
    navController: NavHostController,
    viewModel: PrescriptionItemsHistoryViewModel = hiltViewModel()
) {
    val TAG = "PrescriptionItemsHistoryScreen"

    val prescriptionItems = viewModel.prescriptionItems.observeAsState()
    val drugs = viewModel.drugs.observeAsState()
    val pairs = viewModel.pairs.observeAsState()

    var resourceSuccessNoItems by remember { mutableStateOf(false) }

    var query = viewModel.searchQuery.value

    val keyboardFocusManager = LocalFocusManager.current


    when (prescriptionItems) {
        is Resource.Success<*> -> {
            Log.i(TAG, "Resource Success")
            if (!prescriptionItems.value!!.data.isNullOrEmpty()) {
                viewModel.updatePairs()
            } else {
                resourceSuccessNoItems = true
            }
        }
        is Resource.Loading<*> -> {
            Log.i(TAG, "Resource Loading")
        }
        else -> {
            Log.i(TAG, "Resource Error")
        }
    }

    Log.d(TAG, drugs.value.toString())

//    if (!drugs.value!!.data.isNullOrEmpty()) {
//        viewModel.updatePairs()
//    }

    if (pairs.value.isNullOrEmpty()) {
        Log.i(TAG, "Pairs are NULL - Displaying No Prescription Items text")
        resourceSuccessNoItems = true
    }


    Column() {
        TextField(
            value = query,
            onValueChange = { newValue -> viewModel.onQueryChanged(newValue) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            label = {
                Text(text = "Nome do Medicamento")
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        viewModel.filterPairs(query)
                        keyboardFocusManager.clearFocus()
                    }) {

                    Icon(Icons.Filled.Search, contentDescription = "Botão para pesquisar")
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    viewModel.filterPairs(query)
                    keyboardFocusManager.clearFocus()
                }
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.surface,
                textColor = MaterialTheme.colors.onSurface
            )

        )

        if (resourceSuccessNoItems) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                    text = "Sem Antibióticos!"
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(
                    items = pairs.value!!
                ) { pair ->
                    HistoryCard(pair, navController = navController)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HistoryCard(pair: Pair<PrescriptionItem, Drug?>, navController: NavHostController) {
    Card(
        modifier = Modifier
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp)
            .fillMaxWidth(),
        elevation = 10.dp,
        onClick = { navController.navigate("descricaoAntibiotico/" + pair.first.id + "/" + pair.second!!.id) }) {

        Row() {
            Column(modifier = Modifier.weight(1f)) {

                Text(
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                    maxLines = 1,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    text = pair.second!!.alias + " " + pair.first.dosage + pair.first.intakeUnit
                )

                Text(
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 16.dp),
                    fontSize = 18.sp,
                    text = "${pair.first.intakesTakenCount ?: 0} doses tomadas"
                )
            }
            // TODO ver com imagem tirada na camara
            Image(
                modifier = Modifier
                    .padding(top = 16.dp, end = 16.dp, bottom = 16.dp)
                    .width(60.dp)
                    .height(60.dp)
                    .weight(0.4f),

                painter = painterResource(id = R.drawable.default_img),
                contentDescription = "Imagem do Medicamento"
            )
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun PrescriptionItemsHistoryScreenPreview() {
//    HistoryCard()
//}