package com.czy4201b.fastfill

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.czy4201b.fastfill.core.theme.FastFillTheme
import com.czy4201b.fastfill.feature.fastfill.ui.MainView

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FastFillTheme {
                Scaffold(
                    modifier = Modifier.Companion.fillMaxSize(),
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
                    MainView(
                        modifier = Modifier.Companion.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainViewPreview() {
    FastFillTheme {
        MainView()
    }
}