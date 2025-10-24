package com.czy4201b.fastfill.feature.fastfill.javaScripts

import android.util.Log
import android.webkit.CookieManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.util.Date

abstract class BaseFastFillJS : FastFillJS {
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

    override fun disableTimeCheckAction(toDate: Date): String {
        val targetTs = toDate.time                   // Kotlin Date → 时间戳
        return """
        (function(){
            const RealDate = Date;
            const offset = $targetTs - RealDate.now();   // 直接算差值
            function FakeDate(...a){
                return a.length ? new RealDate(...a) : new RealDate(RealDate.now() + offset);
            }
            Object.setPrototypeOf(FakeDate, RealDate);
            FakeDate.prototype = RealDate.prototype;
            FakeDate.now = () => RealDate.now() + offset;
            FakeDate.parse = RealDate.parse;
            FakeDate.UTC   = RealDate.UTC;
            window.Date = FakeDate;
        })();
    """.trimIndent()
    }

    override fun exitLogin() = Unit
}