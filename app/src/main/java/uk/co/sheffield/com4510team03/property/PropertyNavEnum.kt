package uk.co.sheffield.com4510team03.property

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import uk.co.sheffield.com4510team03.R

enum class PropertyNavEnum {
    HOMES, ARCHIVED
}

data class PropNavBarItem(
    val title: String,
    val screenEnum: PropertyNavEnum,
    val selectedIcon: @Composable () -> Unit,
    val unselectedIcon: @Composable () -> Unit,
) {

    @Composable
    fun IconStyled(currentEnum: PropertyNavEnum) {
        if (screenEnum == currentEnum) {
            selectedIcon()
        } else {
            unselectedIcon()
        }
    }
}

val NavBarItems = listOf(
    PropNavBarItem(title = "Properties", screenEnum = PropertyNavEnum.HOMES,
        selectedIcon = { Icon(Icons.Filled.Home, contentDescription = "Properties") },
        unselectedIcon = { Icon(Icons.Outlined.Home, contentDescription = "Properties") }),
    PropNavBarItem(title = "Archived", screenEnum = PropertyNavEnum.ARCHIVED,
        selectedIcon = {
            Icon(
                painter = painterResource(R.drawable.baseline_inventory_2_24),
                contentDescription = "Archived"
            )
        },
        unselectedIcon = {
            Icon(
                painter = painterResource(R.drawable.outline_inventory_2_24),
                contentDescription = "Archived "
            )
        }
    )
)