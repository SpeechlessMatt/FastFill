package com.czy4201b.fastfill.core.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun <T> BottomPicker(
    shouldShow: Boolean,
    onResultNull: () -> Unit,
    onResult: (List<T>) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "请选择你的选项:",
    titlePadding: PaddingValues = PaddingValues(0.dp),
    titleTextStyle: TextStyle = TextStyle(),
    contentPadding: PaddingValues = PaddingValues(8.dp),
    properties: BottomPickerProperties = BottomPickerProperties(),
    block: BottomPickerScope<T>.() -> Unit
) {
    if (shouldShow) {
        Dialog(
            onDismissRequest = onResultNull,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,   // 让宽度全屏
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            val scope = remember { BottomPickerScope<T>(properties) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        onClick = onResultNull,
                        indication = null,
                        interactionSource = null
                    )
            ) {
                AnimatedVisibility(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    visible = shouldShow,
                    enter = slideInVertically { fullHeight -> fullHeight }   // 从 fullHeight → 0
                            + fadeIn(initialAlpha = 0.3f),                   // 淡入
                    exit = slideOutVertically { fullHeight -> fullHeight }  // 从 0 → fullHeight
                            + fadeOut()
                ) {
                    Surface(
                        modifier = modifier.clickable(
                            onClick = { },
                            indication = null,
                            interactionSource = null
                        ),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(contentPadding),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(titlePadding),
                                text = title,
                                style = titleTextStyle
                            )

                            Box {
                                scope.block()
                                scope.Build()
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                thickness = 1.dp
                            )

                            Spacer(Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.padding(horizontal = 24.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                ModernDefaultOutlinedButton(
                                    modifier = Modifier.widthIn(min = 130.dp),
                                    onClick = onResultNull
                                ) {
                                    Text("取消")
                                }
                                Spacer(Modifier.weight(1f))
                                ModernDefaultFilledButton(
                                    modifier = Modifier.widthIn(min = 130.dp),
                                    onClick = {
                                        scope.resultValues?.let { onResult(it) } ?: onResultNull()
                                    }
                                ) {
                                    Text("确定")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

