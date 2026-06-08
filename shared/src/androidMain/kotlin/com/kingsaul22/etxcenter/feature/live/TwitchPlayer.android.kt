package com.kingsaul22.etxcenter.feature.live

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
actual fun TwitchPlayer(
    channel: String,
    modifier: Modifier
) {
    val context = LocalContext.current
    val webView = remember {
        WebView(context).apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                mediaPlaybackRequiresUserGesture = false
            }
            webViewClient = WebViewClient()

            // Twitch requires a valid "parent" parameter. For local WebViews, 
            // "localhost" is a special-cased supported value.
            loadUrl("https://player.twitch.tv/?channel=$channel&parent=localhost&autoplay=true&muted=true")
        }
    }

    // Asegura la limpieza de memoria al abandonar la composición
    DisposableEffect(Unit) {
        onDispose {
            webView.destroy()
        }
    }

    AndroidView(
        factory = { webView },
        modifier = modifier
    )
}
