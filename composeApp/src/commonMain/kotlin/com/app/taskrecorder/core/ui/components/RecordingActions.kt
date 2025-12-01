package com.app.taskrecorder.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RecordingActions(
    onRecordAgain: () -> Unit,
    onSubmit: () -> Unit,
    submitEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = onRecordAgain,
            modifier = Modifier.weight(1f).height(48.dp)
        ) {
            Text(text = "Record again", color = Color(0xFF2196F3))
        }

        Spacer(Modifier.width(8.dp))

        Button(
            onClick = onSubmit,
            modifier = Modifier.weight(1f).height(48.dp),
            enabled = submitEnabled,
            colors = ButtonDefaults.buttonColors(Color(0xFF2196F3))
        ) {
            Text(text = "Submit")
        }
    }
}
