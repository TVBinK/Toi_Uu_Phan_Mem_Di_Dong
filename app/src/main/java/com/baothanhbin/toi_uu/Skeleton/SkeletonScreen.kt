package com.baothanhbin.toi_uu.Skeleton

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Skeleton Screen - Demo danh sách bài viết với Skeleton + Shimmer
 * 
 * 📊 Để xem Recomposition Counts trong Layout Inspector:
 * 1. Build app ở DEBUG mode
 * 2. Chạy app trên device/emulator
 * 3. Mở Android Studio → Tools → Layout Inspector
 * 4. Chọn process của app
 * 5. Trong Layout Inspector, bật "Show Recomposition Counts" (icon ở toolbar)
 * 6. Xem số lần recompose của mỗi composable trong tree
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkeletonScreen() {
    var isLoading by remember { mutableStateOf(true) }
    var articles by remember { mutableStateOf<List<Article>>(emptyList()) }
    
    // Simulate loading data
    LaunchedEffect(Unit) {
        delay(3000) // Giả lập tải dữ liệu trong 3 giây
        articles = generateSampleArticles()
        isLoading = false
    }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header - sẽ recompose khi isLoading thay đổi
            TopAppBar(
                title = { 
                    AppBarTitle(isLoading = isLoading)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
            
            // Content - tách riêng để dễ track recomposition
            ContentArea(
                isLoading = isLoading,
                articles = articles
            )
        }
    }
}

/**
 * AppBar Title - tách riêng để track recomposition counts
 */
@Composable
private fun AppBarTitle(isLoading: Boolean) {
    Text(
        text = if (isLoading) "Đang tải..." else "Bài viết"
    )
}

/**
 * Content Area - tách riêng để track recomposition counts
 */
@Composable
private fun ContentArea(
    isLoading: Boolean,
    articles: List<Article>
) {
    if (isLoading) {
        // Hiển thị Skeleton với Shimmer
        // ✅ ShimmerEffect sử dụng Drawing Phase nên KHÔNG recompose
        SkeletonList()
    } else {
        // Hiển thị nội dung thật
        ArticleList(articles = articles)
    }
}

/**
 * Skeleton List - tách riêng để track recomposition counts
 */
@Composable
private fun SkeletonList() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(5, key = { it }) { // ✅ Thêm key để track tốt hơn
            SkeletonCard()
        }
    }
}

/**
 * Article List - tách riêng để track recomposition counts
 */
@Composable
private fun ArticleList(articles: List<Article>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(
            items = articles,
            key = { it.id } // ✅ Key để track và tối ưu recomposition
        ) { article ->
            ArticleCard(article = article)
        }
    }
}

/**
 * Tạo danh sách bài viết mẫu
 */
fun generateSampleArticles(): List<Article> {
    return listOf(
        Article(
            id = 1,
            title = "Jetpack Compose: Tối ưu hóa Performance với Shimmer Effect",
            description = "Khám phá cách tạo hiệu ứng shimmer mượt mà và đẹp mắt trong Compose để cải thiện trải nghiệm người dùng khi tải dữ liệu.",
            authorName = "Nguyễn Văn A",
            publishDate = "2 giờ trước"
        ),
        Article(
            id = 2,
            title = "Android Development: Best Practices 2024",
            description = "Những thực hành tốt nhất trong phát triển Android hiện đại, từ kiến trúc MVVM đến testing và performance optimization.",
            authorName = "Trần Thị B",
            publishDate = "5 giờ trước"
        ),
        Article(
            id = 3,
            title = "Kotlin Coroutines: Deep Dive vào Flow và StateFlow",
            description = "Tìm hiểu sâu về Kotlin Coroutines, Flow và StateFlow để xử lý asynchronous operations một cách hiệu quả.",
            authorName = "Lê Văn C",
            publishDate = "1 ngày trước"
        ),
        Article(
            id = 4,
            title = "Material Design 3: Thiết kế UI hiện đại cho Android",
            description = "Áp dụng Material Design 3 vào ứng dụng Android của bạn để tạo ra giao diện đẹp mắt và nhất quán.",
            authorName = "Phạm Thị D",
            publishDate = "2 ngày trước"
        ),
        Article(
            id = 5,
            title = "Clean Architecture trong Android: Từ lý thuyết đến thực hành",
            description = "Xây dựng ứng dụng Android với Clean Architecture để code dễ maintain, test và scale.",
            authorName = "Hoàng Văn E",
            publishDate = "3 ngày trước"
        )
    )
}

