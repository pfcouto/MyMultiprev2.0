package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.drugDetails

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LiveData
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Intake
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.theme.myColors
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Util


enum class TabPage() {
    Detalhes(),
    Tomas(),
    MaisInformacao()
}

@Composable
fun DrugDetailsScreen(
    navController: NavHostController,
    viewModel: DrugDetailsViewModel = hiltViewModel(),
    drugId: String,
    prescriptionId: String
) {
    DisposableEffect(key1 = Unit) {
        if (!drugId.isNullOrBlank()) {
            viewModel.getDrug(drugId)
        }
        if (!prescriptionId.isNullOrBlank()) {
            viewModel.getPrescription(prescriptionId)
        }

        onDispose { }
    }

    val drug = remember { viewModel.drug }
    val prescription = remember { viewModel.prescriptionItem }

    if (prescription.observeAsState().value != null) {
        DisposableEffect(key1 = Unit) {

            GlobalScope.launch {
                viewModel.getIntakes()
            }
            onDispose { }
        }
    }

    val intakes = remember { viewModel.intakes }

//    val handler = Handler(Looper.getMainLooper())
//    handler.postDelayed({
//        Log.i("HERE2", viewModel.intakes.value.toString())
//    }, 5000)

    Column() {
        AppBar(
            drug = drug,
            navController = navController,
            prescription = prescription,
            viewModel = viewModel
        )
        Pager(drug = drug, prescription = prescription, intakes = intakes)
    }
}

@Composable
fun AppBar(
    drug: LiveData<Drug>,
    prescription: LiveData<PrescriptionItem>,
    navController: NavHostController,
    viewModel: DrugDetailsViewModel
) {

    val drugState = drug.observeAsState()
    val prescriptionState = prescription.observeAsState()
    val showInputDialog = remember { mutableStateOf(false) }

    if (showInputDialog.value) {
        InputDialog(alias = drugState.value!!.alias, showInputDialog = showInputDialog) {
            viewModel.setPrescriptionItemAlias(drugState.value!!.id, it)
        }
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        if (drugState.value == null || prescriptionState.value == null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(modifier = Modifier.size(56.dp))
            }
        } else {
            Card {
                GlideImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),

                    imageModel = prescriptionState.value!!.imageLocation,
                    // Crop, Fit, Inside, FillHeight, FillWidth, None
                    contentScale = ContentScale.FillBounds,
                    // shows a placeholder while loading the image.
//                    placeHolder = ImageBitmap.imageResource(R.drawable.loading),
                    // shows an error ImageBitmap when the request failed.
                    error = ImageBitmap.imageResource(R.drawable.default_img),

                    )
                IconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 4.dp),
                    onClick = {
//                        navController.navigate("drugDetailsScreenCamera/" + prescriptionState.value!!.id + "/" + drugState.value!!.id)
                        navController.navigate("drugDetailsScreenCamera/" + prescriptionState.value!!.id)
                    }) {
                    Box(
                        modifier = Modifier
                            .border(
                                BorderStroke(100.dp, Color.White),
                                shape = CircleShape
                            )
                            .size(42.dp)
                    )
                    Icon(
                        tint = Color.Black,
                        imageVector = Icons.Filled.PhotoCamera,
                        modifier = Modifier
                            .size(32.dp),
                        contentDescription = "Camera"
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(bottom = 0.dp, start = 0.dp, end = 0.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    if (isSystemInDarkTheme())
                                        MaterialTheme.colors.surface else Color.White
                                ), tileMode = TileMode.Clamp
                            )
                        ),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(
                        modifier = Modifier
                            .padding(all = 18.dp)
                    ) {
                        Spacer(modifier = Modifier.size(40.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                maxLines = 1,
                                color = Color.Black,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                text = drugState.value!!.name,
                                softWrap = true
                            )
                            IconButton(
                                onClick = {
                                    showInputDialog.value = true
                                }) {
                                Icon(
                                    tint = if (isSystemInDarkTheme())
                                        Color.White else Color.Black,
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Edit_Pencil"
                                )
                            }
                        }
                        if (drugState.value!!.alias.isNotBlank() && drugState.value!!.alias != drugState.value!!.name) {

                            Text(
                                maxLines = 2,
                                color = Color.Black,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                text = drugState.value!!.alias
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun TabHome(selectIndex: Int, onSelect: (TabPage) -> Unit) {
    TabRow(
        selectedTabIndex = selectIndex,
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface
    ) {
        TabPage.values().forEachIndexed { index, tabPage ->
            Tab(
                selected = index == selectIndex,
                onClick = { onSelect(tabPage) },
                text = {
                    Text(
                        fontWeight = FontWeight.W700,
                        fontSize = 16.sp,
                        text = if (index != 2) tabPage.name else "Mais Informação"
                    )
                })
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Pager(
    drug: LiveData<Drug>,
    prescription: LiveData<PrescriptionItem>,
    intakes: LiveData<List<Intake>>
) {

    val drugState = drug.observeAsState()
    val prescriptionState = prescription.observeAsState()
    val intakesState = intakes.observeAsState()

    val pagerSelect = rememberPagerState(pageCount = TabPage.values().size)
    val scope = rememberCoroutineScope()
    Surface(color = MaterialTheme.colors.background) {
        Scaffold(topBar = {
            TabHome(selectIndex = pagerSelect.currentPage, onSelect = {
                scope.launch {
                    pagerSelect.animateScrollToPage(it.ordinal)
                }
            })
        }, content = {
            Box(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(state = pagerSelect) { index ->
                    Column(Modifier.fillMaxSize()) {
                        when (index) {
                            0 -> {
                                Details(drug = drugState, prescription = prescriptionState)
                            }
                            1 -> {
                                Tomas(intakes = intakesState)
                            }

                            2 -> {
                                MoreDetails(drug = drugState)
                            }
                        }
                    }
                }
            }
        })
    }
}

@Composable
fun Details(drug: State<Drug?>, prescription: State<PrescriptionItem?>) {

    if (drug.value == null || prescription.value == null) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(modifier = Modifier.size(56.dp))
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    maxLines = 1,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    text = "Nome Comercial"
                )
                Text(
                    maxLines = 1,
                    modifier = Modifier
                        .weight(1F)
                        .padding(end = 16.dp, top = 16.dp),
                    fontSize = 18.sp,
                    textAlign = TextAlign.End,
                    text = drug.value!!.commercialName
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    maxLines = 1,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    text = "Nome"
                )
                Text(
                    maxLines = 1,
                    modifier = Modifier
                        .weight(1F)
                        .padding(end = 16.dp, top = 16.dp),
                    fontSize = 18.sp,
                    textAlign = TextAlign.End,
                    text = drug.value!!.name
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    maxLines = 1,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    text = "Dosagem"
                )
                Text(
                    maxLines = 1,
                    modifier = Modifier
                        .weight(1F)
                        .padding(end = 16.dp, top = 16.dp),
                    fontSize = 18.sp,
                    textAlign = TextAlign.End,
                    text = prescription.value!!.dosage
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    maxLines = 1,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    text = "Método"
                )
                Text(
                    maxLines = 1,
                    modifier = Modifier
                        .weight(1F)
                        .padding(end = 16.dp, top = 16.dp),
                    fontSize = 18.sp,
                    textAlign = TextAlign.End,
                    text = drug.value!!.intakeMethod
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    maxLines = 1,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    text = "Frequência"
                )
                Text(
                    maxLines = 1,
                    modifier = Modifier
                        .weight(1F)
                        .padding(end = 16.dp, top = 16.dp),
                    fontSize = 18.sp,
                    textAlign = TextAlign.End,
                    text = prescription.value!!.frequency.toString() + "h"
                )
            }
            if (prescription.value!!.nextIntake != null) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        maxLines = 1,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        text = "Próxima Toma"
                    )
                    Text(
                        maxLines = 1,
                        modifier = Modifier
                            .weight(1F)
                            .padding(end = 16.dp, top = 16.dp),
                        fontSize = 18.sp,
                        textAlign = TextAlign.End,
                        text = Util.formatDateTime(prescription.value!!.nextIntake!!)
                    )
                }
            }
        }
    }
}

@Composable
fun Tomas(
    intakes: State<List<Intake>?>
) {

    Log.i("HERE", intakes.value.toString())


    if (intakes.value == null) {
        Log.i("HERE", "INTAKES LOADING")

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(modifier = Modifier.size(56.dp))
        }
    } else {
        Log.i("HERE COUNT", intakes.value!!.size.toString())
        Log.i("HERE", "INTAKES SUCCESS")

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(
                items = intakes.value!!
            ) { idx, item ->
                if (idx != intakes.value!!.size - 1){
                    Toma(item, idx + 1)
                }
            }
        }
    }
}

@Composable
fun Toma(intake: Intake, nIntake: Int) {
    Card(
        modifier = Modifier
            .padding(top = if (nIntake == 1) 16.dp else 0.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
            .fillMaxWidth(),
        elevation = 10.dp
    ) {
        Row() {
            Column(Modifier.weight(1F)) {
                Text(
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp),
                    style = MaterialTheme.typography.h6,
                    fontSize = 20.sp,
                    maxLines = 2,
                    text = "Toma $nIntake"
                )
                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                    style = MaterialTheme.typography.body2,
                    fontSize = 18.sp,
                    color = if (intake.took) MaterialTheme.myColors.darkGreen else MaterialTheme.myColors.darkRed,
                    text = if (intake.took) "Tomado" else "Falhou Toma"
                )
            }
            Text(

                modifier = Modifier
                    .padding(start = 16.dp, top = 12.dp, end = 16.dp)
                    .weight(1.1F),
                style = MaterialTheme.typography.body2,
                fontSize = 18.sp,
                textAlign = TextAlign.End,

                text = if (intake.took) Util.formatDateTime(intake.intakeDate!!) else Util.formatDateTime(
                    intake.expectedAt!!
                )
            )
        }
    }
}

@Composable
fun InputDialog(
    alias: String,
    showInputDialog: MutableState<Boolean>,
    onSuccess: (String) -> Unit
) {
    var text by remember { mutableStateOf(alias) }


    AlertDialog(
        onDismissRequest = {
            showInputDialog.value = false
        },
        title = {
            Text(text = "New Alias")
        },
        text = {
            Column() {
                Text("Insert a new alias for the drug")
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = MaterialTheme.colors.surface,
                        textColor = MaterialTheme.colors.onSurface
                    )
                )
            }
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { showInputDialog.value = false }
                ) {
                    Text("Dismiss")
                }
                Button(
                    onClick = {
                        onSuccess(text.trim())
                        showInputDialog.value = false
//                            setNewAlias(text)
                    }
                ) {
                    Text("Confirm")
                }
            }
        }
    )

}


@Composable
fun MoreDetails(drug: State<Drug?>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            text = "Classe Farmacêutica:"
        )
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp),
            fontSize = 18.sp,
            text = drug.value!!.pharmClass
        )

        Text(
            modifier = Modifier.padding(start = 16.dp, top = 24.dp, end = 16.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            text = "Terapias:"
        )
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp),
            fontSize = 18.sp,
            text = drug.value!!.therapies
        )
    }
}
