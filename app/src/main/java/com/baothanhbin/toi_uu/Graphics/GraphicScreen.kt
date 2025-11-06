package com.baothanhbin.toi_uu.Graphics

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.baothanhbin.toi_uu.model.GraphicScreen

@Composable
fun GraphicScreen() {
    var graphicScreen by remember { mutableStateOf<GraphicScreen>(GraphicScreen.Home) }

    // Xử lý back button điện thoại
    BackHandler(enabled = graphicScreen != GraphicScreen.Home) {
        graphicScreen = GraphicScreen.Home
    }

    when (graphicScreen) {
        GraphicScreen.Home -> HomeScreen { graphicScreen = it }
        GraphicScreen.Part1 -> Part1_CPUToolkitDemo()
        GraphicScreen.Part2 -> Part2_RenderEffectDemo()
        GraphicScreen.Part3 -> Part3_AGSLShaderDemo()
    }
}

@Composable
fun HomeScreen(onNavigate: (GraphicScreen) -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Thay thế RenderScript",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Text("4 Giải pháp Hiện đại", style = MaterialTheme.typography.titleMedium)

        DeviceInfo()

        NavCard(
            "PHẦN 1: CPU Toolkit",
            listOf("✅ Blur", "✅ Color Matrix", "✅ Android 5.0+"),
            21
        ) { onNavigate(GraphicScreen.Part1) }

        NavCard(
            "PHẦN 2: RenderEffect",
            listOf("⚡ GPU Blur/ColorMatrix", "⚡ Glassmorphism", "⚡ Android 12+"),
            31
        ) { onNavigate(GraphicScreen.Part2) }

        NavCard(
            "PHẦN 3: AGSL Shader",
            listOf("🎨 Wave/Pixelate/Vignette", "🎨 Custom shaders", "🎨 Android 13+"),
            33
        ) { onNavigate(GraphicScreen.Part3) }
    }
}

@Composable
private fun DeviceInfo() {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("📱 Thiết bị", fontWeight = FontWeight.Bold)
            Text("Android API: ${Build.VERSION.SDK_INT}")
            Text("CPU Toolkit: Có")
            Text("RenderEffect: ${if (Build.VERSION.SDK_INT >= 31) "Có" else "Không"}")
            Text("AGSL: ${if (Build.VERSION.SDK_INT >= 33) "Có" else "Không"}")
        }
    }
}

@Composable
private fun NavCard(title: String, features: List<String>, minApi: Int, onClick: () -> Unit) {
    val supported = Build.VERSION.SDK_INT >= minApi

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            features.forEach { Text(it) }

            if (!supported) {
                Text("⚠️ Yêu cầu API $minApi+", color = MaterialTheme.colorScheme.error)
            }

            Button(onClick, Modifier.fillMaxWidth(), enabled = supported) {
                Text(if (supported) "Xem Demo" else "Không hỗ trợ")
            }
        }
    }
}


