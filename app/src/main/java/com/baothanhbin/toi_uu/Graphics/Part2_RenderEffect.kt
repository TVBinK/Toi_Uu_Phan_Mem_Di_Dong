package com.baothanhbin.toi_uu.Graphics

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.baothanhbin.toi_uu.R

@Composable
fun Part2_RenderEffectDemo() {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("PHẦN 2: RenderEffect (GPU)", style = MaterialTheme.typography.titleLarge)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            DemoBlur()
            DemoColorMatrix()
            DemoGlassmorphism()
        } else {
            Text("⚠️ Yêu cầu Android 12+ (API 31+)", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun DemoBlur() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
    
    Text("1) Blur Effect", style = MaterialTheme.typography.titleMedium)
    var blur by remember { mutableFloatStateOf(0f) }

    Image(
        painterResource(R.drawable.bg), null,
        Modifier.size(200.dp).graphicsLayer {
            renderEffect = if (blur > 0f) {
                android.graphics.RenderEffect.createBlurEffect(
                    blur, blur, android.graphics.Shader.TileMode.CLAMP
                ).asComposeRenderEffect()
            } else null
        },
        contentScale = ContentScale.Crop
    )

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { blur = 0f }) { Text("0") }
        Button(onClick = { blur = 10f }) { Text("10") }
        Button(onClick = { blur = 25f }) { Text("25") }
    }
}

@Composable
private fun DemoColorMatrix() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
    
    Text("2) Color Matrix", style = MaterialTheme.typography.titleMedium)
    var mode by remember { mutableStateOf(0) }

    Image(
        painterResource(R.drawable.img_gemini), null,
        Modifier.size(200.dp).graphicsLayer {
            renderEffect = when (mode) {
                0 -> null
                1 -> android.graphics.RenderEffect.createColorFilterEffect(
                    android.graphics.ColorMatrixColorFilter(
                        android.graphics.ColorMatrix(floatArrayOf(
                            0.33f, 0.33f, 0.33f, 0f, 0f,
                            0.33f, 0.33f, 0.33f, 0f, 0f,
                            0.33f, 0.33f, 0.33f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        ))
                    )
                ).asComposeRenderEffect()
                else -> android.graphics.RenderEffect.createColorFilterEffect(
                    android.graphics.ColorMatrixColorFilter(
                        android.graphics.ColorMatrix(floatArrayOf(
                            0.393f, 0.769f, 0.189f, 0f, 0f,
                            0.349f, 0.686f, 0.168f, 0f, 0f,
                            0.272f, 0.534f, 0.131f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        ))
                    )
                ).asComposeRenderEffect()
            }
        },
        contentScale = ContentScale.Crop
    )

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { mode = 0 }) { Text("Normal") }
        Button(onClick = { mode = 1 }) { Text("Gray") }
        Button(onClick = { mode = 2 }) { Text("Sepia") }
    }
}

@Composable
private fun DemoGlassmorphism() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
    
    Text("3) Glassmorphism", style = MaterialTheme.typography.titleMedium)

    Box(Modifier.fillMaxWidth().height(250.dp)) {
        Image(painterResource(R.drawable.bg), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        
        Box(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(120.dp)
                .graphicsLayer {
                    renderEffect = android.graphics.RenderEffect.createBlurEffect(
                        25f, 25f, android.graphics.Shader.TileMode.CLAMP
                    ).asComposeRenderEffect()
                }
                .background(Color.Black.copy(alpha = 0.3f))
        ) {
            Text(
                "Glassmorphism\n✨ Blur + Transparency",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center).padding(16.dp)
            )
        }
    }
}
