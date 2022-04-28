package pt.ipleiria.estg.dei.pi.mymultiprev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.MainScreen
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.login.LoginScreen
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.activeDrugList.ActiveDrugListScreen
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.theme.MyMultiPrevTheme

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
fun MyMultiPrev() {

    val navController = rememberNavController()


    NavHost(navController = navController, startDestination = "mainScreen") {

        composable("login") {
            LoginScreen(){

            }
        }

        composable("mainScreen") {
            MainScreen()
        }

//        composable("dishes/{category}", arguments = listOf(navArgument("category") {
//            type = NavType.StringType
//        })) {
//
//            var categoryStr = remember {
//                it.arguments?.getString("category")
//            }
//
//
//
//            DishesScreen(category = categoryStr){ dishId ->
//                navController.navigate("detail/${dishId}")
//            }
//        }
//
//        composable("detail/{mealId}", arguments = listOf(navArgument("mealId") {
//            type = NavType.StringType
//        })) {
//            var mealStrId = remember {
//                it.arguments?.getString("mealId")
//            }
//
//            DetailScreen(mealID = mealStrId)
//        }
//
//        // DishesScreen
//        // CategoryScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyMultiPrevTheme {
//        Greeting("Android")
    }
}