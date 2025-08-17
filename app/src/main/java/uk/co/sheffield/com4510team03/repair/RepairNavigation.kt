package uk.co.sheffield.com4510team03.repair

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun RepairNavigation(
    currentScreen: RepairNavEnum,
    setCurrentScreen: (RepairNavEnum) -> Unit
) {

    NavigationBar {
        NavBarItems.forEach { item ->
            NavigationBarItem(
                label = { Text(item.title) },
                icon = { item.IconStyled(currentScreen) },
                selected = item.screenEnum == currentScreen,
                onClick = { setCurrentScreen(item.screenEnum) },
            )
        }
    }
}