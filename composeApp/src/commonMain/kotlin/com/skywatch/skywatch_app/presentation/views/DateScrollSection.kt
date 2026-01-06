package com.skywatch.skywatch_app.presentation.views

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DateScrollSection(
    selectedDate: String,
    onPreviousDate: () -> Unit,
    onNextDate: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousDate) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Previous Date",
                tint = TextGray
            )
        }

        Text(
            text = selectedDate,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextBlack,
            modifier = Modifier
                .border(1.dp, BorderGray, RoundedCornerShape(20.dp))
                .padding(horizontal = 20.dp, vertical = 10.dp)
        )

        IconButton(onClick = onNextDate) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Next Date",
                tint = TextGray
            )
        }
    }
}

