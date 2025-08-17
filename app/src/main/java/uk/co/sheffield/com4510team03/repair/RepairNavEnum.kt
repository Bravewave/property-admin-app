package uk.co.sheffield.com4510team03.repair

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import uk.co.sheffield.com4510team03.R

enum class RepairNavEnum {
    SHOW, CONTRACTORS_DETAILS, REPAIRED
}

data class RepairNavBarItem(
    val title: String,
    val screenEnum: RepairNavEnum,
    val selectedIcon: @Composable () -> Unit,
    val unselectedIcon: @Composable () -> Unit,
) {

    @Composable
    fun IconStyled(currentEnum: RepairNavEnum) {
        if (screenEnum == currentEnum) {
            selectedIcon()
        } else {
            unselectedIcon()
        }
    }
}

val NavBarItems = listOf(
    RepairNavBarItem(title = "Repairs", screenEnum = RepairNavEnum.SHOW,
        selectedIcon = { Icon(Icons.Filled.Build, contentDescription = "See Contractors") },
        unselectedIcon = {
            Icon(Icons.Outlined.Build, contentDescription = "See Contractors")
        }
    ),
    RepairNavBarItem(title = "Contractors", screenEnum = RepairNavEnum.CONTRACTORS_DETAILS,
        selectedIcon = { Icon(Icons.Filled.Face, contentDescription = "See Contractors") },
        unselectedIcon = { Icon(Icons.Outlined.Face, contentDescription = "See Contractors") }),
    RepairNavBarItem(title = "Repaired", screenEnum = RepairNavEnum.REPAIRED,
        selectedIcon = {
            Icon(
                painter = painterResource(R.drawable.baseline_inventory_2_24),
                contentDescription = "Repaired"
            )
        },
        unselectedIcon = {
            Icon(
                painter = painterResource(R.drawable.outline_inventory_2_24),
                contentDescription = "Repaired "
            )
        }
    )
)
