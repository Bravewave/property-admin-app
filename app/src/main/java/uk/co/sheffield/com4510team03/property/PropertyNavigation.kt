package uk.co.sheffield.com4510team03.property

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun PropertyNavigation(
    currentScreen: PropertyNavEnum,
    setCurrentScreen: (PropertyNavEnum) -> Unit
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