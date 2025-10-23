package com.czy4201b.fastfill

import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.czy4201b.fastfill.javaScripts.impl.TxDocFill
import com.czy4201b.fastfill.ui.ModernFilledButton
import com.czy4201b.fastfill.ui.ModernOutlinedButton
import com.czy4201b.fastfill.ui.SnackBar
import com.czy4201b.fastfill.ui.TabBar
import com.czy4201b.fastfill.ui.URLTextField
import com.czy4201b.fastfill.ui.UserFillTable
import com.czy4201b.fastfill.ui.theme.FastFillTheme
import com.czy4201b.fastfill.web.HiddenFilledTableWebView
import com.czy4201b.fastfill.web.WebLoginScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FastFillTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        Row(
                            modifier = Modifier.padding(16.dp, top = 38.dp, bottom = 10.dp)
                        ) {
                            Text(
                                text = "FastFill",
                                style = MaterialTheme.typography.titleLarge,
                                fontFamily = FontFamily.Serif
                            )
                        }
                    }
                ) { innerPadding ->
                    MainView(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainView(
    modifier: Modifier = Modifier,
    userFillViewModel: UserFillViewModel = viewModel(),
    vm: MainViewViewModel = viewModel()
) {
    val uiState by vm.state.collectAsState()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })

    LaunchedEffect(uiState.currentTab) {
        if (pagerState.currentPage != uiState.currentTab) {
            pagerState.animateScrollToPage(uiState.currentTab)
        }
    }

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
            fillData = userFillViewModel.userFillMap,
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
                    Text("登录")
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
                Text("开始自动化填入")
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
                    Text("退出登录")
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

            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false
            ) { page ->
                when (page) {
                    0 -> UserFillTable(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(380.dp) // 高度未定，再说
                            .padding(8.dp), // 遵循设计规范 8.dp
                        vm = userFillViewModel
                    )
                }
            }

            // 用户填入的匹配表 暂时不支持room写入内部存储


//            SubmitTimeTable(
//                modifier = Modifier.fillMaxWidth().padding(8.dp),
//                onConfirm = {},
//                onDismiss = {}
//            )
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