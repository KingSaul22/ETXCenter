package com.kingsaul22.etxcenter.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

/**
 * Displays a team logo loaded from a remote URL, falling back to a
 * [Shield][Icons.Default.Shield] icon when the URL is null, blank,
 * or fails to load.
 *
 * The caller controls sizing and clipping through [modifier], e.g.:
 * ```
 * TeamLogo(
 *     logoUrl = team.logoUrl,
 *     modifier = Modifier.size(48.dp).clip(CircleShape),
 *     contentDescription = team.name
 * )
 * ```
 *
 * @param logoUrl       Remote image URL; `null` or blank triggers the fallback icon.
 * @param modifier      Controls size, clipping, and background.
 * @param contentDescription Accessibility label for the image.
 */
@Composable
fun TeamLogo(
    logoUrl: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val fallbackPainter = rememberVectorPainter(Icons.Default.Shield)

    if (logoUrl.isNullOrBlank()) {
        // Fast-path: skip network entirely when there's no URL
        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        AsyncImage(
            model = logoUrl,
            contentDescription = contentDescription,
            placeholder = fallbackPainter,
            error = fallbackPainter,
            fallback = fallbackPainter,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    }
}
