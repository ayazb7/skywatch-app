package com.skywatch.skywatch_app.presentation.views

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

@Composable
fun SkyWatchLogo() {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(GradientPink, GradientBlue)
    )

    Text(
        buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    brush = gradientBrush,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            ) {
                append("sky")
            }
            withStyle(
                style = SpanStyle(
                    color = TextBlack,
                    fontWeight = FontWeight.Normal,
                    fontSize = 28.sp
                )
            ) {
                append("watch")
            }
        }
    )
}

