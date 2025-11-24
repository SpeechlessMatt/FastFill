package com.czy4201b.fastfill.feature.fastfill.javaScripts

import android.util.Log
import android.webkit.CookieManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

abstract class BaseFastFillJS : FastFillJS {
    abstract val checkAuthProbe: String

    override suspend fun checkLogin(): Boolean = withContext(Dispatchers.IO) {
        val cookie = CookieManager.getInstance().getCookie(domain) ?: return@withContext false
        val desktopUA =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        val conn = URL(checkAuthProbe).openConnection() as HttpURLConnection
        conn.setRequestProperty("User-Agent", desktopUA)
        Log.d("network", "BaseFastFillJs sent $name checkLogin()")
        conn.instanceFollowRedirects = false
        conn.setRequestProperty("Cookie", cookie)
        Log.d("network", "responseCode: ${conn.responseCode}")
        conn.responseCode in 200..299
    }
}