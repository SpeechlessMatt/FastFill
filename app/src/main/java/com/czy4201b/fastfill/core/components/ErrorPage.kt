package com.czy4201b.fastfill.core.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.czy4201b.fastfill.R

@Composable
fun ErrorPage(message: String) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier.Companion.padding(
                    16.dp,
                    top = 38.dp,
                    bottom = 10.dp
                )
            ) {
                Text(
                    text = "FastFill",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Companion.Serif
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Row {
                    Icon(
                        painter = painterResource(R.drawable.report_error),
                        contentDescription = null
                    )
                    Text(
                        "我们遇到了无法解决的错误！",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
                Text(message)
            }
        }
    }
}