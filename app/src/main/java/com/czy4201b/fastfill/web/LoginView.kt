package com.czy4201b.fastfill.web

import android.annotation.SuppressLint
import android.util.Log
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


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebLoginScreen(
    modifier: Modifier = Modifier,
    fastFillJS: FastFillJS,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(600, 1000)

            val cm = CookieManager.getInstance()
            cm.setAcceptCookie(true)
            cm.setAcceptThirdPartyCookies(this, true)   // this = WebView

            // 设置为桌面版 Chrome 的 UA
            val desktopUA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
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

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String?) {
                    super.onPageFinished(view, url)
                    CookieManager.getInstance().flush()
                    Log.i("Webview", "onPageFinished: $url")
                    val cm = CookieManager.getInstance()
                    val c  = cm.getCookie(url)          // 用当前 url
                    Log.i("Webview", "cookie@$url → $c")
                }
            }
        }
    }
    BackHandler { onBack() }
    AndroidView(
        factory = { webView },
        modifier = modifier
    ) {
        val url = fastFillJS.loginUrl
        Log.d("WebView", "now load:$url")
        it.loadUrl(url)
    }
}