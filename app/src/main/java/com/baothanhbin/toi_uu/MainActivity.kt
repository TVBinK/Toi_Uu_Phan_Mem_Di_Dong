package com.baothanhbin.toi_uu

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.baothanhbin.toi_uu.Graphics.GraphicScreen
import com.baothanhbin.toi_uu.Graphics.Part1_CPUToolkitDemo
import com.baothanhbin.toi_uu.Graphics.Part2_RenderEffectDemo
import com.baothanhbin.toi_uu.Graphics.Part3_AGSLShaderDemo
import com.baothanhbin.toi_uu.model.GraphicScreen
import com.baothanhbin.toi_uu.ui.theme.Toi_UuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Toi_UuTheme {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    GraphicScreen()
                }
            }
        }
    }
}

