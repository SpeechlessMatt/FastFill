package com.czy4201b.fastfill.feature.fastfill.javaScripts

import android.webkit.WebView

interface FastFillJS {
    val name: String
    val domain: String
    val iconPath: String

    /**
     * 登录使用的网址
     */
    val loginUrl: String

    /**
     * 可选：如果无法实现登录检查可以改成false
     */
    val supportsLoginCheck: Boolean get() = true

    /**
     * 检查网站登录状态
     * @return 登录结果，是否已经登录
     */
    suspend fun checkLogin(): Boolean {
        throw UnsupportedOperationException("$name 不支持登录状态检查")
    }

    /**
     * fastfill执行的fillAction
     * @param url 页面加载完成时页面的url
     * @param extraData 执行fastfill需要的数据
     */
    fun fillAction(webView: WebView, url: String?, extraData: ExtraData)

    /**
     * 进入登录url的时候执行的loginAction
     */
    fun loginAction(webView: WebView)

    /**
     * 进入登录url的时候执行的loginAction
     */
    fun exitLoginAction(webView: WebView)
}

/**
 * 额外数据类
 * @param fillTable 用户预设的fastfill填充内容
 * @param extraMap 通过继承FastFillJS定义extraMap，从用户获取的自定义设置
 */
data class ExtraData(
    val fillTable: Map<String, String>?,
    val extraMap: Map<Any, Any?>?
)