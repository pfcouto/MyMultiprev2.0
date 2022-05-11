package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.drugDetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Intake
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.data.network.Resource
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.PrescriptionItemsRepository
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Util
import java.time.format.DateTimeFormatter

enum class TabPage() {
    Detalhes(),
    Tomas(),
    MaisInformacao()
}

@Composable
fun DrugDetailsScreen(
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

    if (prescription.value != null) {
        DisposableEffect(key1 = Unit) {
            GlobalScope.launch {
                viewModel.getIntakes()
            }
            onDispose { }
        }

    }

    val intakes = viewModel.intakes

    Column() {
        AppBar(drug = drug)
        Pager(drug = drug, prescription = prescription, intakes = intakes)
    }
}

@Composable
fun AppBar(drug: LiveData<Drug>) {

    val drugState = drug.observeAsState()


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        if (drugState.value == null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(modifier = Modifier.size(56.dp))
            }
        } else {

            Card {

                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    painter = rememberImagePainter(
                        data = "https://www.example.com/image.jpg",
                        builder = {
                            placeholder(R.drawable.placeholder)
                        }
                    ),
                    contentDescription = "Botao de Tirar Fotografia")
                IconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 10.dp, end = 10.dp),
                    onClick = { /*TODO - navegar para camera com drug.id*/ }) {
                    Icon(
                        tint = Color.Black,
                        imageVector = Icons.Filled.PhotoCamera,
                        contentDescription = "Camera"
                    )
                    // TODO ver a cor que queremos
                }
                Text(
                    maxLines = 2,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 18.dp, bottom = 18.dp),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.SemiBold,
                    text = drugState.value!!.name
                )

            }
        }
    }
}


@Composable
fun TabHome(selectIndex: Int, onSelect: (TabPage) -> Unit) {
    TabRow(selectedTabIndex = selectIndex) {
        TabPage.values().forEachIndexed { index, tabPage ->
            Tab(
                selected = index == selectIndex,
                onClick = { onSelect(tabPage) },
                text = { Text(text = if (index != 2) tabPage.name else "Mais Informação") })
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Pager(
    drug: LiveData<Drug>,
    prescription: LiveData<PrescriptionItem>,
    intakes: LiveData<Resource<List<Intake>>>
) {

    val drugState = drug.observeAsState()
    val prescriptionState = prescription.observeAsState()

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
                                Tomas(intakes = intakes)
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

    if (drug.value == null) {
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
                    text = prescription.value!!.intakeValue.toString() + " " + prescription.value!!.dosage
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
    intakes: LiveData<Resource<List<Intake>>>
) {
    //TODO intakes not working
    val resourceList = intakes.observeAsState()

    if (resourceList.value == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(modifier = Modifier.size(56.dp))
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(
                items = intakes.value!!.data!!
            ) { item ->
                Toma(item)
            }
        }
    }

}

@Composable
fun Toma(intake: Intake) {
    Card(
        modifier = Modifier
            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
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
                    text = "12313132-123-123-123-123"
                )
                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                    style = MaterialTheme.typography.body2,
                    fontSize = 18.sp,
                    color = MaterialTheme.colors.secondary,
                    text = "Took"
                )
            }
            Text(

                modifier = Modifier
                    .padding(start = 16.dp, top = 12.dp, end = 16.dp)
                    .weight(1.1F),
                style = MaterialTheme.typography.body2,
                fontSize = 18.sp,
                textAlign = TextAlign.End,
                color = MaterialTheme.colors.secondary,

                text = Util.formatDateTime(intake.intakeDate!!)
            )
        }
    }
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
