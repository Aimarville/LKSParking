package com.example.lksparking.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lksparking.ui.theme.OrangeLKS

data class NotificationItem(
    val id: Int,
    val title: String,
    val description: String,
    val timestamp: String,
    val type: NotificationType,
    val isNew: Boolean = true
)

enum class NotificationType {
    CONFIRMED, REMINDER
}

@Composable
fun NotificationsPanel(
    isVisible: Boolean,
    onClose: () -> Unit,
    onMarkAllAsRead: () -> Unit
) {
    val notifications = listOf(
        NotificationItem(
            1,
            "Reserva confirmada",
            "Tu reserva para mañana en la plaza A-01 ha sido confirmada.",
            "10 de marzo a las 10:30",
            NotificationType.CONFIRMED
        ),
        NotificationItem(
            2,
            "Recordatorio",
            "Tu reserva comienza en 1 hora.",
            "12 de marzo a las 14:00",
            NotificationType.REMINDER
        )
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it })
    ) {
        Surface(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.85f)
                .statusBarsPadding(),
            color = Color.White,
            tonalElevation = 8.dp,
            shadowElevation = 16.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notificaciones",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.Gray)
                    }
                }

                // Mark all as read
                Text(
                    text = "MARCAR TODAS COMO LEÍDAS",
                    modifier = Modifier
                        .clickable { onMarkAllAsRead() }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = OrangeLKS,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider(modifier = Modifier.padding(top = 8.dp), thickness = 0.5.dp)

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(notifications) { notification ->
                        NotificationRow(notification)
                        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationRow(notification: NotificationItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (notification.isNew) Color(0xFFF9F9F9) else Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        val icon: ImageVector
        val iconColor: Color
        
        when (notification.type) {
            NotificationType.CONFIRMED -> {
                icon = Icons.Default.CheckCircle
                iconColor = Color(0xFF4CAF50)
            }
            NotificationType.REMINDER -> {
                icon = Icons.Default.Info
                iconColor = Color(0xFF03A9F4)
            }
        }

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = notification.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.Black
                )
                if (notification.isNew) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = OrangeLKS,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Nuevo",
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Text(
                text = notification.description,
                fontSize = 14.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            Text(
                text = notification.timestamp,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}
