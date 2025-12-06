package com.baothanhbin.toi_uu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.baothanhbin.toi_uu.Graphics.GraphicScreen
import com.baothanhbin.toi_uu.ScrollOptimize.ScrollScreen
import com.baothanhbin.toi_uu.ImageList.ImageListScreen
import com.baothanhbin.toi_uu.ImageList.ImageLoadOptimizeScreen
import com.baothanhbin.toi_uu.ui.theme.Toi_UuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Toi_UuTheme {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ImageLoadOptimizeScreen()
                }
            }
        }
    }
}

