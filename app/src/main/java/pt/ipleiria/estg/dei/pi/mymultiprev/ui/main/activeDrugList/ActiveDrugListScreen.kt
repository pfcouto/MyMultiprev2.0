package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.activeDrugList

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.theme.MyMultiPrevTheme

@Composable
fun ActiveDrugListScreen(
//    viewModel: ActiveDrugListViewModel = hiltViewModel()
) {

    val showByColumnList = remember { mutableStateOf(true) }

    Column {

        Logout()

        Text(
            modifier = Modifier.padding(start = 32.dp, top = 8.dp, bottom = 8.dp),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            text = "Meus Antibióticos"
        )

        ListIcon(showByColumnList)


//        LazyColumn() {
//            items(listOfAntibiotics) { antibiotic ->
//
        if (showByColumnList.value)
            AntibioticCard_Prescription_Item_Short_Item()
        else
            AntibioticCard_Prescription_Item_Full_Item()
//            }
//        }
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
fun AntibioticCard_Prescription_Item_Short_Item() {
    Card(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .fillMaxWidth()
            .clickable { },
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
                    text = "Ola"
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W300,
                    text = "Ola"
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
fun AntibioticCard_Prescription_Item_Full_Item() {

    val alarmOn = remember { mutableStateOf(true) }

    val icon = if (alarmOn.value)
        Icons.Filled.AlarmOn
    else Icons.Filled.AlarmOff

    val color = if (alarmOn.value)
        Color.Green
    else Color.Red


    Card(
        modifier = Modifier
            .padding(start = 24.dp, top = 8.dp, end = 24.dp, bottom = 16.dp)
            .fillMaxWidth(),
        elevation = 10.dp
    ) {
        Column() {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W600,
                        text = "Amoxicilina 500 mg"
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W300,
                        text = "Ola"
                    )
                }
                Row(horizontalArrangement = Arrangement.End) {
                    IconButton(
                        modifier = Modifier.padding(end = 11.dp),
                        onClick = { alarmOn.value = !alarmOn.value }) {
                        Icon(imageVector = icon, contentDescription = "Loggout", tint = color)
                    }
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


@Preview(showBackground = true)
@Composable
fun MainPreview() {
    MyMultiPrevTheme {
        ActiveDrugListScreen()
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun CardPreview() {
//    MyMultiPrevTheme {
//        AntibioticCard_Prescription_Item_Short_Item()
//    }
//}

@Preview(showBackground = true)
@Composable
fun CardPreview2() {
    MyMultiPrevTheme {
        AntibioticCard_Prescription_Item_Full_Item()
    }
}
