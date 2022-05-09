package pt.ipleiria.estg.dei.pi.mymultiprev.ui

import android.util.Log
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.activeDrugList.ActiveDrugListScreen
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.activeDrugList.DrugDetailsScreen
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.confirmNewIntake.ConfirmIntakeDetailsScreen

@Composable
fun BottomNavGraph(navController: NavHostController, navControllerLogin: NavHostController) {
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
            Text(text = "SINTOMAS")
        }

        composable(route = BottomBarScreen.Historico.route) {
            Text(text = "HISTORICO")
        }

        composable(route = "descricaoAntibiotico") {
            DrugDetailsScreen()
        }

        composable(
            route = "newIntakeDetailsScreen/{prescriptionItem}/{drug}", arguments = listOf(
                navArgument("prescriptionItem") {
                    type = NavType.StringType
                }, navArgument("drug") {
                    type = NavType.StringType
                })
        ) {
            var drugId = remember {
                it.arguments?.getString("drug")
            }
            var prescriptionItemId = remember {
                it.arguments?.getString("prescriptionItem")
            }
            Log.d("Aqui", "")
            ConfirmIntakeDetailsScreen(navController = navController, drugId = drugId!!, prescriptionItemId = prescriptionItemId!!)
        }
    }
}