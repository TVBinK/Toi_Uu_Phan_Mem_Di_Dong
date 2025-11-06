package com.baothanhbin.toi_uu.ScrollOptimize

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


@Composable
fun ScrollScreen() {
    Surface(modifier = Modifier.fillMaxSize()) {
        // ScrollToTop_Unoptimized()
        ScrollToTop_Optimized()
    }
}
//  Phiên bản chưa tối ưu – Recompose liên tục khi cuộn
@Composable
fun ScrollToTop_Unoptimized() {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(state = listState) {
            items(200) { index ->
                Text(
                    text = "Item #$index",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }

        val showButton = listState.firstVisibleItemIndex > 0

        AnimatedVisibility(
            visible = showButton,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        listState.animateScrollToItem(0)
                    }
                }
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Scroll to top")
            }
        }
    }
}

//  Phiên bản tối ưu – chỉ recompose khi giá trị thật sự đổi
@Composable
fun ScrollToTop_Optimized() {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(state = listState) {
            items(200) { index ->
                Text(
                    text = "Item #$index",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }

        // ✅ derivedStateOf: chỉ thay đổi khi logic đổi thật sự
        val showButton by remember {
            derivedStateOf {
                listState.firstVisibleItemIndex > 0
            }
        }

        AnimatedVisibility(
            visible = showButton,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        listState.animateScrollToItem(0)
                    }
                }
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Scroll to top")
            }
        }
    }
}
