package com.lksnext.ParkingAVillegas.ui.components.main

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lksnext.ParkingAVillegas.ui.theme.OrangeLKS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    notificationCount: Int,
    onMenuClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {

    TopAppBar(

        title = {

            Row {

                Icon(
                    Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = Color.White
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    "Garaje",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },

        navigationIcon = {

            IconButton(
                onClick = onMenuClick
            ) {

                Icon(
                    Icons.Default.Menu,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        },

        actions = {

            IconButton(
                onClick = onNotificationsClick
            ) {

                BadgedBox(
                    badge = {

                        if (notificationCount > 0) {

                            Badge {
                                Text("$notificationCount")
                            }
                        }
                    }
                ) {

                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        },

        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = OrangeLKS
        )
    )
}