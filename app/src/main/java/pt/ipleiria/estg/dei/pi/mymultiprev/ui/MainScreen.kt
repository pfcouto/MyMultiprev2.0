package pt.ipleiria.estg.dei.pi.mymultiprev.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.ui.main.activeDrugList.ActiveDrugListViewModel

@Composable
fun MainScreen(
    navControllerLogin: NavHostController,
    activeViewModel: ActiveDrugListViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val listOfPrescriptions by activeViewModel.prescriptionItems.observeAsState()
    Scaffold(
        bottomBar = {
            BottomBar(listOfPrescriptions?.data, navController = navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            BottomNavGraph(navController = navController, navControllerLogin = navControllerLogin)
        }
    }
}

@Composable
fun BottomBar(listOfPrescriptions: List<PrescriptionItem>?, navController: NavHostController) {
    val screens = listOf(
        BottomBarScreen.Antibioticos,
        BottomBarScreen.Sintomas,
        BottomBarScreen.Historico
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    BottomNavigation() {
        screens.forEach { screen ->
            if (screen.route == "antibioticos") {
                if (listOfPrescriptions != null) {
                    screen.badgeCount = listOfPrescriptions.size
                }
            }
            AddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    BottomNavigationItem(label = {
        Text(text = screen.title)
    },
        icon = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (screen.badgeCount > 0) {
                    BadgedBox(badge = {
                        Badge {
                            Text(text = screen.badgeCount.toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }) {
                        Icon(imageVector = screen.icon, contentDescription = "NavigationIcon")
                    }
                } else {
                    Icon(imageVector = screen.icon, contentDescription = "NavigationIcon")
                }
            }
        },
        selected = currentDestination?.hierarchy?.any {
            it.route == screen.route
        } == true,
        onClick = { navController.navigate(screen.route) })
}