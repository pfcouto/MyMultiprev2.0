package pt.ipleiria.estg.dei.pi.mymultiprev.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import okhttp3.internal.wait
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.MainViewModel
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.activeDrugList.ActiveDrugListScreen
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.activeDrugList.DrugDetailsScreen


@Composable
fun BottomNavGraph(
    navController: NavHostController,
    navControllerLogin: NavHostController,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    //TODO VER NAVS CONTROLLERS (2 para 1)


    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Antibioticos.route
    ) {

        composable(route = BottomBarScreen.Antibioticos.route) {
            ActiveDrugListScreen(navController = navController) {
                navControllerLogin.navigate("login")
            }
        }

        composable(route = BottomBarScreen.Sintomas.route) {
            val context = LocalContext.current

            Text(text = "SINTOMAS")
            Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Bottom) {
                if (!mainViewModel.isNetworkAvailable()) {
//                Toast.makeText(
//                    context,
//                    "No Internet Connection! Please, reconnect and try again",
//                    Toast.LENGTH_SHORT
//                )
                    Snackbar() {
                        Text(text = "No Internet Connection! Please, reconnect and try again")
                    }
                }
            }
        }

        composable(route = BottomBarScreen.Historico.route) {
            Text(text = "HISTORICO")
        }

        composable(route = "descricaoAntibiotico") {
            DrugDetailsScreen()
        }
    }
}