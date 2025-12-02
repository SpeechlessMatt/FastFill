package com.czy4201b.fastfill.feature.fastfill.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.czy4201b.fastfill.R
import com.czy4201b.fastfill.core.theme.DarkHazeStyle
import com.czy4201b.fastfill.core.theme.LightHazeStyle
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@Composable
fun UserFillTableExpanded(
    modifier: Modifier = Modifier,
    vm: UserFillTableViewModel
) {
    val uiState by vm.state.collectAsState()
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val hazeState = rememberHazeState()
    val blurRadius by animateDpAsState(
        targetValue = if (uiState.isShowEditView) 10.dp else 0.dp,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        ),
        label = "blurRadius"
    )
    val coroutineScope = rememberCoroutineScope()

    BackHandler { vm.zoomTable() }

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        // 匹配表主体部分
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = blurRadius)
                .clickable(
                    onClick = {
                        focusManager.clearFocus()
                    },
                    indication = null,
                    interactionSource = null
                ),
            state = listState,
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
//            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 表头
            stickyHeader {
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                        .hazeEffect(
                            state = hazeState,
                            style = if (isSystemInDarkTheme()) DarkHazeStyle else LightHazeStyle
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = { },
                                onDoubleTap = {
                                    focusManager.clearFocus()
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(0)
                                    }
                                }
                            )
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "匹配表",
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Light,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .clickable(
                                    onClick = {
                                        focusManager.clearFocus()
                                        vm.addRow()
                                        coroutineScope.launch {
                                            listState.animateScrollToItem(vm.userFillMapSize - 1)
                                        }
                                    },
                                ),
                            painter = painterResource(R.drawable.list_add),
                            contentDescription = null,
                            tint = Color.Gray
                        )
                        Icon(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .clickable(
                                    onClick = {
                                        focusManager.clearFocus()
                                        vm.zoomTable()
                                    },
                                ),
                            painter = painterResource(R.drawable.expand),
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                }
            }

            // 这里是表格实现
            items(uiState.userFillTable.sortedBy { it.index }, key = { it.id }) { row ->
                SwipeBox(
                    modifier = Modifier.animateItem(
                        placementSpec = tween(
                            durationMillis = 600,
                            easing = FastOutSlowInEasing
                        ),
                        fadeInSpec = tween(
                            durationMillis = 400,
                            easing = FastOutSlowInEasing
                        ),
                    ),
                    onDelete = { vm.removeRow(row.id) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .hazeSource(state = hazeState),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "问题",
                            modifier = Modifier.align(Alignment.Start),
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Light,
                            fontSize = 14.sp
                        )
                        TableTextField(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onValueChange = { newKey ->
                                vm.updateTableRow(id = row.id, key = newKey)
                            },
                            value = row.key,
                            textStyle = TextStyle(fontSize = 12.sp),
                            placeholder = {
                                Text(
                                    "请输入问题",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                        alpha = 0.6f
                                    ),
                                    fontSize = 12.sp
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "答案",
                            modifier = Modifier.align(Alignment.Start),
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Light,
                            fontSize = 14.sp
                        )
                        TableTextField(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onValueChange = { newValue ->
                                vm.updateTableRow(id = row.id, value = newValue)
                            },
                            value = row.value,
                            textStyle = TextStyle(fontSize = 12.sp),
                            placeholder = {
                                Text(
                                    "请输入答案",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                        alpha = 0.6f
                                    ),
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}