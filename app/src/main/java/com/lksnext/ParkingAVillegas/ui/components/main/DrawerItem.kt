package com.lksnext.ParkingAVillegas.ui.components.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lksnext.ParkingAVillegas.ui.theme.OrangeLKS

@Composable
fun DrawerItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    NavigationDrawerItem(
        label = {
            Text(
                text = label,
                fontWeight =
                    if (isSelected)
                        FontWeight.Bold
                    else
                        FontWeight.Normal
            )
        },

        selected = isSelected,

        onClick = onClick,

        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        },

        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            selectedContainerColor = Color.Transparent,

            selectedIconColor = OrangeLKS,
            selectedTextColor = OrangeLKS,

            unselectedIconColor = Color.Gray,
            unselectedTextColor = Color.Black
        ),

        modifier = Modifier.padding(horizontal = 12.dp)
    )
}