package com.app.taskrecorder.core.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin
import kotlin.math.PI

@Composable
fun AudioPlayerCard(
    isPlaying: Boolean,
    playbackPosition: Long,
    duration: Long,
    onTogglePlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(Color(0xFFE3F2FD))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onTogglePlayPause,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))

                if (isPlaying) {
                    WaveformAnimation(modifier = Modifier.weight(1f).height(40.dp))
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.width(12.dp))

                Text(
                    text = "${formatTime(playbackPosition)} / ${formatTime(duration)}",
                    fontSize = 13.sp,
                    color = Color(0xFF424242)
                )
            }

            Spacer(Modifier.height(12.dp))

            Slider(
                value = if (duration > 0) playbackPosition.toFloat() else 0f,
                onValueChange = { onSeek(it.toLong()) },
                valueRange = 0f..(if (duration > 0) duration.toFloat() else 1f),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF2196F3),
                    activeTrackColor = Color(0xFF2196F3),
                    inactiveTrackColor = Color(0xFFBBDEFB)
                )
            )
        }
    }
}

@Composable
private fun WaveformAnimation(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()

    val wave1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val wave2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val wave3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val centerY = height / 2
        val barCount = 7
        val barWidth = 3.dp.toPx()
        val spacing = width / (barCount + 1)

        val waves = listOf(wave1, wave2, wave3)

        for (i in 0 until barCount) {
            val x = spacing * (i + 1)
            val waveIndex = i % waves.size
            val phase = (waves[waveIndex] + i * 40) % 360
            val radians = phase * PI.toFloat() / 180f

            val sineValue = sin(radians)
            val normalizedValue = (sineValue + 1f) / 2f

            val maxAmplitude = height * (0.3f + (i % 3) * 0.1f)
            val minAmplitude = height * 0.15f
            val amplitude = minAmplitude + (maxAmplitude - minAmplitude) * normalizedValue

            drawLine(
                color = Color(0xFF2196F3),
                start = Offset(x, centerY - amplitude / 2),
                end = Offset(x, centerY + amplitude / 2),
                strokeWidth = barWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

private fun formatTime(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / 1000) / 60
    return minutes.toString() + ":" + seconds.toString().padStart(2, '0')
}

