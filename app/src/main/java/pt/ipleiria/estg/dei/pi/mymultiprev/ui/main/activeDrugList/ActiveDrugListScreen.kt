package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.activeDrugList

import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.skydoves.landscapist.glide.GlideImage
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.MainViewModel
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.confirmAcquisition.ConfirmAcquisitionViewModel
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.confirmNewIntake.ConfirmIntakeViewModel
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.seeDetails.SeeDetailsViewModel
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ActiveDrugListScreen(
    navController: NavHostController,
    viewModel: ActiveDrugListViewModel = hiltViewModel(),
    confirmViewModel: ConfirmAcquisitionViewModel = hiltViewModel(),
    confirmIntakeViewModel: ConfirmIntakeViewModel = hiltViewModel(),
    seeDetailsViewModel: SeeDetailsViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel(),
    logout: () -> Unit
) {

    val TAG = "ActiveDrugListScreen"

    val showByColumnList = remember { mutableStateOf(true) }

    val listOfDrugs by viewModel.drugs.observeAsState()
    val listOfPairs by viewModel.pairs.observeAsState()
    val listOfPrescriptions by viewModel.prescriptionItems.observeAsState()

    var loadingData by remember { mutableStateOf(false) }
    var openDialog by remember { mutableStateOf(false) }


    AlertDialogLogout(openDialog = openDialog, { openDialog = false }) {
        mainViewModel.deleteAppData()
        logout()
    }


    when (listOfPrescriptions) {
        is Resource.Success -> {
            loadingData = false
            Log.d(TAG, "Resource Success")
            if (!(listOfPrescriptions as Resource.Success<List<PrescriptionItem>>).data.isNullOrEmpty()) {
                viewModel.updatePairs()
                viewModel.updateNextAlarm()
            }
        }
        is Resource.Loading -> {
            loadingData = true
            Log.d(TAG, "Resource Loading")
        }
        else -> {
            loadingData = true
            Log.d(TAG, "Resource Error")
        }
    }



    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Logout() {
            openDialog = true
        }

        Text(
            modifier = Modifier.padding(start = 32.dp, top = 8.dp, bottom = 8.dp),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            text = "Meus Antibióticos"
        )

        ListIcon(showByColumnList)

        Log.d("Pairs", listOfPairs?.size.toString())

        if (listOfPairs?.isNotEmpty() == true) {

            LazyColumn() {

                items(items = listOfPairs!!) { item ->

                    Log.d("Aqui4", item.first.isOverdue.toString())

                    var prescriptionAcquisitionConfirmed = remember { mutableStateOf(false) }
                    val prescriptionIsOverdue = remember { mutableStateOf(false) }

                    // TODO ver isto
                    if (item.first.acquiredAt == null) {
                        prescriptionAcquisitionConfirmed.value = false
                    } else {
                        prescriptionAcquisitionConfirmed.value = true
                        if (item.first.nextIntake != null) {
                            if (item.first.isOverdue) {
                                prescriptionIsOverdue.value = true
                            }
                        }
                    }

                    var timeTextText by remember { mutableStateOf("") }

                    if (item.first.acquiredAt == null) {
                        timeTextText = "Confirmar Aquisição"
                    } else {
                        if (item.first.nextIntake != null) {
                            if (item.first.isOverdue) {
                                timeTextText = "Toma em Atraso"
                            } else {
                                val diffMillis = item.first.timeUntil()
                                val dayDiff = TimeUnit.MILLISECONDS.toDays(diffMillis!!)
                                val hourDiff =
                                    TimeUnit.MILLISECONDS.toHours(diffMillis) % TimeUnit.DAYS.toHours(
                                        1
                                    )
                                val minDiff =
                                    TimeUnit.MILLISECONDS.toMinutes(diffMillis) % TimeUnit.HOURS.toMinutes(
                                        1
                                    )
                                if (dayDiff == 0L) {
                                    if (hourDiff == 0L) {
                                        if (minDiff == 0L)
                                            timeTextText = "Menos de um minuto"
                                        else
                                            timeTextText = "${minDiff}min"
                                    } else
                                        timeTextText = "${hourDiff} e ${minDiff}min"
                                } else
                                    timeTextText = "${dayDiff}d e ${hourDiff}h"
                            }
                        }
                    }



                    if (showByColumnList.value) {
                        AntibioticCard_Prescription_Item_Short_Item(
                            navController = navController,
                            item = item,
                            seeDetailsViewModel = seeDetailsViewModel,
                            confirmIntakeViewModel = confirmIntakeViewModel,
                            confirmViewModel = confirmViewModel,
                            prescriptionAcquisitionConfirmed = prescriptionAcquisitionConfirmed,
                            prescriptionIsOverdue = prescriptionIsOverdue,
                            timeTextText = timeTextText
                        )
                    } else {
                        AntibioticCard_Prescription_Item_Full_Item(
                            navController = navController,
                            item = item,
                            seeDetailsViewModel = seeDetailsViewModel,
                            confirmIntakeViewModel = confirmIntakeViewModel,
                            confirmViewModel = confirmViewModel,
                            prescriptionAcquisitionConfirmed = prescriptionAcquisitionConfirmed,
                            prescriptionIsOverdue = prescriptionIsOverdue,
                            timeTextText = timeTextText
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (loadingData) {

                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(68.dp)
                            .fillMaxSize()
                    )
                } else {

                    Text(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.SemiBold,
                        text = "Sem Antibióticos"
                    )
                }
            }
        }
    }
}

@Composable
fun AlertDialogLogout(openDialog: Boolean, setDialogFalse: () -> Unit, logout: () -> Unit) {
    MaterialTheme {
        Column {
            if (openDialog) {
                AlertDialog(
                    onDismissRequest = {
                    },
                    title = {
                        Text(text = "LOGOUT")
                    },
                    text = {
                        Text("Are you sure you want to logout?")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                setDialogFalse()
                                logout()
                            }) {
                            Text("Logout")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                setDialogFalse()
                            }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun Logout(
    onClick: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        IconButton(
            modifier = Modifier.padding(end = 11.dp),
            onClick = onClick
        ) {
            Icon(imageVector = Icons.Outlined.Logout, contentDescription = "Loggout")
        }
    }
}

@Composable
fun ListIcon(showByColumnList: MutableState<Boolean>) {


    val icon = if (showByColumnList.value)
        Icons.Outlined.ListAlt
    else Icons.Filled.Layers

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        IconButton(
            modifier = Modifier.padding(end = 11.dp),
            onClick = { showByColumnList.value = !showByColumnList.value }) {
            Icon(imageVector = icon, contentDescription = "Loggout")
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AntibioticCard_Prescription_Item_Short_Item(
    navController: NavHostController,
    item: Pair<PrescriptionItem, Drug?>,
    seeDetailsViewModel: SeeDetailsViewModel,
    confirmIntakeViewModel: ConfirmIntakeViewModel,
    confirmViewModel: ConfirmAcquisitionViewModel,
    prescriptionAcquisitionConfirmed: MutableState<Boolean>,
    prescriptionIsOverdue: MutableState<Boolean>,
    timeTextText: String
) {

    val cardBackgroundColor = if (!prescriptionAcquisitionConfirmed.value)
        Color.LightGray
    else MaterialTheme.colors.surface

    val timeTextColor = if (prescriptionIsOverdue.value)
        Color.Red
    else
        Color.DarkGray


    Card(
        modifier = Modifier
            .padding(start = 24.dp, top = 12.dp, end = 24.dp, bottom = 12.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate("descricaoAntibiotico/" + item.first.id + "/" + item.second!!.id)
            },
        elevation = 8.dp,
        backgroundColor = cardBackgroundColor
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

//            Glide.with(LocalContext.current).load(item.first.imageLocation)
//                .placeholder(R.drawable.default_img).into(this)
//            item.first.imageLocation?.let {
//                CoilImage(
//                    imageModel = it,
//                    // Crop, Fit, Inside, FillHeight, FillWidth, None
//                    contentScale = ContentScale.Crop,
//                    // shows a placeholder while loading the image.
//                    placeHolder = ImageBitmap.imageResource(R.drawable.placeholder),
//                    // shows an error ImageBitmap when the request failed.
//                    error = ImageBitmap.imageResource(R.drawable.error_image)
//                )
//            }
//            GlideImage(
//                imageModel = item.first.imageLocation,
//                // Crop, Fit, Inside, FillHeight, FillWidth, None
//                contentScale = ContentScale.Crop,
//                // shows a placeholder while loading the image.
//                placeHolder = ImageBitmap.imageResource(R.drawable.default_img),
//                // shows an error ImageBitmap when the request failed.
//                error = ImageBitmap.imageResource(R.drawable.error_image)
//            )

            //TODO alterar imagem, mas coloquei so para ficar bonito
            Image(
                modifier = Modifier
                    .size(80.dp)
                    .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
                painter = painterResource(id = R.drawable.default_img),
                contentDescription = "Imagem do medicamento"
            )

            Column(modifier = Modifier.width(160.dp)) {
                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 4.dp),
                    fontSize = 18.sp,
                    maxLines = 1,
                    fontWeight = FontWeight.W600,
                    text = "${item.second?.alias}"
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W300,
                    color = timeTextColor,
                    text = timeTextText
                )
            }

            Button(modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
                onClick = {
                    onDetailsAndConfirmButtonClick(
                        item = item,
                        confirmIntakeViewModel = confirmIntakeViewModel,
                        confirmViewModel = confirmViewModel,
                        navController = navController,
                        seeDetailsViewModel = seeDetailsViewModel
                    )
                }) {
                Text(text = "VER")
            }
        }
    }
}

@Composable
fun AntibioticCard_Prescription_Item_Full_Item(
    item: Pair<PrescriptionItem, Drug?>,
    navController: NavHostController,
    seeDetailsViewModel: SeeDetailsViewModel,
    confirmIntakeViewModel: ConfirmIntakeViewModel,
    confirmViewModel: ConfirmAcquisitionViewModel,
    prescriptionAcquisitionConfirmed: MutableState<Boolean>,
    prescriptionIsOverdue: MutableState<Boolean>,
    timeTextText: String
) {

    val alarmOn = remember { mutableStateOf(true) }

    val context = LocalContext.current

    val icon = if (alarmOn.value)
        Icons.Filled.AlarmOn
    else Icons.Filled.AlarmOff

    val color = if (alarmOn.value)
        Color.Green
    else Color.Red

    val cardBackgroundColor = if (!prescriptionAcquisitionConfirmed.value)
        Color.LightGray
    else MaterialTheme.colors.surface

    val timeTextColor = if (prescriptionIsOverdue.value)
        Color.Red
    else
        Color.DarkGray

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp)
                .clickable {  navController.navigate("descricaoAntibiotico/" + item.first.id + "/" + item.second!!.id) },
            elevation = 10.dp,
            backgroundColor = cardBackgroundColor
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            modifier = Modifier
                                .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.W600,
                            maxLines = 2,
                            text = "${item.second?.alias}"
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            modifier = Modifier
                                .padding(start = 16.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W300,
                            color = timeTextColor,
                            text = timeTextText
                        )
                    }
//                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        modifier = Modifier.padding(end = 11.dp),

                        // TODO passar o que esta dentro do onclick para uma funcao
                        onClick = {
                            alarmOn.value = !alarmOn.value;
                            if (alarmOn.value)
                                Toast.makeText(context, "Notificacao Ativada", Toast.LENGTH_SHORT)
                                    .show()
                            else
                                Toast.makeText(
                                    context,
                                    "Notificacao Desativada",
                                    Toast.LENGTH_SHORT
                                ).show()
                        }) {
                        Icon(imageVector = icon, contentDescription = "Loggout", tint = color)
                    }
                }
                if (item.first.imageLocation == null) {

                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(225.dp),
                        painter = painterResource(id = R.drawable.default_img),
                        contentDescription = "Imagem do medicamento"
                    )
                } else {
                    GlideImage(
                        imageModel = item.first.imageLocation,
                        // Crop, Fit, Inside, FillHeight, FillWidth, None
                        contentScale = ContentScale.Crop,
                        // shows a placeholder while loading the image.
                        placeHolder = ImageBitmap.imageResource(R.drawable.default_img),
                        // shows an error ImageBitmap when the request failed.
                        error = ImageBitmap.imageResource(R.drawable.error_image)
                    )
                }

                TextButton(onClick = {
                    onDetailsAndConfirmButtonClick(
                        item = item,
                        confirmIntakeViewModel = confirmIntakeViewModel,
                        confirmViewModel = confirmViewModel,
                        navController = navController,
                        seeDetailsViewModel = seeDetailsViewModel
                    )
                }) {
                    Text(text = "Confirmar Toma / Ver Detalhes")
                }
            }
        }
    }
}

private fun onDetailsAndConfirmButtonClick(
    item: Pair<PrescriptionItem, Drug?>,
    confirmIntakeViewModel: ConfirmIntakeViewModel,
    confirmViewModel: ConfirmAcquisitionViewModel,
    navController: NavHostController,
    seeDetailsViewModel: SeeDetailsViewModel
) {

    Log.d("onDetailsAndConfirmButtonClick", "Aqui")
    if (item.first.acquiredAt == null) {
        onConfirmAcquisitionClick(pair = item, confirmViewModel = confirmViewModel)
    } else {
        if (item.first.nextIntake != null) {
            if (item.first.isOverdue) {
                onConfirmDoseClick(item, confirmIntakeViewModel, navController)
            } else {
                onSeeDetailsClick(pair = item, seeDetailsViewModel = seeDetailsViewModel, navController = navController)
            }
        }
    }

}


fun onSeeDetailsClick(
//    imageview: ImageView?,
    pair: Pair<PrescriptionItem, Drug?>,
    seeDetailsViewModel: SeeDetailsViewModel,
    navController: NavHostController
) {
//    seeDetailsViewModel.setPrescriptionItemDrugPair(pair)
    navController.navigate("descricaoAntibiotico/" + pair.first.id + "/" + pair.second!!.id)
    Log.d("onSeeDetailsClick", "aqui details")

//        if (imageview != null) {
//            val args =
//                bundleOf(Constants.PRESCRIPTION_ITEM_IMAGE_TRANSITION to imageview.transitionName)
//            val extras = FragmentNavigatorExtras(imageview to imageview.transitionName)
//            findNavController().navigate(
//                R.id.action_activeDrugListFragment_to_drugDetailsFragment,
//                args,
//                null,
//                extras
//            )
//        } else {
//            findNavController().navigate(
//                R.id.action_activeDrugListFragment_to_drugDetailsFragment
//            )
//        }

}

fun onConfirmDoseClick(
    pair: Pair<PrescriptionItem, Drug?>,
    confirmIntakeViewModel: ConfirmIntakeViewModel,
    navController: NavHostController
) {
//    confirmIntakeViewModel.setPrescriptionItemDrugPair(pair)
//    Log.d("onConfirmDoseClick", "aqui confirm intake")
    navController.navigate("newIntakeDetailsScreen/${pair.first!!.id}/${pair.second!!.id}")

}

//    fun onAlarmClick(prescriptionItem: PrescriptionItem) {
//        viewModel.prescriptionItems.value?.data?.find { prescriptionItem.id == it.id }?.alarm =
//            prescriptionItem.alarm
//        val alarmState = prescriptionItem.alarm
//        viewModel.setAlarm(alarmState, prescriptionItem.id)
//        val restId = when (alarmState) {
//            true -> R.string.alarm_on
//            else -> R.string.alarm_off
//        }
//        com.google.android.material.snackbar.Snackbar.make(binding.root, restId, com.google.android.material.snackbar.Snackbar.LENGTH_SHORT)
//            .setAction(getString(R.string.OK)) {}.show()\

fun onConfirmAcquisitionClick(
    pair: Pair<PrescriptionItem, Drug?>,
    confirmViewModel: ConfirmAcquisitionViewModel
) {

    confirmViewModel.setPrescriptionItemDrugPair(pair)
    Log.d("onConfirmAcquisitionClick", "aqui confirmViewModel")
//        findNavController().navigate(R.id.action_activeDrugListFragment_to_confirmAcquisitionFragment)
}
