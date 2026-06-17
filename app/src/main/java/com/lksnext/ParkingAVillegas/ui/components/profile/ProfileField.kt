package com.lksnext.ParkingAVillegas.ui.components.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lksnext.ParkingAVillegas.ui.theme.OrangeLKS

@Composable
fun ProfileField(label: String, value: String, isReadOnly: Boolean, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = isReadOnly,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isReadOnly) Color.LightGray else OrangeLKS,
                unfocusedBorderColor = Color.LightGray
            )
        )
    }
}