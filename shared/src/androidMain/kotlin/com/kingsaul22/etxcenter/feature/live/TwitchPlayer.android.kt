package com.kingsaul22.etxcenter.feature.live

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
actual fun TwitchPlayer(channel: String, modifier: Modifier) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    mediaPlaybackRequiresUserGesture = false
                }

                CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
                webViewClient = WebViewClient()
                webChromeClient = WebChromeClient()

                loadUrl("https://twitch.kingsaul22.net/?channel=$channel")
            }
        },
        modifier = modifier,
        onRelease = { webView ->
            webView.stopLoading()
            webView.destroy()
        }
    )
}