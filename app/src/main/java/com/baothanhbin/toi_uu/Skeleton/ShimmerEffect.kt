package com.baothanhbin.toi_uu.Skeleton

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Hiệu ứng Shimmer - sóng ánh sáng di chuyển từ trái sang phải
 * 
 * ✅ TỐI ƯU: Implementation trong Drawing Phase
 * - Sử dụng Modifier.drawWithContent để vẽ shimmer effect
 * - Animation chỉ trigger redraw, KHÔNG trigger recomposition
 * - Giảm overhead của Composition Phase
 */
@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    baseColor: Color = Color(0xFFE0E0E0),
    highlightColor: Color = Color(0xFFFFFFFF),
    durationMillis: Int = 1200
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    
    // Animation cho vị trí của shimmer (từ 0 đến 1)
    // Animation value được đọc trong Drawing Phase, không trigger recomposition
    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_progress"
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseColor) // Base color
            .drawBehind {
                // ✅ DRAWING PHASE: Code này chỉ chạy trong Drawing Phase
                // Không trigger recomposition, chỉ redraw khi animation value thay đổi
                // drawBehind vẽ sau background, trước content
                
                // Tính toán vị trí shimmer dựa trên kích thước thực tế
                val shimmerWidth = size.width * 0.6f
                val shimmerStart = (size.width + shimmerWidth) * shimmerProgress - shimmerWidth
                val shimmerEnd = shimmerStart + shimmerWidth
                
                // Vẽ shimmer gradient overlay
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            highlightColor.copy(alpha = 0.3f),
                            highlightColor.copy(alpha = 0.5f),
                            highlightColor.copy(alpha = 0.3f),
                            Color.Transparent,
                            Color.Transparent
                        ),
                        start = Offset(shimmerStart, 0f),
                        end = Offset(shimmerEnd, 0f)
                    )
                )
            }
    )
}

/**
 * Skeleton Box với shimmer effect
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    baseColor: Color = Color(0xFFE0E0E0),
    highlightColor: Color = Color(0xFFF8F8F8)
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = baseColor
    ) {
        ShimmerEffect(
            modifier = Modifier.fillMaxSize(),
            baseColor = baseColor,
            highlightColor = highlightColor
        )
    }
}

