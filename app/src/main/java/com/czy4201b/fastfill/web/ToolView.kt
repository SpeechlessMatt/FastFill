package com.czy4201b.fastfill.web

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.czy4201b.fastfill.javaScripts.FastFillJS
import java.util.Calendar
import java.util.Date

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HiddenFilledTableWebView(
    modifier: Modifier = Modifier,
    startDate: Date? = null,
    url: String,
    fastFillJS: FastFillJS,
    fillData: Map<String, String>,
    onBack: () -> Unit
//    onDone: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    val webView: WebView = remember {
        WebView(context).apply {
            // 隐藏
            layoutParams = ViewGroup.LayoutParams(600, 1000)

            val cm = CookieManager.getInstance()
            cm.setAcceptCookie(true)
            cm.setAcceptThirdPartyCookies(this, true)   // this = WebView

            // 设置为桌面版 Chrome 的 UA
            val desktopUA =
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
            settings.apply {
                setUserAgentString(desktopUA)
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                builtInZoomControls = false
                displayZoomControls = false
                useWideViewPort = true
                loadWithOverviewMode = true
            }

            setLayerType(View.LAYER_TYPE_HARDWARE, null)

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String?) {
                    startDate?.let {
                        // 加载脚本
                        view.evaluateJavascript(
                            fastFillJS.disableTimeCheckAction(
                                toDate = it
                            )
                        ) { _ ->

                        }
                    }
                    // 加载脚本
                    view.evaluateJavascript(
                        fastFillJS.fillAction(targetMap = fillData)
                    ) { _ ->
                        // 先啥都不写
                    }
                }
            }
        }
    }
    // 劫持返回键为回到上一页，有用的
    BackHandler { if (webView.canGoBack()) webView.goBack() else onBack() }
    AndroidView(
        modifier = modifier,
        factory = { webView }
    ) {
        Log.d("WebView", "now load:$url")
        it.loadUrl(url)
    }
}


