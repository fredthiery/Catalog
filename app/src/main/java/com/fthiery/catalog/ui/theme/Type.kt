package com.fthiery.catalog.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.fthiery.catalog.R

val JosefinSans = FontFamily(
    Font(R.font.josefinsans_regular),
    Font(R.font.josefinsans_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.josefinsans_bold, FontWeight.Bold),
    Font(R.font.josefinsans_bolditalic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.josefinsans_extralight, FontWeight.ExtraLight),
    Font(R.font.josefinsans_extralightitalic, FontWeight.ExtraLight, FontStyle.Italic)
)

// Set of Material typography styles to start with
val Typography = Typography(
    defaultFontFamily = JosefinSans,
    button = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)