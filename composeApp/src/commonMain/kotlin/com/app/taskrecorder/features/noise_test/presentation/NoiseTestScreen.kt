package com.app.taskrecorder.features.noise_test.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.taskrecorder.platform.RequestAudioPermission
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoiseTestScreen(
    onTestPassed: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: NoiseTestViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        onDispose { if (state.testCompleted) viewModel.resetTest() }
    }

    if (!state.hasPermission) {
        RequestAudioPermission { granted ->
            if (granted) viewModel.requestPermission()
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text(text = "Sample Task") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = { IconButton(onClick = {}) { Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More") } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Test Ambient Noise Level", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF37474F))
            Spacer(Modifier.height(8.dp))
            Text(text = "Before you can start the call we will have to check your ambient noise level.",
                fontSize = 14.sp, color = Color(0xFF78909C), textAlign = TextAlign.Center)
            Spacer(Modifier.height(48.dp))

            DecibelMeter(currentValue = state.currentDecibel, maxValue = 60f)

            Spacer(Modifier.weight(1f))

            if (state.isTesting) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp), color = Color(0xFF2196F3))
            }

            if (state.testCompleted) {
                Text(
                    text = if (state.canProceed) "Good to proceed" else "Please move to a quieter place",
                    fontSize = 16.sp, fontWeight = FontWeight.Medium,
                    color = if (state.canProceed) Color(0xFF4CAF50) else Color(0xFFF44336),
                    modifier = Modifier.padding(16.dp)
                )

                Button(
                    onClick = {
                        if (state.canProceed) {
                            onTestPassed()
                        } else {
                            viewModel.resetTest()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    Text(text = if (state.canProceed) "Continue" else "Try Again", color = Color.White)
                }
            } else if (!state.isTesting) {
                Button(
                    onClick = { viewModel.startTest() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    enabled = state.hasPermission
                ) {
                    Text(text = "Start Test", fontSize = 16.sp, color = Color.White)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun DecibelMeter(
    currentValue: Float,
    maxValue: Float
) {
    val animatedValue by animateFloatAsState(
        targetValue = currentValue,
        animationSpec = androidx.compose.animation.core.tween(500),
        label = ""
    )

    val marks = listOf(10, 20, 30, 40, 50)
    val startAngle = 135f
    val sweepAngle = 270f

    val threshold = maxValue * 0.85f
    val blueSweep = sweepAngle * (threshold / maxValue)
    val redSweep = sweepAngle - blueSweep
    val density = LocalDensity.current

    Box(modifier = Modifier.size(300.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(280.dp)) {
            val strokeWidth = 40.dp.toPx()
            val tickLength = 8.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)

            drawArc(
                color = Color(0xFF2196F3),
                startAngle = startAngle,
                sweepAngle = blueSweep,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            drawArc(
                color = Color(0xFFF44336),
                startAngle = startAngle + blueSweep,
                sweepAngle = redSweep,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            val clamped = animatedValue.coerceIn(0f, maxValue)
            val needleAngle = startAngle + (sweepAngle * (clamped / maxValue))
            val rad = needleAngle * PI / 180f
            val needleEnd = Offset(
                center.x + (radius * 0.65f * cos(rad)).toFloat(),
                center.y + (radius * 0.65f * sin(rad)).toFloat()
            )

            drawLine(
                color = Color.LightGray.copy(alpha = 0.75f),
                start = center,
                end = needleEnd,
                strokeWidth = 6.dp.toPx(),
                cap = StrokeCap.Round
            )

            val inner = radius - strokeWidth / 2
            val outer = inner + tickLength

            marks.forEach { value ->
                val ratio = value / maxValue
                val angle = startAngle + sweepAngle * ratio
                val tickRad = angle * PI / 180f

                val inside = Offset(
                    center.x + (inner * cos(tickRad)).toFloat(),
                    center.y + (inner * sin(tickRad)).toFloat()
                )
                val outside = Offset(
                    center.x + (outer * cos(tickRad)).toFloat(),
                    center.y + (outer * sin(tickRad)).toFloat()
                )

                drawLine(
                    Color.Black,
                    inside,
                    outside,
                    2.dp.toPx(),
                    StrokeCap.Round
                )
            }
        }

        marks.forEach { value ->
            val angle = startAngle + (value / maxValue) * sweepAngle
            val rad = angle * PI / 180f

            val (xDp, yDp) = with(density) {
                val rPx = 115.dp.toPx()
                val xPx = (cos(rad) * rPx).toFloat()
                val yPx = (sin(rad) * rPx).toFloat()
                xPx.toDp() to yPx.toDp()
            }

            Text(
                "$value",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF607D8B),
                modifier = Modifier.offset(x = xDp, y = yDp)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "${animatedValue.toInt()}", fontSize = 40.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF37474F))
            Text(text = "db", fontSize = 18.sp, color = Color(0xFF90A4AE))
        }
    }
}