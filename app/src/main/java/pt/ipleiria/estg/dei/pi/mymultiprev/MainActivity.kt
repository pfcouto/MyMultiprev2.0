package pt.ipleiria.estg.dei.pi.mymultiprev

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.CameraScreen
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.MainScreen
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.login.LoginScreen
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.login.LoginViewModel
import java.io.File
import java.util.concurrent.Executor

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
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

    NavHost(navController = navController, startDestination = "teste") {
        composable("teste") {
            CameraScreen()
        }

        composable("login") {
            LoginScreen() {
                navController.navigate("mainScreen")
            }
        }

        composable("mainScreen") {
            MainScreen(navController)
        }
    }
}