package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.prescriptionItemsHistory

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.skydoves.landscapist.glide.GlideImage
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.theme.Teal

@Composable
fun PrescriptionItemsHistoryScreen(
    navController: NavHostController,
    viewModel: PrescriptionItemsHistoryViewModel = hiltViewModel()
) {
    val TAG = "PrescriptionItemsHistoryScreen"

    val drugs = viewModel.drugs.observeAsState()
    val pairs = viewModel.pairs.value
    val prescriptionItems = viewModel.prescriptionItems.observeAsState()

    var resourceSuccessNoItems by remember { mutableStateOf(false) }

    var query = viewModel.searchQuery.value

    val focusManager = LocalFocusManager.current


    if (pairs.isEmpty()) {
        when (prescriptionItems.value) {
            is Resource.Success -> {
                Log.i(TAG, "Resource Success")
                Log.i(TAG, "Pairs are empty - Displaying No Prescription Items text")
                resourceSuccessNoItems = if (!prescriptionItems.value?.data.isNullOrEmpty()) {
                    Log.i(TAG, ">>-----<<  GOING TO UPDATE PAIRS  >>-----<<")
                    viewModel.updatePairs()
                    false
                } else {
                    true
                }
            }
            else -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        color = Teal,
                        modifier = Modifier
                            .size(68.dp)
                            .fillMaxSize()
                    )
                }
            }
        }
    } else {
        Log.i(TAG, "Pairs are not empty")
        resourceSuccessNoItems = false
    }


    Column(modifier = Modifier.pointerInput(Unit) {
        detectTapGestures(onTap = {
            focusManager.clearFocus()
        })
    }) {
        val customTextSelectionColors = TextSelectionColors(
            handleColor = Teal,
            backgroundColor = Teal
        )

        CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
            TextField(
                value = query,
                onValueChange = { newValue ->
                    viewModel.onQueryChanged(newValue)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                label = {
                    Text(text = "Nome do Medicamento")
                },
                leadingIcon = {
                    IconButton(
                        onClick = {
                            viewModel.filterPairs(query)
                            focusManager.clearFocus()
                        }) {

                        Icon(Icons.Filled.Search, contentDescription = "Botão para pesquisar")
                    }
                },
                trailingIcon = {

                    if (!query.isNullOrEmpty()) {
                        IconButton(
                            onClick = {
                                viewModel.onQueryChanged("")
//                            viewModel.updatePairs()
                                focusManager.clearFocus()
                            }) {

                            Icon(Icons.Filled.Close, contentDescription = "Botão para apagar o texto")
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.filterPairs(query)
                        focusManager.clearFocus()
                    }
                ),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Teal,
                    backgroundColor = MaterialTheme.colors.background,
                    textColor = MaterialTheme.colors.onBackground,
                    cursorColor = Teal,
                    focusedLabelColor = Teal
                )
            )
        }

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
                    items = pairs
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
    val focusManager = LocalFocusManager.current
    Card(
        modifier = Modifier
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp)
            .fillMaxWidth(),
        elevation = 10.dp,
        onClick = {
            focusManager.clearFocus()
            navController.navigate("descricaoAntibiotico/" + pair.first.id + "/" + pair.second!!.id)
        }) {

        Row() {
            Column(modifier = Modifier.weight(1f)) {

                Text(
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                    maxLines = 2,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    text = pair.second!!.alias + " " + pair.first.dosage
                )

                Text(
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 16.dp),
                    fontSize = 16.sp,
                    text = "${pair.first.intakesTakenCount ?: 0} doses tomadas"
                )
            }

            GlideImage(
                modifier = Modifier
                    .padding(top = 16.dp, end = 16.dp, bottom = 16.dp)
                    .width(60.dp)
                    .height(60.dp)
                    .weight(0.4f),
                imageModel = pair.first.imageLocation,
                // Crop, Fit, Inside, FillHeight, FillWidth, None
                contentScale = ContentScale.FillBounds,
                // shows a placeholder while loading the image.
//                placeHolder = ImageBitmap.imageResource(R.drawable.loading),
                // shows an error ImageBitmap when the request failed.
                error = ImageBitmap.imageResource(R.drawable.default_img),
            )
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun PrescriptionItemsHistoryScreenPreview() {
//    HistoryCard()
//}