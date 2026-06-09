package com.kingsaul22.etxcenter.feature.live

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.WebKit.WKAudiovisualMediaTypesNone
import platform.Foundation.NSURL
import platform.CoreGraphics.CGRectZero

@Composable
actual fun TwitchPlayer(
    channel: String,
    modifier: Modifier
) {
    UIKitView(
        factory = {
            val configuration = WKWebViewConfiguration().apply {
                allowsInlineMediaPlayback = true
                mediaTypesRequiringUserActionForPlayback = WKAudiovisualMediaTypesNone
            }
            
            val webView = WKWebView(frame = CGRectZero, configuration = configuration)
            
            // On iOS, custom/file origins often break Twitch's parent domain validation.
            // We bypass this by loading an HTML iframe wrapper with a baseURL of https://localhost.
            val html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
                    <style>
                        body, html { margin: 0; padding: 0; width: 100%; height: 100%; overflow: hidden; background-color: #000000; }
                        iframe { border: none; width: 100%; height: 100%; }
                    </style>
                </head>
                <body>
                    <iframe 
                        src="https://player.twitch.tv/?channel=$channel&parent=localhost&autoplay=true&muted=true" 
                        allowfullscreen="true">
                    </iframe>
                </body>
                </html>
            """.trimIndent()
            
            webView.loadHTMLString(html, baseURL = NSURL.URLWithString("https://localhost"))
            webView
        },
        modifier = modifier
    )
}
