package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.activeDrugList

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug
import pt.ipleiria.estg.dei.pi.mymultiprev.repositories.TesteLazyColumRepository

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ActiveDrugListScreen(
    navController: NavHostController,
    viewModel: ActiveDrugListViewModel = hiltViewModel()
) {

    val showByColumnList = remember { mutableStateOf(true) }

    val listOfDrugs by viewModel.drugs.observeAsState()



    // para testes
    val testRepository = TesteLazyColumRepository()
    val alldata = testRepository.getAllData()
    //


    Column {

        Logout()

        Text(
            modifier = Modifier.padding(start = 32.dp, top = 8.dp, bottom = 8.dp),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            text = "Meus Antibióticos"
        )

        ListIcon(showByColumnList)

        var newList: List<Drug> = mutableListOf()

        if (!listOfDrugs?.data.isNullOrEmpty()) {
            newList = listOfDrugs?.data!!
        }

        if (showByColumnList.value) {
            LazyColumn() {


                    items(items = newList) { item ->

                        AntibioticCard_Prescription_Item_Short_Item(navController = navController, item = item)
                }

            }
        } else {


            LazyColumn() {
                items(items = newList) { item ->

                    AntibioticCard_Prescription_Item_Full_Item(item)
                }
            }
        }
    }
}

@Composable
fun Logout() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        IconButton(modifier = Modifier.padding(end = 11.dp), onClick = { /*TODO*/ }) {
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
fun AntibioticCard_Prescription_Item_Short_Item(navController: NavHostController, item: Drug) {



    Card(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .fillMaxWidth()
            .clickable { navController.navigate("descricaoAntibiotico") },
        elevation = 8.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                modifier = Modifier
                    .size(80.dp)
                    .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
                painter = rememberImagePainter(
                    data = "https://www.example.com/image.jpg",
                    builder = {
                        placeholder(R.drawable.placeholder)
                    }
                ),
                contentDescription = "Imagem do medicamento")

            Column(modifier = Modifier.width(160.dp)) {
                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 4.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W600,
                    text = "${item.alias}"
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W300,
                    text = "Teste"
                )
            }

            Button(modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(), onClick = { /*TODO*/ }) {
                Text(text = "VER")
            }
        }
    }
}

@Composable
fun AntibioticCard_Prescription_Item_Full_Item(item: Drug) {

    val alarmOn = remember { mutableStateOf(true) }

    val context = LocalContext.current

    val icon = if (alarmOn.value)
        Icons.Filled.AlarmOn
    else Icons.Filled.AlarmOff

    val color = if (alarmOn.value)
        Color.Green
    else Color.Red

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp),
            elevation = 10.dp
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
                            text = "${item.alias}"
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            modifier = Modifier
                                .padding(start = 16.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W300,
                            text = "teste"
                        )

                    }
//                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        modifier = Modifier.padding(end = 11.dp),

                        // TODO passar o que esta dentro do onclick para uma funcao
                        onClick = { alarmOn.value = !alarmOn.value;
                            if (alarmOn.value)
                                Toast.makeText(context, "Notificacao Ativada", Toast.LENGTH_SHORT).show()
                            else
                                Toast.makeText(context, "Notificacao Desativada", Toast.LENGTH_SHORT).show()
                        }) {
                        Icon(imageVector = icon, contentDescription = "Loggout", tint = color)
                    }
                }
                Image(modifier = Modifier
                    .fillMaxWidth()
                    .height(225.dp),
                    painter = rememberImagePainter(
                        data = "https://www.example.com/image.jpg",
                        builder = {
                            placeholder(R.drawable.placeholder)
                        }
                    ),
                    contentDescription = "Imagem do medicamento")

                TextButton(onClick = { /*TODO*/ }) {
                    Text(text = "Confirmar Toma / Ver Detalhes")
                }
            }
        }
    }
}

//@OptIn(ExperimentalPagerApi::class)
//@Composable
//fun HorizontalIndicatorPager() {
//    Column(
//        Modifier
//            .fillMaxSize()
//            .padding(10.dp)) {
//        val pagerSelect = rememberPagerState(pageCount = list.size)
//
//        Box(modifier = Modifier.fillMaxWidth()) {
//            HorizontalPager(state = pagerSelect) { index ->
//                Column(Modifier.fillMaxWidth()) {
//                    when(index){
//                        0 -> {
//
//                        }
//                        1 -> {
//
//                        }
//                    }
//                }
//            }
//        }
//
//
//
//        HorizontalPagerIndicator(
//            pagerState = pagerSelect,
//            modifier = Modifier
//                .align(Alignment.CenterHorizontally)
//                .padding(16.dp),
//        )
//    }
//}


//@Preview(showBackground = true)
//@Composable
//fun MainPreview() {
//    MyMultiPrevTheme {
//        ActiveDrugListScreen()
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun CardPreview() {
//    MyMultiPrevTheme {
//        AntibioticCard_Prescription_Item_Short_Item()
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun CardPreview2() {
//    MyMultiPrevTheme {
//
//        AntibioticCard_Prescription_Item_Full_Item()
//    }
//}
