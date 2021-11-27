package com.patloew.oeffitracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/* Copyright 2021 Patrick Löwenstein
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */

private val DarkColorPalette = darkColors(
    primary = Green300,
    primaryVariant = Green300Variant,
    secondary = TealA700,
    secondaryVariant = TealA700Variant,
    surface = Color(0xFF141414),
    background = Color.Black
)

private val LightColorPalette = lightColors(
    primary = Green700,
    primaryVariant = Green700Variant,
    secondary = TealA400,
    secondaryVariant = TealA400Variant,
    surface = Color.White,
    background = Color(0xFFFAFAFA)
)

@Composable
fun OeffiTrackerTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}