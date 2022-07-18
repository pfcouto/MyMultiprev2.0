package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.drugDetails

import android.Manifest
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
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
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LiveData
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Intake
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.theme.Teal
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
    navControllerOutsideLoginScope: NavHostController,
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

    Column() {
        Column(Modifier.weight(1.3f)) {
            AppBar(
                drug = drug,
                navControllerOutsideLoginScope = navControllerOutsideLoginScope,
                prescription = prescription,
                viewModel = viewModel
            )
        }
        Column(Modifier.weight(2f)) {
            Pager(drug = drug, prescription = prescription, intakes = intakes)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AppBar(
    drug: LiveData<Drug>,
    prescription: LiveData<PrescriptionItem>,
    navControllerOutsideLoginScope: NavHostController,
    viewModel: DrugDetailsViewModel
) {

    val drugState = drug.observeAsState()
    val prescriptionState = prescription.observeAsState()
    val showInputDialog = remember { mutableStateOf(false) }

    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )

    if (showInputDialog.value) {
        InputDialog(alias = drugState.value!!.alias, showInputDialog = showInputDialog) {
            if (it.isNotEmpty())
                viewModel.setPrescriptionItemAlias(drugState.value!!.id, it)
        }
    }

    Column() {
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
                    CircularProgressIndicator(color = Teal, modifier = Modifier.size(56.dp))
                }
            } else {
                Card {

                    val imageLocation = prescriptionState.value!!.imageLocation
                    if (imageLocation != null) {
                        val painter = rememberImagePainter(data = imageLocation)
                        Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(), painter = painter, contentDescription = "",
                            contentScale = ContentScale.FillBounds
                        )
                    } else {
                        Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            painter = painterResource(id = R.drawable.default_img),
                            contentDescription = "",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 4.dp, end = 4.dp),
                        onClick = {
                            cameraPermissionState.launchPermissionRequest()
                            navControllerOutsideLoginScope.navigate("drugDetailsScreenCamera/" + prescriptionState.value!!.id)
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
                                    color = MaterialTheme.colors.onBackground,
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
            CircularProgressIndicator(color = Teal, modifier = Modifier.size(56.dp))
        }
    } else {

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
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
            CircularProgressIndicator(color = Teal, modifier = Modifier.size(56.dp))
        }
    } else {
        Log.i("HERE COUNT", intakes.value!!.size.toString())
        Log.i("HERE", "INTAKES SUCCESS")

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(
                items = intakes.value!!
            ) { idx, item ->
                if (idx != intakes.value!!.size - 1) {
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
            .padding(
                top = if (nIntake == 1) 16.dp else 0.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            )
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
                    color = if (intake.took) MaterialTheme.myColors.darkGreen else MaterialTheme.myColors.messageOverdue,
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
    var text by remember { mutableStateOf("") }


    AlertDialog(
        onDismissRequest = {
            showInputDialog.value = false
        },
        title = {
            Text(text = "Nova Alcunha")
        },
        text = {
            Column() {
                Text("Inserira uma nova alcunha para o antibiótico:")

                val customTextSelectionColors = TextSelectionColors(
                    handleColor = Teal,
                    backgroundColor = Teal
                )

                CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                    TextField(
                        value = text,
                        placeholder = { Text(text = alias) },
                        onValueChange = { text = it },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = MaterialTheme.colors.surface,
                            textColor = MaterialTheme.colors.onSurface,
                            cursorColor = Teal,
                            focusedIndicatorColor = Teal
                        )
                    )
                }
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
                    colors = ButtonDefaults.buttonColors(backgroundColor = Teal),
                    border = BorderStroke(1.dp, Teal),
                    onClick = { showInputDialog.value = false }
                ) {
                    Text("Cancelar")
                }
                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = Teal),
                    border = BorderStroke(1.dp, Teal),
                    onClick = {
                        onSuccess(text.trim())
                        showInputDialog.value = false
                    }
                ) {
                    Text("Confirmar")
                }
            }
        }
    )

}


@Composable
fun MoreDetails(drug: State<Drug?>) {

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(scrollState)
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
