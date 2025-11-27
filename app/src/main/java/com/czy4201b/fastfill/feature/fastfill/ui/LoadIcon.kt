package com.czy4201b.fastfill.feature.fastfill.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.czy4201b.fastfill.R
import kotlinx.coroutines.Dispatchers

@Composable
fun LoadIcon(
    name: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data("file:///android_asset/$name") // Assets 路径
            .dispatcher(Dispatchers.IO) // 后台线程
            .crossfade(true) // 淡入淡出
            .build(),
        contentDescription = contentDescription,
        placeholder = painterResource(R.drawable.error_load_image), // 静态占位
        error = painterResource(R.drawable.error_load_image), // 错误图
        modifier = modifier
    )
}