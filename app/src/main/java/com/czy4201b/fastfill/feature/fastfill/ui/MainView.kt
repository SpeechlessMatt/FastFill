package com.czy4201b.fastfill.feature.fastfill.ui

import android.icu.util.Calendar
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.czy4201b.fastfill.R
import com.czy4201b.fastfill.core.components.ModernFilledButton
import com.czy4201b.fastfill.core.components.ModernOutlinedButton
import com.czy4201b.fastfill.core.components.SnackBar
import com.czy4201b.fastfill.feature.fastfill.javaScripts.impl.TxDocFill
import com.czy4201b.fastfill.feature.fastfill.web.HiddenFilledTableWebView
import com.czy4201b.fastfill.feature.fastfill.web.WebLoginScreen

@Composable
fun MainView(
    modifier: Modifier = Modifier,
    userFillTableViewModel: UserFillTableViewModel = viewModel(),
    vm: MainViewViewModel = viewModel()
) {
    val uiState by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.apply {
            checkLogin(TxDocFill)
        }
    }

    if (uiState.isShowLoginWeb) {
        // 全屏 WebView，带返回键处理
        WebLoginScreen(
            modifier = modifier.fillMaxSize(),
            fastFillJS = TxDocFill,
            onBack = { vm.closeLoginWeb() }
        )
    }

    // 这里需要修改
    if (uiState.isStartFilling) {
        HiddenFilledTableWebView(
            modifier = modifier.fillMaxSize(),
            url = uiState.url,
            fastFillJS = TxDocFill,
            fillData = userFillTableViewModel.userFillMap,
            onBack = { vm.endFilling() },
            startDate = Calendar.getInstance().apply { // 这里需要修改
                add(Calendar.YEAR, 10) // 这里需要修改
            }.time // 这里需要修改
        )
    }

    if (!uiState.isShowLoginWeb && !uiState.isStartFilling) {
        Column(
            modifier = modifier
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

            AnimatedVisibility(uiState.loginMap[TxDocFill] == false) {
                // 登录按钮
                ModernOutlinedButton(
                    onClick = {
                        vm.showLoginWeb()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
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
                    vm.startFilling(TxDocFill)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp), // 遵循设计规范 8.dp
                enabled = uiState.loginMap[TxDocFill] == true
            ) {
                Text("开始自动化填入", color = MaterialTheme.colorScheme.onPrimary)
            }

            // 退出登录按钮
            AnimatedVisibility(uiState.loginMap[TxDocFill] == true) {
                ModernOutlinedButton(
                    onClick = {
                        vm.showLoginWeb()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp) // 遵循设计规范 8.dp
                ) {
                    Text("退出登录", color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(Modifier.height(8.dp))

            TabBar(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .fillMaxWidth(),
                tabList = listOf("数据", "定时", "自定义"),
                currentTab = uiState.currentTab,
                onTabClicked = {
                    vm.selectTab(it)
                }
            )

            TabPager(
                pageCount = 3,
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
                            .height(400.dp)
                    )
                    2 -> Column {
                        Text("99999")
                    }
                }
            }
        }
    }

}