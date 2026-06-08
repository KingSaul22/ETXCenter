package com.kingsaul22.etxcenter.feature.live

import android.annotation.SuppressLint
import android.webkit.CookieManager
import android.webkit.WebChromeClient
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
                
                // Twitch blocks standard WebView user agents. 
                // We strip the WebView signature ("; wv" and "Version/4.0") 
                // to make it appear as a standard Chrome browser.
                val defaultUserAgent = userAgentString
                userAgentString = defaultUserAgent
                    .replace("; wv", "")
                    .replace("Version/4.0 ", "")
            }
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient() // Essential for HTML5 video/media rendering
            
            // Allow Twitch embed domains to set cookies
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

            loadDataWithBaseURL(
                "https://localhost",
                """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
                    <style>
                        body,html{margin:0;padding:0;width:100%;height:100%;overflow:hidden;background:#000}
                        iframe{border:none;width:100%;height:100%}
                    </style>
                </head>
                <body>
                    <iframe src="https://player.twitch.tv/?channel=$channel&parent=localhost&autoplay=true&muted=true"
                            allowfullscreen="true"></iframe>
                </body>
                </html>
                """.trimIndent(),
                "text/html", "UTF-8", null
            )
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

