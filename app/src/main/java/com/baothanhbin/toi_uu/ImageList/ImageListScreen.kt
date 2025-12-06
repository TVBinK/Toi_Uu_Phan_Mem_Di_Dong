package com.baothanhbin.toi_uu.ImageList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import coil.compose.AsyncImage
import coil.request.ImageRequest

// Số lượng ảnh để demo
private const val IMAGE_COUNT = 1000

// Danh sách URL ảnh từ Picsum Photos (placeholder service)
private fun getImageUrl(index: Int, width: Int = 400, height: Int = 300): String {
    return "https://picsum.photos/seed/$index/$width/$height"
}

@Composable
fun ImageListScreen() {
    Surface(modifier = Modifier.fillMaxSize()) {
        // Mặc định dùng LazyColumn (tối ưu)
        ImageListColumnView()
    }
}

// Phiên bản chưa tối ưu – Render tất cả items cùng lúc
@Composable
fun ImageListColumnView() {
    val scrollState = rememberScrollState()
    var isReady by remember { mutableStateOf(false) }
    var startTime by remember { mutableLongStateOf(0L) }
    var loadTime by remember { mutableLongStateOf(0L) }
    
    LaunchedEffect(Unit) {
        startTime = System.currentTimeMillis()
        // Giả lập delay khi render tất cả items
        kotlinx.coroutines.delay(100) // Delay nhỏ để hiển thị loading
        loadTime = System.currentTimeMillis() - startTime
        isReady = true
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header với thông tin
        HeaderSection()
        
        Box(modifier = Modifier.fillMaxSize()) {
            if (!isReady) {
                // Loading indicator
                LoadingOverlay(
                    message = "Đang render tất cả $IMAGE_COUNT items...\nCó thể mất vài giây!",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Thông báo hiệu năng
                    PerformanceBadge(
                        method = "Column + Scroll",
                        renderCount = IMAGE_COUNT,
                        isOptimized = false,
                        loadTimeMs = loadTime
                    )
                    
                    // Render tất cả items
                    repeat(IMAGE_COUNT) { index ->
                        ImageCard(
                            imageUrl = getImageUrl(index),
                            index = index,
                            totalCount = IMAGE_COUNT
                        )
                    }
                }
                
                // Scroll indicator
                ScrollIndicator(scrollState.value, scrollState.maxValue)
            }
        }
    }
}

// Phiên bản tối ưu – Chỉ render items đang hiển thị
@Composable
fun ImageListLazyColumnView() {
    val listState = rememberLazyListState()
    var visibleItemCount by remember { mutableIntStateOf(0) }
    var startTime by remember { mutableLongStateOf(0L) }
    var loadTime by remember { mutableLongStateOf(0L) }
    var isFirstLoad by remember { mutableStateOf(true) }
    
    // Đo thời gian load ban đầu
    LaunchedEffect(Unit) {
        if (isFirstLoad) {
            startTime = System.currentTimeMillis()
            isFirstLoad = false
        }
    }
    
    // Theo dõi số items đang visible
    LaunchedEffect(listState.firstVisibleItemIndex, listState.layoutInfo.visibleItemsInfo.size) {
        val firstVisible = listState.firstVisibleItemIndex
        val lastVisible = firstVisible + listState.layoutInfo.visibleItemsInfo.size
        visibleItemCount = maxOf(visibleItemCount, lastVisible)
        
        // Tính thời gian load khi có items đầu tiên hiển thị
        if (visibleItemCount > 0 && loadTime == 0L) {
            loadTime = System.currentTimeMillis() - startTime
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header với thông tin
        HeaderSection()
        
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Thông báo hiệu năng
                PerformanceBadge(
                    method = "LazyColumn",
                    renderCount = visibleItemCount,
                    isOptimized = true,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    loadTimeMs = loadTime
                )
                
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(IMAGE_COUNT) { index ->
                        ImageCard(
                            imageUrl = getImageUrl(index),
                            index = index,
                            totalCount = IMAGE_COUNT
                        )
                    }
                }
            }
            
            // Scroll indicator
            val scrollProgress = if (listState.layoutInfo.totalItemsCount > 0) {
                listState.firstVisibleItemIndex.toFloat() / listState.layoutInfo.totalItemsCount
            } else 0f
            ScrollIndicator(
                currentValue = (scrollProgress * 100).toInt(),
                maxValue = 100
            )
        }
    }
}

@Composable
fun HeaderSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "So sánh hiệu năng",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$IMAGE_COUNT ảnh",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Column + Scroll: Render tất cả items cùng lúc\nLazyColumn: Chỉ render items đang hiển thị",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}


@Composable
fun ImageCard(
    imageUrl: String,
    index: Int,
    totalCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Ảnh với gradient overlay
            AsyncImage(
                model = ImageRequest.Builder(context = androidx.compose.ui.platform.LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Image $index",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Gradient overlay để text dễ đọc
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.6f)
                            )
                        )
                    )
            )
            
            // Thông tin ảnh
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Ảnh #${index + 1}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${((index + 1) * 100f / totalCount).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun PerformanceBadge(
    method: String,
    renderCount: Int,
    isOptimized: Boolean,
    modifier: Modifier = Modifier,
    loadTimeMs: Long = 0L
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = if (isOptimized) {
            MaterialTheme.colorScheme.tertiaryContainer
        } else {
            MaterialTheme.colorScheme.errorContainer
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = method,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isOptimized) {
                            MaterialTheme.colorScheme.onTertiaryContainer
                        } else {
                            MaterialTheme.colorScheme.onErrorContainer
                        }
                    )
                    Text(
                        text = "Đã render: $renderCount / $IMAGE_COUNT",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isOptimized) {
                            MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                        } else {
                            MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                        }
                    )
                    if (loadTimeMs > 0) {
                        Text(
                            text = "Thời gian load: ${loadTimeMs}ms",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isOptimized) {
                                MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                            } else {
                                MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                            }
                        )
                    }
                }
                
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isOptimized) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                ) {
                    Text(
                        text = if (isOptimized) "✓ Tối ưu" else "⚠ Không tối ưu",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isOptimized) {
                            MaterialTheme.colorScheme.onTertiary
                        } else {
                            MaterialTheme.colorScheme.onError
                        }
                    )
                }
            }
            
            // Cảnh báo cho Column + Scroll
            if (!isOptimized) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "⚠ Cảnh báo: App sẽ load rất chậm vì render tất cả $IMAGE_COUNT items cùng lúc!",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingOverlay(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Text(
                text = "App có thể bị đơ hoặc lag nặng!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@Composable
fun BoxScope.ScrollIndicator(
    currentValue: Int,
    maxValue: Int
) {
    if (maxValue > 0) {
        val progress = (currentValue.toFloat() / maxValue).coerceIn(0f, 1f)
        
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            shadowElevation = 4.dp
        ) {
            Text(
                text = "${(progress * 100).toInt()}%",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

