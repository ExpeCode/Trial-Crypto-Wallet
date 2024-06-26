package com.app.trialcryptowallet.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.app.trialcryptowallet.screens.Screen

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    currentRoute: String? = null,
    items: List<Screen>,
    onSelectedItem: (navController: NavHostController, id: String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        tonalElevation = 8.dp
    ) {
        //val currentRoute = currentRoute(navController)
        val contentColor = MaterialTheme.colorScheme.onPrimary
        val unselectedContentColor = contentColor.copy(alpha = ContentAlpha.medium)

        items.forEach { item ->
            val selected = currentRoute == item.id
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconResId),
                        contentDescription = stringResource(item.titleResId),
                        tint = if (selected) contentColor else unselectedContentColor
                    )
                },
                label = {
                    Text(
                        text = stringResource(item.titleResId),
                        color = if (selected) contentColor else unselectedContentColor
                    )
                },
                selected = selected,
                onClick = {
                    onSelectedItem(navController, item.id)
                },
                alwaysShowLabel = true
            )
        }
    }
}

object ContentAlpha {
    const val high = 1.0f
    const val medium = 0.74f
    const val disabled = 0.38f
}