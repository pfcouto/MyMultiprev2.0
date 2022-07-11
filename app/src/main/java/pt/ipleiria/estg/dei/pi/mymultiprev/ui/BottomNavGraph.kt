package pt.ipleiria.estg.dei.pi.mymultiprev.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.MainViewModel
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.activeDrugList.ActiveDrugListScreen
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.confirmAcquisition.ConfirmAcquisitionScreen
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.confirmNewIntake.ConfirmIntakeDetailsScreen
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.drugDetails.DrugDetailsScreen
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.prescriptionItemsHistory.PrescriptionItemsHistoryScreen
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.register_symptoms.RegisterSymptomsScreen
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.theme.Teal

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomNavGraph(
    navController: NavHostController,
    navControllerLogin: NavHostController,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Antibioticos.route
    ) {

        composable(route = BottomBarScreen.Antibioticos.route) {
            ActiveDrugListScreen(navController = navController) {
                navControllerLogin.navigate("login")
            }
        }

        composable(
            route = BottomBarScreen.Sintomas.route
        ) {
            if (!mainViewModel.isNetworkAvailable()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
//                        shape = RoundedCornerShape(25),
                        modifier = Modifier
                            .background(MaterialTheme.colors.background).border(BorderStroke(1.dp, Teal), RoundedCornerShape(25))
                        ,
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(8.dp),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colors.onSurface,
                            textAlign = TextAlign.Center,
                            text = "Sem conexão à Internet, por favor conecte-se à mesma e tente novamente!"
                        )
                    }

                }
            } else {
                RegisterSymptomsScreen(navHostController = navController)
            }
        }

        composable(
            route = BottomBarScreen.Sintomas.route + "/{prescItemId}",
            arguments = listOf(
                navArgument("prescItemId") {
                    type = NavType.StringType
                })
        ) {
            var prescItemId = remember {
                it.arguments?.getString("prescItemId")
            }
            Log.i("TESTE ROUTE", BottomBarScreen.Sintomas.route)
            Log.i("TESTE VAR", prescItemId.toString())

            if (!mainViewModel.isNetworkAvailable()) {
                Snackbar() {
                    Text(text = "No Internet Connection! Please, reconnect and try again")
                }
            } else {
                RegisterSymptomsScreen(
                    navHostController = navController,
                    prescriptionItemId = prescItemId
                )
            }
        }

        composable(route = BottomBarScreen.Historico.route) {
            PrescriptionItemsHistoryScreen(navController = navController)
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
            DrugDetailsScreen(
                drugId = drugId!!,
                prescriptionId = prescriptionId!!,
                navController = navController,
                navControllerOutsideLoginScope = navControllerLogin
            )
        }

        composable(
            route = "newIntakeDetailsScreen/{prescriptionItem}/{drug}", arguments = listOf(
                navArgument("prescriptionItem") {
                    type = NavType.StringType
                }, navArgument("drug") {
                    type = NavType.StringType
                })
        ) {
            val drugId = remember {
                it.arguments?.getString("drug")
            }
            val prescriptionItemId = remember {
                it.arguments?.getString("prescriptionItem")
            }
            ConfirmIntakeDetailsScreen(
                navController = navController,
                drugId = drugId!!,
                prescriptionItemId = prescriptionItemId!!
            )
        }



        composable(route = "confirmAcquisitionScreen/{prescriptionItem}/{drug}", arguments = listOf(
            navArgument("prescriptionItem") {
                type = NavType.StringType
            }, navArgument("drug") {
                type = NavType.StringType
            })
        ) {
            val drugId = remember {
                it.arguments?.getString("drug")
            }
            val prescriptionItemId = remember {
                it.arguments?.getString("prescriptionItem")
            }
            ConfirmAcquisitionScreen(
                navController = navController,
                drugId = drugId!!,
                prescriptionItemId = prescriptionItemId!!
            )
        }
    }
}