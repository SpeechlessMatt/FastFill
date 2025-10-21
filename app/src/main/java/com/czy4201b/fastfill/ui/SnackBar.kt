package com.czy4201b.fastfill.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SnackBar(
    message: String,
    modifier: Modifier = Modifier,
    buttonText: String? = null,
    onButtonClicked: () -> Unit = {},
) {
    Surface(
        modifier = modifier,
        color = Color(0xE6202020),
        shape = RoundedCornerShape(4.dp),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                message,
                color = Color(0xFFFFFFFF),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
            Spacer(modifier = Modifier.weight(1f))
            buttonText?.let {
                Text(
                    text = it,
                    modifier = Modifier.clickable(
                        onClick = {
                            onButtonClicked()
                        },
                    ),
                    color = Color(0xFFBB86FC),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

        }

    }

}