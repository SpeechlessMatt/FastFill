package com.czy4201b.fastfill.web

import android.util.Log
import android.webkit.CookieManager
import java.util.Calendar
import java.util.Date

/**
 * 把 6 个 Int 转成 Date
 * 月份按生活习惯：1 就是 1 月，不用减 1
 */
fun toDate(
    year: Int,
    month: Int,      // 1..12
    day: Int,
    hour: Int = 0,
    minute: Int = 0,
    second: Int = 0
): Date = Calendar.getInstance().apply {
    clear()      // 清空当前时间
    set(year, month - 1, day, hour, minute, second) // 月份 0 基
}.time

/**
 * 删除指定域名（及子域名）下的所有 WebView Cookie
 * @param domain 例：".example.com" 或 "example.com"
 */
fun clearCookiesForDomain(domain: String) {
    val cm = CookieManager.getInstance()
    val rawCookies = cm.getCookie(domain) ?: return        // 可能为 null
    val cookies = rawCookies.split(";".toRegex())
    for (cookie in cookies) {
        val eq = cookie.indexOf('=')
        val name = if (eq > 0) cookie.substring(0, eq).trim() else continue
        // 拼一条“立即过期”的 Set-Cookie 头
        val expired = "$name=; Expires=Wed, 01 Jan 1970 00:00:00 GMT; Max-Age=0"
        val url = domain.replaceFirst(Regex("^https?://"), "")
        // 对主域及 www 子域各写一次，确保命中
        Log.d("Cookie", "Delete: $url")
        cm.setCookie("https://$url", expired)
        cm.setCookie("https://www.$url", expired)
    }
    cm.flush()
}
