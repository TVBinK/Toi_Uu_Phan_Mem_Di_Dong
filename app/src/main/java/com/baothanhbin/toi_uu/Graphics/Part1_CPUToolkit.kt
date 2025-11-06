package com.baothanhbin.toi_uu.Graphics

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.baothanhbin.toi_uu.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun Part1_CPUToolkitDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("PHẦN 1: CPU Toolkit", style = MaterialTheme.typography.titleLarge)
        DemoGaussianBlur()
        DemoColorMatrix()
    }
}

@Composable
private fun DemoGaussianBlur() {
    Text("1) Gaussian Blur", style = MaterialTheme.typography.titleMedium)

    val context = LocalContext.current
    val original = remember {
        BitmapFactory.decodeResource(context.resources, R.drawable.bg)
            .copy(Bitmap.Config.ARGB_8888, false)
    }
    var blurred by remember { mutableStateOf<Bitmap?>(null) }
    var radius by remember { mutableStateOf(12) }

    LaunchedEffect(original, radius) {
        Log.d("Part1", "Blur LaunchedEffect triggered - radius: $radius")
        blurred = withContext(Dispatchers.Default) { 
            Log.d("Part1", "Starting blur processing...")
            val result = Toolkit.blur(original, radius)
            Log.d("Part1", "Blur processing completed")
            result
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Gaussian Blur (radius: $radius):", style = MaterialTheme.typography.bodyMedium)
        blurred?.asImageBitmap()?.let { 
            Image(
                it, 
                null, 
                Modifier.fillMaxWidth().height(250.dp),
                contentScale = ContentScale.Crop
            )
        } ?: Image(
            painterResource(R.drawable.bg),
            null,
            Modifier.fillMaxWidth().height(250.dp),
            contentScale = ContentScale.Crop
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { 
            Log.d("Part1", "Minus button clicked - current radius: $radius")
            if (radius > 1) radius-- 
        }) { Text("-") }
        Text("Radius: $radius")
        Button(onClick = { 
            Log.d("Part1", "Plus button clicked - current radius: $radius")
            if (radius < 25) radius++ 
        }) { Text("+") }
    }
}

@Composable
private fun DemoColorMatrix() {
    Text("2) Color Matrix", style = MaterialTheme.typography.titleMedium)

    val context = LocalContext.current
    val original = remember {
        BitmapFactory.decodeResource(context.resources, R.drawable.img_gemini)
            .copy(Bitmap.Config.ARGB_8888, false)
    }
    var processed by remember { mutableStateOf<Bitmap?>(null) }
    var mode by remember { mutableStateOf(ColorMode.SATURATION) }

    LaunchedEffect(original, mode) {
        Log.d("Part1", "ColorMatrix LaunchedEffect triggered - mode: $mode")
        val matrix = when (mode) {
            ColorMode.SATURATION -> colorMatrixSaturation(1.5f)
            ColorMode.GRAYSCALE -> COLOR_MATRIX_GRAYSCALE
            ColorMode.SEPIA -> COLOR_MATRIX_SEPIA
        }
        processed = withContext(Dispatchers.Default) {
            Log.d("Part1", "Starting color matrix processing...")
            val result = Toolkit.colorMatrix(original, matrix)
            Log.d("Part1", "Color matrix processing completed")
            result
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Color Matrix ($mode):", style = MaterialTheme.typography.bodyMedium)
        processed?.asImageBitmap()?.let { 
            Image(
                it, 
                null, 
                Modifier.fillMaxWidth().height(250.dp),
                contentScale = ContentScale.Crop
            )
        } ?: Image(
            painterResource(R.drawable.img_gemini),
            null,
            Modifier.fillMaxWidth().height(250.dp),
            contentScale = ContentScale.Crop
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { 
            Log.d("Part1", "Saturation button clicked")
            mode = ColorMode.SATURATION 
        }) { Text("Saturation") }
        Button(onClick = { 
            Log.d("Part1", "Grayscale button clicked")
            mode = ColorMode.GRAYSCALE 
        }) { Text("Grayscale") }
        Button(onClick = { 
            Log.d("Part1", "Sepia button clicked")
            mode = ColorMode.SEPIA 
        }) { Text("Sepia") }
    }
}

private enum class ColorMode { SATURATION, GRAYSCALE, SEPIA }

private val COLOR_MATRIX_GRAYSCALE = floatArrayOf(
    0.2126f, 0.7152f, 0.0722f, 0f, 0f,
    0.2126f, 0.7152f, 0.0722f, 0f, 0f,
    0.2126f, 0.7152f, 0.0722f, 0f, 0f,
    0f, 0f, 0f, 1f, 0f
)

private val COLOR_MATRIX_SEPIA = floatArrayOf(
    0.393f, 0.769f, 0.189f, 0f, 0f,
    0.349f, 0.686f, 0.168f, 0f, 0f,
    0.272f, 0.534f, 0.131f, 0f, 0f,
    0f, 0f, 0f, 1f, 0f
)

private fun colorMatrixSaturation(s: Float): FloatArray {
    val ir = (1 - s) * 0.2126f; val ig = (1 - s) * 0.7152f; val ib = (1 - s) * 0.0722f
    return floatArrayOf(
        ir + s, ig, ib, 0f, 0f,
        ir, ig + s, ib, 0f, 0f,
        ir, ig, ib + s, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )
}

