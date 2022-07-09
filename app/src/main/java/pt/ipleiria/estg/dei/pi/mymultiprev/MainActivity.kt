    package pt.ipleiria.estg.dei.pi.mymultiprev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.CameraScreen
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.MainScreen
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.login.LoginScreen
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.login.LoginViewModel
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.theme.MyMultiPrevTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyMultiPrevTheme {
                MyMultiPrev()
            }
        }
    }
}

@Composable
fun MyMultiPrev(
    loginViewModel: LoginViewModel = hiltViewModel()
) {

    val navController = rememberNavController()

    var starDest = if (loginViewModel.isLoggedIn) "mainScreen" else "login"

    NavHost(navController = navController, startDestination = starDest) {

        composable("login") {
            LoginScreen() {
                navController.navigate("mainScreen")
            }
        }

        composable("mainScreen") {
            MainScreen(navController)
        }

        composable(
            route = "drugDetailsScreenCamera/{prescriptionId}", arguments = listOf(
                navArgument("prescriptionId") {
                    type = NavType.StringType
                })
        ) {
            var prescriptionId = remember {
                it.arguments?.getString("prescriptionId")
            }
            CameraScreen(
                prescriptionId = prescriptionId!!,
                navController = navController
            )
        }
    }
}
