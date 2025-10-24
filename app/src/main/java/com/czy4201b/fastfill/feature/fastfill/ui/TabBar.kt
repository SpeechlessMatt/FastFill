package com.czy4201b.fastfill.feature.fastfill.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TabBar(
    modifier: Modifier = Modifier,
    tabList: List<String>,
    currentTab: Int,
    onTabClicked: (Int) -> Unit = { },
) {
    val width: Dp = 70.dp
    val slideBoxWidth: Dp = 18.dp

    val offsetSpacer by animateDpAsState(
        targetValue = width * currentTab,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        )
    )

    Surface(
        modifier = modifier,
        color = Color.Transparent,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                tabList.forEachIndexed { idx, text ->
                    Column(
                        modifier = Modifier
                            .width(width)
                            .clickable(
                                onClick = { onTabClicked(idx) },
                                indication = null,
                                interactionSource = null
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = text,
                            color = if (idx == currentTab)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (idx == currentTab)
                                FontWeight.SemiBold
                            else
                                FontWeight.Normal
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .offset((width - slideBoxWidth) / 2 + offsetSpacer)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .width(16.dp)
                    .height(2.dp)
            )
        }
    }
}