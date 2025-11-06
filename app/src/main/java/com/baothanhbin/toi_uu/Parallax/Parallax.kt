package com.baothanhbin.toi_uu.Parallax

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.baothanhbin.toi_uu.R
import kotlin.math.roundToInt

@Composable
fun ParallaxScreen() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
    ) {
        // Cột trái: bản chưa tối ưu
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Color(0xFFE3F2FD)), // xanh nhạt để phân biệt
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Unoptimized",
                modifier = Modifier.padding(8.dp),
                color = Color.Black
            )
            Parallax_Unoptimized()
        }

        // Cột phải: bản tối ưu
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Color(0xFFFFF9C4)), // vàng nhạt để phân biệt
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Optimized (Defer Reads)",
                modifier = Modifier.padding(8.dp),
                color = Color.Black
            )
            Parallax_Optimized()
        }
    }
}
@Composable
fun Parallax_Unoptimized() {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(Color(0xFFF5F5F5))
    ) {
        //  Đọc trực tiếp scrollState.value trong Composition
        Image(
            painter = painterResource(R.drawable.img_gemini),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .offset(y = (scrollState.value / 2).dp) // <-- đọc ở Composition phase
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            repeat(50) {
                Text("Item # $it", Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
fun Parallax_Optimized() {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(Color(0xFFF5F5F5))
    ) {
        //  Đọc scrollState.value TRONG lambda của Layout Modifier (Defer State Reads)
        Image(
            painter = painterResource(id = R.drawable.img_gemini),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .offset {
                    // Hàm này chỉ chạy trong Layout phase
                    val offsetPx = (scrollState.value / 2f).roundToInt()
                    IntOffset(x = 0, y = offsetPx)
                }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            repeat(50) {
                Text("Item # $it", Modifier.padding(8.dp))
            }
        }
    }
}