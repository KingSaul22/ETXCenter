package com.kingsaul22.etxcenter

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "ETXCenter",
    ) {
        App()
    }
}