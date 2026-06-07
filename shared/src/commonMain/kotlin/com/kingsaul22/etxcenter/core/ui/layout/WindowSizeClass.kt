package com.kingsaul22.etxcenter.core.ui.layout

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

enum class WindowWidthSizeClass { Compact, Medium, Expanded }

enum class WindowHeightSizeClass { Compact, Medium, Expanded }

data class WindowSizeClass(
    val widthSizeClass: WindowWidthSizeClass,
    val heightSizeClass: WindowHeightSizeClass
)

val LocalWindowSizeClass = staticCompositionLocalOf {
    WindowSizeClass(WindowWidthSizeClass.Compact, WindowHeightSizeClass.Compact)
}

@Composable
fun BoxWithConstraintsScope.calculateWindowSizeClass(): WindowSizeClass {
    val widthClass = when {
        maxWidth < 600.dp -> WindowWidthSizeClass.Compact
        maxWidth < 840.dp -> WindowWidthSizeClass.Medium
        else -> WindowWidthSizeClass.Expanded
    }
    val heightClass = when {
        maxHeight < 480.dp -> WindowHeightSizeClass.Compact
        maxHeight < 900.dp -> WindowHeightSizeClass.Medium
        else -> WindowHeightSizeClass.Expanded
    }
    return WindowSizeClass(widthClass, heightClass)
}
