package com.baothanhbin.toi_uu.Graphics

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.baothanhbin.toi_uu.R

@Composable
fun Part3_AGSLShaderDemo() {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("PHẦN 3: AGSL Shader", style = MaterialTheme.typography.titleLarge)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            DemoWave()
            DemoPixelate()
            DemoVignette()
        } else {
            Text("⚠️ Yêu cầu Android 13+ (API 33+)", color = MaterialTheme.colorScheme.error)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun DemoWave() {
    Text("1) Wave Distortion", style = MaterialTheme.typography.titleMedium)
    var time by remember { mutableFloatStateOf(0f) }

    val shader = remember {
        RuntimeShader("""
            uniform shader image;
            uniform float time;
            uniform float2 resolution;
            vec4 main(vec2 coord) {
                vec2 uv = coord / resolution;
                float wave = sin(uv.y * 10.0 + time) * 0.02;
                return image.eval(coord + vec2(wave * resolution.x, 0.0));
            }
        """.trimIndent())
    }

    Image(
        painterResource(R.drawable.bg), null,
        Modifier.size(250.dp).graphicsLayer {
            shader.setFloatUniform("time", time)
            shader.setFloatUniform("resolution", size.width, size.height)
            renderEffect = RenderEffect.createRuntimeShaderEffect(shader, "image").asComposeRenderEffect()
        },
        contentScale = ContentScale.Crop
    )

    Slider(time, { time = it }, valueRange = 0f..10f)
    Text("Time: %.2f".format(time))
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun DemoPixelate() {
    Text("2) Pixelate", style = MaterialTheme.typography.titleMedium)
    var size by remember { mutableFloatStateOf(1f) }

    val shader = remember {
        RuntimeShader("""
            uniform shader image;
            uniform float pixelSize;
            uniform float2 resolution;
            vec4 main(vec2 coord) {
                vec2 uv = coord / resolution;
                vec2 pixelated = floor(uv * resolution / pixelSize) * pixelSize;
                return image.eval(pixelated);
            }
        """.trimIndent())
    }

    Image(
        painterResource(R.drawable.img_gemini), null,
        Modifier.size(250.dp).graphicsLayer {
            shader.setFloatUniform("pixelSize", size)
            shader.setFloatUniform("resolution", this.size.width, this.size.height)
            renderEffect = RenderEffect.createRuntimeShaderEffect(shader, "image").asComposeRenderEffect()
        },
        contentScale = ContentScale.Crop
    )

    Slider(size, { size = it }, valueRange = 1f..50f)
    Text("Size: %.0f".format(size))
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun DemoVignette() {
    Text("3) Vignette", style = MaterialTheme.typography.titleMedium)
    var intensity by remember { mutableFloatStateOf(0.5f) }

    val shader = remember {
        RuntimeShader("""
            uniform shader image;
            uniform float intensity;
            uniform float2 resolution;
            vec4 main(vec2 coord) {
                vec2 uv = coord / resolution;
                float dist = distance(uv, vec2(0.5, 0.5));
                float vignette = 1.0 - smoothstep(0.3, 0.8, dist * intensity);
                return image.eval(coord) * vignette;
            }
        """.trimIndent())
    }

    Image(
        painterResource(R.drawable.bg), null,
        Modifier.size(250.dp).graphicsLayer {
            shader.setFloatUniform("intensity", intensity)
            shader.setFloatUniform("resolution", size.width, size.height)
            renderEffect = RenderEffect.createRuntimeShaderEffect(shader, "image").asComposeRenderEffect()
        },
        contentScale = ContentScale.Crop
    )

    Slider(intensity, { intensity = it }, valueRange = 0f..2f)
    Text("Intensity: %.2f".format(intensity))
}
