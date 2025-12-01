package com.app.taskrecorder.features.start.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StartScreen(
    onStartClick: () -> Unit
) {
    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = buildAnnotatedString {
                    append("Lets Start with a\n")
                    withStyle(style = SpanStyle(color = Color(0xFF2196F3))) {
                        append("Sample Task")
                    }
                    append("\nfor practice!")
                },
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF37474F),
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Pehele hum ek\nsample task karte h",
                fontSize = 18.sp,
                color = Color(0xFF78909C),
                textAlign = TextAlign.Center,
                lineHeight = 26.sp
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Start Sample Task",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}
