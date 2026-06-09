package com.kingsaul22.etxcenter.feature.live

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A platform-aware Twitch player.
 * On mobile platforms (Android/iOS), it embeds a native WebView/WKWebView containing the Twitch player.
 * On Desktop platforms, it displays a polished fallback UI to open the stream in the user's default browser.
 */
@Composable
expect fun TwitchPlayer(
    channel: String,
    modifier: Modifier = Modifier
)
