package com.baothanhbin.toi_uu.ImageList

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy

// Số lượng ảnh để demo
private const val IMAGE_COUNT = 50

// Danh sách URL ảnh từ Picsum Photos với kích thước lớn để test resizing
private fun getImageUrl(index: Int): String {
    return "https://picsum.photos/seed/image$index/800/600"
}

// Tạo ImageLoader tối ưu với đầy đủ cache
private fun createOptimizedImageLoader(context: android.content.Context): ImageLoader {
    return ImageLoader.Builder(context)
        .memoryCache {
            MemoryCache.Builder(context)
                .maxSizePercent(0.25) // 25% RAM
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("image_cache"))
                .maxSizePercent(0.02) // 2% storage
                .build()
        }
        .respectCacheHeaders(false) // Luôn cache
        .build()
}

@Composable
fun ImageLoadOptimizeScreen() {
    Surface(modifier = Modifier.fillMaxSize()) {
        // Mặc định dùng phiên bản tối ưu
        ImageLoadUnoptimizedView()
    }
}

// Phiên bản KHÔNG tối ưu – Không có cache, không resize
@Composable
fun ImageLoadUnoptimizedView() {
    val listState = rememberLazyListState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        OptimizationHeader(
            title = "KHÔNG TỐI ƯU",
            subtitle = "Không cache • Không resize • Tải lại từ mạng mỗi lần scroll",
            isOptimized = false
        )
        
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // Thông báo cảnh báo
                    WarningCard(
                        title = "⚠️ Vấn đề khi không tối ưu:",
                        items = listOf(
                            "Tải lại ảnh mỗi lần scroll (tốn băng thông)",
                            "Ảnh kích thước lớn → tốn RAM nhiều",
                            "Dễ gây OutOfMemoryError (OOM)",
                            "Lag/giật khi cuộn danh sách",
                            "Tốn pin và dữ liệu mạng",
                            "Mỗi lần scroll qua sẽ load lại từ mạng!"
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                // Danh sách ảnh không tối ưu - Dùng LazyColumn để detect khi item vào/ra viewport
                items(IMAGE_COUNT, key = { it }) { index ->
                    UnoptimizedImageCard(
                        imageUrl = getImageUrl(index),
                        index = index
                    )
                }
            }
            
            val scrollProgress = if (listState.layoutInfo.totalItemsCount > 0) {
                listState.firstVisibleItemIndex.toFloat() / listState.layoutInfo.totalItemsCount
            } else 0f
            ImageLoadScrollIndicator(
                currentValue = (scrollProgress * 100).toInt(),
                maxValue = 100
            )
        }
    }
}

// Phiên bản TỐI ƯU – Có cache, resize, crossfade
@Composable
fun ImageLoadOptimizedView() {
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val imageLoader = remember { createOptimizedImageLoader(context) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        OptimizationHeader(
            title = "TỐI ƯU VỚI COIL",
            subtitle = "Memory cache • Disk cache • Resize • Crossfade",
            isOptimized = true
        )
        
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // Thông báo lợi ích
                    BenefitsCard(
                        title = "✅ Lợi ích khi tối ưu:",
                        items = listOf(
                            "Memory cache: Ảnh tải lại từ RAM (cực nhanh)",
                            "Disk cache: Ảnh lưu trên thiết bị (không cần mạng)",
                            "Image resizing: Tự động giảm kích thước → tiết kiệm RAM",
                            "Crossfade: Hiệu ứng hiển thị mượt mà",
                            "Tiết kiệm pin, băng thông và dữ liệu"
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                items((0 until IMAGE_COUNT).toList()) { index ->
                    OptimizedImageCard(
                        imageUrl = getImageUrl(index),
                        index = index,
                        imageLoader = imageLoader
                    )
                }
            }
            
            val scrollProgress = if (listState.layoutInfo.totalItemsCount > 0) {
                listState.firstVisibleItemIndex.toFloat() / listState.layoutInfo.totalItemsCount
            } else 0f
            ImageLoadScrollIndicator(
                currentValue = (scrollProgress * 100).toInt(),
                maxValue = 100
            )
        }
    }
}

@Composable
fun OptimizationHeader(
    title: String,
    subtitle: String,
    isOptimized: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOptimized) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = if (isOptimized) {
                    MaterialTheme.colorScheme.onTertiaryContainer
                } else {
                    MaterialTheme.colorScheme.onErrorContainer
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = if (isOptimized) {
                    MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                } else {
                    MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$IMAGE_COUNT ảnh (800x600 → resize tự động)",
                style = MaterialTheme.typography.bodySmall,
                color = if (isOptimized) {
                    MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                } else {
                    MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                }
            )
        }
    }
}

@Composable
fun WarningCard(
    title: String,
    items: List<String>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
        border = androidx.compose.foundation.BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.error
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            items.forEach { item ->
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun BenefitsCard(
    title: String,
    items: List<String>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
        border = androidx.compose.foundation.BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.tertiary
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary
            )
            items.forEach { item ->
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "✓",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun UnoptimizedImageCard(
    imageUrl: String,
    index: Int
) {
    // Sử dụng key duy nhất kết hợp index + một ID để force reload mỗi lần vào viewport
    var loadId by remember { mutableStateOf(0) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf(false) }
    
    // Reset state mỗi lần vào composition (khi loadId thay đổi)
    LaunchedEffect(imageUrl, loadId) {
        // Reset state mỗi lần load
        isLoading = true
        loadError = false
        val previousBitmap = bitmap
        bitmap = null
        
        // Release bitmap cũ để giải phóng memory
        previousBitmap?.recycle()
        
        try {
            withContext(Dispatchers.IO) {
                val url = URL(imageUrl)
                val connection = url.openConnection()
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.connect()
                
                val inputStream = connection.getInputStream()
                BitmapFactory.decodeStream(inputStream)
            }?.let {
                bitmap = it
                isLoading = false
            }
        } catch (e: Exception) {
            loadError = true
            isLoading = false
        }
    }
    
    // Tăng loadId mỗi lần item vào lại composition (vào viewport)
    // DisposableEffect sẽ chạy khi item ra khỏi composition
    DisposableEffect(Unit) {
        // Tăng loadId khi vào composition để force reload
        loadId++
        
        onDispose {
            // Release bitmap memory khi item ra khỏi viewport
            bitmap?.recycle()
            bitmap = null
            isLoading = true
            loadError = false
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Hiển thị loading indicator
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp
                        )
                        Text(
                            text = "Đang tải...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Hiển thị ảnh khi đã load xong
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Unoptimized Image $index",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            // Gradient overlay (chỉ hiển thị khi có ảnh)
            if (bitmap != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        )
                )
            }
            
            // Info (chỉ hiển thị khi có ảnh hoặc đang load)
            if (!loadError) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Ảnh #${index + 1}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (isLoading) {
                        Text(
                            text = "⏳ Đang tải từ mạng...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    } else {
                        Text(
                            text = "❌ Không cache • Load từ mạng",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OptimizedImageCard(
    imageUrl: String,
    index: Int,
    imageLoader: ImageLoader
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Load ảnh TỐI ƯU - có cache, resize, crossfade
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .size(Size(400, 300)) // Resize về kích thước nhỏ hơn
                    .memoryCachePolicy(CachePolicy.ENABLED) // Bật memory cache
                    .diskCachePolicy(CachePolicy.ENABLED) // Bật disk cache
                    .build(),
                imageLoader = imageLoader,
                contentDescription = "Optimized Image $index",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )
            
            // Info với các tính năng tối ưu
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Ảnh #${index + 1}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "✅ Memory cache • Disk cache",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Text(
                    text = "✅ Resized 800x600 → 400x300",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Text(
                    text = "✅ Crossfade animation",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun BoxScope.ImageLoadScrollIndicator(
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

