package com.example.ui.components

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.viewinterop.AndroidView
import io.livekit.android.renderer.SurfaceViewRenderer
import livekit.org.webrtc.RendererCommon

/**
 * Full-screen edge-to-edge LiveKit VideoRenderer with SCALE_ASPECT_FILL
 * ensuring zero black bars or letterboxing across Android devices.
 */
@Composable
fun LiveKitVideoRenderer(
    modifier: Modifier = Modifier,
    scalingType: RendererCommon.ScalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL,
    contentScale: ContentScale = ContentScale.Crop
) {
    var hasRendererError by remember { mutableStateOf(false) }

    if (!hasRendererError) {
        AndroidView(
            factory = { context ->
                try {
                    SurfaceViewRenderer(context).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        // Apply SCALE_ASPECT_FILL to eliminate letterboxing / black bars
                        setScalingType(scalingType)
                        setEnableHardwareScaler(true)
                    }
                } catch (e: Throwable) {
                    hasRendererError = true
                    android.view.View(context)
                }
            },
            update = { view ->
                if (view is SurfaceViewRenderer) {
                    try {
                        view.setScalingType(scalingType)
                    } catch (e: Throwable) {
                        hasRendererError = true
                    }
                }
            },
            modifier = modifier.fillMaxSize()
        )
    } else {
        // High quality full-screen fallback canvas when hardware EGL is restricted
        Canvas(modifier = modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF2E1065),
                        Color(0xFF090A10)
                    )
                )
            )
        }
    }
}
