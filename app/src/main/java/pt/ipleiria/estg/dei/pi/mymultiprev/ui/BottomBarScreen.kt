package pt.ipleiria.estg.dei.pi.mymultiprev.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Healing
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.PlaylistAddCheck
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector,
    var badgeCount: Int
) {
    object Antibioticos : BottomBarScreen(
        route = "antibioticos",
        title = "Antibióticos",
        icon = Icons.Outlined.PlaylistAddCheck,
        badgeCount = 0
    )

    object Sintomas : BottomBarScreen(
        route = "sintomas",
        title = "Registar Sintomas",
        icon = Icons.Outlined.Healing,
        badgeCount = 0
    )

    object Historico : BottomBarScreen(
        route = "historico",
        title = "Histórico",
        icon = Icons.Outlined.History,
        badgeCount = 0
    )
}