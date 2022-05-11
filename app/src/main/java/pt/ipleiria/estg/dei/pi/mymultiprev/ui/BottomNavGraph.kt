package pt.ipleiria.estg.dei.pi.mymultiprev.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.MainViewModel
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.activeDrugList.ActiveDrugListScreen
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.drugDetails.DrugDetailsScreen


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

        composable(route = BottomBarScreen.Historico.route) {
            Text(text = "HISTORICO")
        }

        composable(
            route = "descricaoAntibiotico/{prescription}/{drug}",
            arguments = listOf(
                navArgument("drug") {
                    type = NavType.StringType
                },
                navArgument("prescription") {
                    type = NavType.StringType
                })
        ) {
            var drugId = remember {
                it.arguments?.getString("drug")
            }
            var prescriptionId = remember {
                it.arguments?.getString("prescription")
            }
//            Log.i("BottomNavGraph", prescriptionId.toString())
            DrugDetailsScreen(drugId = drugId!!, prescriptionId = prescriptionId!!)
        }
    }
}