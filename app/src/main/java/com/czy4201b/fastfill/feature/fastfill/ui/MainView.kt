package com.czy4201b.fastfill.feature.fastfill.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.czy4201b.fastfill.R
import com.czy4201b.fastfill.core.components.ErrorPage
import com.czy4201b.fastfill.core.components.ModernFilledButton
import com.czy4201b.fastfill.core.components.ModernOutlinedButton
import com.czy4201b.fastfill.core.components.SnackBar
import com.czy4201b.fastfill.feature.fastfill.web.HiddenFilledTableWebView
import com.czy4201b.fastfill.feature.fastfill.web.WebLoginScreen

@Composable
fun MainView(
    modifier: Modifier = Modifier,
    userFillTableViewModel: UserFillTableViewModel,
    timeSettingsViewModel: TimeSettingsViewModel,
    vm: MainViewViewModel
) {
    val uiState by vm.state.collectAsState()
    val isUserFillTableExpanded by userFillTableViewModel.expandState.collectAsState()

    uiState.currentFastFillJS?.let { currentFastFillJS ->
        if (uiState.isShowLoginWeb) {
            // 全屏 WebView，带返回键处理
            WebLoginScreen(
                modifier = Modifier.fillMaxSize(),
                fastFillJS = currentFastFillJS,
                onBack = { vm.closeLoginWeb() }
            )
        }

        // 这里需要修改
        if (uiState.isStartFilling) {
            HiddenFilledTableWebView(
                modifier = Modifier.fillMaxSize(),
                url = uiState.url,
                fastFillJS = currentFastFillJS,
                fillMap = userFillTableViewModel.userFillMap,
                onBack = { vm.endFilling() },
            )
        }
    } ?: run {
        if (uiState.isShowLoginWeb || uiState.isStartFilling) {
            ErrorPage("- fastFillJS数据为null")
        }
    }

    if (isUserFillTableExpanded) {
        Popup {
            val visibleState = remember { MutableTransitionState(false) }

            LaunchedEffect(Unit) {
                visibleState.targetState = true
            }

            LaunchedEffect(visibleState.currentState, visibleState.targetState) {
                if (!visibleState.currentState && !visibleState.targetState) {
                    userFillTableViewModel.zoomTable()
                }
            }

            BackHandler { visibleState.targetState = false }

            AnimatedVisibility(
                visibleState = visibleState,
                enter = fadeIn() + scaleIn(initialScale = 0.8f),
                exit = fadeOut() + scaleOut(targetScale = 0.8f),
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Text("你好吗？")
                }
            }
        }
    }

    if (!uiState.isShowLoginWeb && !uiState.isStartFilling) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                Row(
                    modifier = Modifier.padding(
                        start = 12.dp,
                        top = 38.dp,
                        end = 12.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FastFill",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Serif
                    )
                    Spacer(Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable(
                                onClick = {

                                }
                            )
                            .padding(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = null,
                            modifier = Modifier
                        )
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = modifier.padding(innerPadding)
            ) {
                URLTextField(
                    value = uiState.url,
                    isError = uiState.isUrlInvalid,
                    onValueChange = { vm.updateUrl(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp), // 遵循设计规范 8.dp
                    maxLines = 3
                ) {
                    Image(
                        painter = painterResource(R.drawable.txdocs),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                AnimatedVisibility(!uiState.isCurrentLogin) {
                    // 登录按钮
                    ModernOutlinedButton(
                        onClick = {
                            vm.showLoginWeb()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .height(48.dp)
                    ) {
                        Text("登录", color = MaterialTheme.colorScheme.primary)
                    }
                }

                AnimatedVisibility(uiState.isUrlInvalid) {
                    SnackBar(
                        "该URL非目标域名",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        buttonText = "清除",
                        onButtonClicked = {
                            vm.clearUrl()
                        }
                    )
                }

                // 开始填入按钮
                ModernFilledButton(
                    onClick = {
                        vm.startFilling()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp) // 遵循设计规范 8.dp
                        .height(48.dp),
                    enabled = uiState.isCurrentLogin
                ) {
                    Text("开始自动化填入", color = MaterialTheme.colorScheme.onPrimary)
                }

                // 退出登录按钮
                AnimatedVisibility(uiState.isCurrentLogin) {
                    ModernOutlinedButton(
                        onClick = {
                            vm.showLoginWeb()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp) // 遵循设计规范 8.dp
                            .height(48.dp)
                    ) {
                        Text("退出登录", color = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(Modifier.height(8.dp))

                TabBar(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .fillMaxWidth(),
//                    tabList = listOf("数据", "定时", "自定义"),
                    tabList = listOf("数据", "定时"),
                    currentTab = uiState.currentTab,
                    onTabClicked = {
                        vm.selectTab(it)
                    }
                )

                TabPager(
                    pageCount = 2,
                    currentPage = uiState.currentTab,
                ) { page ->
                    when (page) {
                        0 -> UserFillTable(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp) // 遵循设计规范 8.dp
                                .height(400.dp), // 高度未定，再说
                            vm = userFillTableViewModel
                        )

                        1 -> TimeSettings(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .height(400.dp),
                            vm = timeSettingsViewModel
                        )
//
//                        2 -> Column {
//                            Text("99999")
//                        }
                    }
                }
            }
        }
    }
}