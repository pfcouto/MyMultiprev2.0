package pt.ipleiria.estg.dei.pi.mymultiprev.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen(
    navControllerLogin: NavHostController
) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomBar(navController = navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            BottomNavGraph(navController = navController, navControllerLogin = navControllerLogin)
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        BottomBarScreen.Antibioticos,
        BottomBarScreen.Sintomas,
        BottomBarScreen.Historico
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    BottomNavigation() {
        screens.forEach { screen ->
            AddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
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
                        Text(text = screen.badgeCount.toString())
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