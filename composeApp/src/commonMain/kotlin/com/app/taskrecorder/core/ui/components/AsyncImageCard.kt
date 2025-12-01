package com.app.taskrecorder.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage

@Composable
fun AsyncImageCard(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        if (imageUrl != null) {
            CoilImage(
                imageModel = { imageUrl },
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    contentDescription = contentDescription
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                loading = {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
                failure = {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp).padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Failed to load image",
                            fontSize = 14.sp,
                            color = Color(0xFF78909C)
                        )
                    }
                }
            )
        } else {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp).padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No image available",
                    fontSize = 14.sp,
                    color = Color(0xFF78909C)
                )
            }
        }
    }
}
