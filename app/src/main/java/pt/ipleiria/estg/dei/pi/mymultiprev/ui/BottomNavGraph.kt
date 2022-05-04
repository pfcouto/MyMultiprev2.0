package pt.ipleiria.estg.dei.pi.mymultiprev.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.activeDrugList.ActiveDrugListScreen
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.activeDrugList.DrugDetailsScreen

@Composable
fun BottomNavGraph(navController: NavHostController, navControllerLogin : NavHostController) {
    //TODO VER NAVS CONTROLLERS (2 para 1)
    NavHost(navController = navController,
        startDestination = BottomBarScreen.Antibioticos.route) {

        composable(route = BottomBarScreen.Antibioticos.route) {
            ActiveDrugListScreen(navController = navController){
                navControllerLogin.navigate("login")
            }
        }

        composable(route = BottomBarScreen.Sintomas.route) {
            Text(text = "SINTOMAS")
        }

        composable(route = BottomBarScreen.Historico.route) {
            Text(text = "HISTORICO")
        }

        composable(route = "descricaoAntibiotico") {
            DrugDetailsScreen()
        }
    }
}