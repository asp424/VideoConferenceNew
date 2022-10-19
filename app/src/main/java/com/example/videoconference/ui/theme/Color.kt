package com.example.videoconference.ui.theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)
val Navy500 = Color(0xFF64869B)
val Navy700 = Color(0xFFA6EEA9)
val Navy500_1 = Color(0xFF6D93AA)
val Navy700_1 = Color(0xFF2962FF)
val Navy900 = Color(0xFF073042)
val Green300 = Color(0xFF3DDC84)
val Green900 = Color(0xFF00A956)
val Yellow900 = Color(0xFFFDD835)
val DarkGreen = Color(0xFF257015)

val GreenBar = Color(0xFF148F18)
val Eye = Color(0xFF047C08)
val A = Color(0xFF009688)
val listColors = listOf(
    Green900, Color.Blue,
    Color.Magenta, Color.Red, Teal200, Navy900, Navy500, Navy500_1, Purple200, Purple500, Green300
)
val LightColors = lightColors(
    primary = Navy700_1,
    primaryVariant = Navy900,
    secondary = Green300,
    secondaryVariant = Green900
    // Using default values for onPrimary, surface, error, etc.
)

val DarkColors = darkColors(
    primary = Navy500_1,
    primaryVariant = Navy900,
    secondary = Green300
    // secondaryVariant == secondary in dark theme
)