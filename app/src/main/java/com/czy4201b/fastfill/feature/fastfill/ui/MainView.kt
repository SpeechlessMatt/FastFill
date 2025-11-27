package com.czy4201b.fastfill.feature.fastfill.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

@Composable
fun MainView(
    modifier: Modifier = Modifier,
    userFillTableViewModel: UserFillTableViewModel,
    timeSettingsViewModel: TimeSettingsViewModel,
    vm: MainViewViewModel
) {
    val uiState by vm.state.collectAsState()
    val isUserFillTableExpanded by userFillTableViewModel.expandState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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

    AnimatedVisibility(
        visible = isUserFillTableExpanded,
        enter = fadeIn() + scaleIn(initialScale = 0.8f),
        exit = fadeOut() + scaleOut(targetScale = 0.8f),
    ) {

        UserFillTableExpanded(
            modifier = Modifier.fillMaxSize(),
            vm = userFillTableViewModel
        )
    }

    if (!uiState.isShowLoginWeb && !uiState.isStartFilling) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                ) {
                    Text("暂无更多设计", Modifier.padding(16.dp))
                }
            }
        ) {
            Scaffold(
                modifier = modifier.fillMaxSize(),
                topBar = {
                    Row(
                        modifier = Modifier.padding(
                            start = 8.dp,
                            top = 38.dp,
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable(
                                    onClick = {
                                        scope.launch { drawerState.open() }
                                    }
                                )
                                .padding(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = null,
                                modifier = Modifier
                            )
                        }
                        Spacer(Modifier.padding(end = 5.dp))
                        Text(
                            text = "FastFill",
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = FontFamily.Serif
                        )
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
}