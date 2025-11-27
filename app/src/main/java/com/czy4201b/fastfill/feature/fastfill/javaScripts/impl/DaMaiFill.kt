package com.czy4201b.fastfill.feature.fastfill.javaScripts.impl

import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import com.czy4201b.fastfill.feature.fastfill.javaScripts.ExtraData
import com.czy4201b.fastfill.feature.fastfill.javaScripts.FastFillJS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object DaMaiFill : FastFillJS {
    override val name: String
        get() = "DaMai"
    override val domain: String
        get() = "https://www.damai.cn/"
    override val iconPath: String
        get() = "null"
    override val loginUrl: String
        get() = "https://passport.damai.cn/login"

    private fun generateUrl(): String {
        val timestampMillis = System.currentTimeMillis()
        val url = "https://mtop.damai.cn" +
                "/h5/mtop.damai.mxm.user.accesstoken.getbytbs/1.0/?jsv=2.7.2&" +
                "appKey=12574478&t=$timestampMillis&sign=07a73d5beab42fb90897efff46527b53&" +
                "api=mtop.damai.mxm.user.accesstoken.getbytbs&v=1.0&type=jsonp&dataType=" +
                "jsonp&callback=mtopjsonp1&data=%7B%7D"
        return url
    }

    override suspend fun checkLogin(): Boolean = withContext(Dispatchers.IO) {
        val cookie = CookieManager.getInstance().getCookie(domain) ?: return@withContext false
        val conn = URL(generateUrl()).openConnection() as HttpURLConnection

        Log.d("FastFill", "DaMaiFill sent $name checkLogin()")
        conn.instanceFollowRedirects = false
        conn.setRequestProperty("Cookie", cookie)
        Log.d("FastFill", "responseCode: ${conn.responseCode}")
        val body = conn.inputStream.bufferedReader().use { it.readText() }
        val jsonString = body.removePrefix(" mtopjsonp1(").removeSuffix(")")
        // 提取 ret 字段
        val isSuccess = try {
            val ret = JSONObject(jsonString)
                .getJSONArray("ret")
                .getString(0) // "FAIL_SYS_ILLEGAL_ACCESS::非法请求"
            ret == "FAIL_SYS_ILLEGAL_ACCESS::非法请求"
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
        Log.d("FastFill", "code=$isSuccess")
        isSuccess
    }

    override fun fillAction(
        webView: WebView,
        url: String?,
        extraData: ExtraData
    ) {
        TODO("Not yet implemented")
    }

    override fun loginAction(webView: WebView) {
        TODO("Not yet implemented")
    }

    override fun exitLoginAction(webView: WebView) {
        TODO("Not yet implemented")
    }

}