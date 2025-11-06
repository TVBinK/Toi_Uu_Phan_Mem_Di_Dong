package com.baothanhbin.toi_uu.Skeleton

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * Skeleton Card - Giả lập card bài viết đang tải
 */
@Composable
fun SkeletonCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Hình ảnh skeleton (16:9 ratio)
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Tiêu đề skeleton (2 dòng)
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp),
                shape = RoundedCornerShape(4.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(20.dp),
                shape = RoundedCornerShape(4.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Mô tả skeleton (3 dòng)
            repeat(3) {
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp),
                    shape = RoundedCornerShape(4.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Footer với avatar và tên tác giả
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar skeleton
                ShimmerBox(
                    modifier = Modifier
                        .size(40.dp),
                    shape = RoundedCornerShape(20.dp)
                )
                
                // Tên tác giả skeleton
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .height(14.dp),
                        shape = RoundedCornerShape(4.dp)
                    )
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth(0.3f)
                            .height(12.dp),
                        shape = RoundedCornerShape(4.dp)
                    )
                }
            }
        }
    }
}

