package com.app.taskrecorder.core.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ValidationCheckboxes(
    noBackgroundNoise: Boolean,
    noMistakes: Boolean,
    noErrorsInMiddle: Boolean,
    onNoBackgroundNoiseChange: (Boolean) -> Unit,
    onNoMistakesChange: (Boolean) -> Unit,
    onNoErrorsInMiddleChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val checkboxItems = listOf(
        Triple("No background noise", noBackgroundNoise, onNoBackgroundNoiseChange),
        Triple("No mistakes while reading", noMistakes, onNoMistakesChange),
        Triple("Beech me koi galti nahi hai", noErrorsInMiddle, onNoErrorsInMiddleChange)
    )

    checkboxItems.forEach { (text, checked, onCheck) ->
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = { onCheck(it) }
            )
            Text(text = text, fontSize = 14.sp)
        }
    }
}
