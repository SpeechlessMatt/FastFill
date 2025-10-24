package com.czy4201b.fastfill.feature.fastfill.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.czy4201b.fastfill.R
import com.czy4201b.fastfill.core.theme.DarkCustomBackground
import com.czy4201b.fastfill.core.theme.DarkHazeStyle
import com.czy4201b.fastfill.core.theme.LightCustomBackground
import com.czy4201b.fastfill.core.theme.LightHazeStyle
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@Composable
fun UserFillTable(
    modifier: Modifier = Modifier,
    vm: UserFillTableViewModel
) {
    val uiState by vm.state.collectAsState()
//    val allTables by vm.allTables.collectAsState()
    val otherTables by vm.otherTables.collectAsState()

    // 理论上不用放在vm里面
    val listState = rememberLazyListState()
//    val headerAlpha by animateFloatAsState(
//        targetValue = if (uiState.isShowEditView) 0f else 1f,
//        animationSpec = tween(
//            durationMillis = 400,
//            easing = FastOutSlowInEasing
//        )
//    )
    val hazeState = rememberHazeState()
    val blurRadius by animateDpAsState(
        targetValue = if (uiState.isShowEditView) 10.dp else 0.dp,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        ),
        label = "blurRadius"
    )
    val rotateDegree by animateFloatAsState(
        targetValue = if (uiState.isShowPicker) 180f else 0f,
        animationSpec = tween(
            durationMillis = 200,
            easing = FastOutSlowInEasing
        ),
        label = "rotateDegree"
    )
    val coroutineScope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

    // 懒得写成card组件了，复制粘贴吧
    Surface(
        modifier = modifier,
        color = if (isSystemInDarkTheme()) DarkCustomBackground else LightCustomBackground,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 1.dp
    ) {
        Box {
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
                verticalArrangement = Arrangement.spacedBy(8.dp),
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
                                            vm.showEditView()
                                        },
                                    ),
                                painter = painterResource(R.drawable.save),
                                contentDescription = null,
                                tint = Color.Gray
                            )
                            Icon(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable(
                                        onClick = {

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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .hazeSource(state = hazeState),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TableTextField(
                                modifier = Modifier
                                    .weight(1f),
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
                            TableTextField(
                                modifier = Modifier
                                    .weight(1f),
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
                }
            }

            /*
             * 第二层： 模糊层
             * 模糊层淡入淡出
             */
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.TopCenter),
                visible = uiState.isShowEditView,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            onClick = {
                                if (isFocused) {
                                    focusManager.clearFocus()
                                } else {
                                    vm.closeEditView()
                                }
                            },
                            indication = null,
                            interactionSource = null
                        )
                        .padding(start = 50.dp, end = 50.dp, top = 70.dp),
                ) {
                    Text(
                        "表格名称",
                        color = Color(0xFF757575),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        BasicTextField(
                            modifier = Modifier
                                .weight(1f)
                                .onFocusChanged { focusState ->
                                    isFocused = focusState.isFocused
                                },
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 1,
                            value = uiState.inputTableName,
                            onValueChange = {
                                vm.updateTableNameText(it)
                            },
                            decorationBox = { innerTextField ->
                                Box(
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Box(
                                        modifier = Modifier.alpha(if (uiState.inputTableName.isEmpty()) 1f else 0f)
                                    ) {
                                        Text(
                                            text = "请输入表格名称",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                alpha = 0.6f
                                            )
                                        )
                                    }
                                    innerTextField()
                                    HorizontalDivider(
                                        modifier = Modifier.align(Alignment.BottomCenter),
                                        thickness = if (isFocused) 1.5.dp else 1.dp,
                                    )
                                }
                            }
                        )

                        Icon(
                            modifier = Modifier
                                .clip(CircleShape)
                                .rotate(degrees = rotateDegree)
                                .clickable(
                                    onClick = {
                                        vm.setPickerShow(!uiState.isShowPicker)
                                    }
                                ),
                            painter = painterResource(R.drawable.arrow_down),
                            contentDescription = null
                        )
                    }

                    // picker
                    AnimatedVisibility(
                        visible = uiState.isShowPicker,
                    ) {
                        Column(
                            // 为了用户快速点删除和添加的体验，让他不会突然退出，
                            // 虽然我不知道什么用户用这么玩但是也要写的
                            modifier = Modifier
                                .padding(8.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = { }
                                    )
                                }
                        ) {
                            LazyColumn(
                                Modifier
                                    .heightIn(max = 90.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(
                                    otherTables.sortedByDescending { it.createdAt },
                                    key = { it.tableId }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .animateItem(
                                                placementSpec = tween(
                                                    durationMillis = 600,
                                                    easing = FastOutSlowInEasing
                                                ),
                                                fadeInSpec = tween(
                                                    durationMillis = 400,
                                                    easing = FastOutSlowInEasing
                                                )
                                            )
                                            .fillMaxWidth()
                                            .padding(end = 8.dp),
                                    ) {
                                        Text(
                                            text = it.name,
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable(
                                                    onClick = { vm.selectTable(it.tableId) },
                                                    role = Role.Button
                                                )
                                        )
                                        Box(
                                            modifier = Modifier
                                                .padding(top = 2.dp)
                                                .clip(CircleShape)
                                                .rotate(rotateDegree)
                                                .clickable(
                                                    onClick = {
                                                        vm.deleteTable(it)
                                                    },
                                                ),
                                        ) {
                                            Icon(
                                                modifier = Modifier
                                                    .size(20.dp),
                                                imageVector = Icons.Outlined.Close,
                                                contentDescription = null,
                                                tint = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        interactionSource = null,
                                        indication = null,
                                        onClick = {
                                            // 添加新表格的逻辑
                                            vm.addTable()
                                            focusManager.clearFocus()
                                        }
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "添加新表格",
                                    tint = Color.Gray
                                )
                                Text(
                                    "添加新表格",
                                    color = Color.Gray,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }

                    }

                    Spacer(Modifier.height(12.dp))

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally),
                        onClick = {
                            vm.closeEditView()
                        },
                        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    ) {
                        Text("取消", modifier = Modifier.padding(horizontal = 8.dp))
                    }

                    Spacer(Modifier.height(4.dp))

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally),
                        onClick = {
                            vm.saveAll()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                    ) {
                        Text("保存", modifier = Modifier.padding(horizontal = 8.dp))
                    }

                }
            }
        }
    }
}