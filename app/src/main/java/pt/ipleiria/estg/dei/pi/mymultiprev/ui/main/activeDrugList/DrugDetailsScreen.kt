package pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.activeDrugList

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import pt.ipleiria.estg.dei.pi.mymultiprev.R

enum class TabPage() {
    Detalhes(),
    Tomas(),
    MaisInformacao()
}

@Composable
fun DrugDetailsScreen() {
    Column() {
        AppBar()
        Pager()
    }
}

@Composable
fun AppBar() {
    Card {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
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
                onClick = { /*TODO*/ }) {
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
                text = "Amoxixalina"
            )

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
fun Pager() {

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
                                Details()
                            }
                            1 -> {
                                LazyColumn() {
                                    items(10) {
                                        Tomas()
                                    }
                                }
                            }

                            2 -> {
                                MoreDetails()
                            }
                        }
                    }
                }
            }
        })
    }
}

@Composable
fun Details() {
    Column() {
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
                text = "Vibramicina"
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
                text = "Doxiciclina"
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
                text = "500.0 mg"
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
                text = "Comprimido Dispersível"
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
                text = "7h"
            )
        }
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
                text = "2022-04-13 08:22"
            )
        }
    }
}

@Composable
fun Tomas() {
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

                text = "12/04/2021 23:30"
            )
        }
    }
}

@Composable
fun MoreDetails() {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp).clickable {  }) {
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            text = "Classe Farmacêutica:"
        )
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp),
            fontSize = 18.sp,
            text = "Exemplo"
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
            text = "Exemplo 2"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppBarPreview() {
//    Tomas()
//    Pager()
    DrugDetailsScreen()
}

//@Preview
//@Composable
//fun DetailsPreview() {
//    Details()
//}