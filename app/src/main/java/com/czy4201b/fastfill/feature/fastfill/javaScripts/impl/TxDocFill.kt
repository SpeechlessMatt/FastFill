package com.czy4201b.fastfill.feature.fastfill.javaScripts.impl

import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import com.czy4201b.fastfill.feature.fastfill.javaScripts.ExtraData
import com.czy4201b.fastfill.feature.fastfill.javaScripts.FastFillJS
import com.czy4201b.fastfill.feature.fastfill.javaScripts.FastInjectScope
import com.czy4201b.fastfill.feature.fastfill.javaScripts.click
import com.czy4201b.fastfill.feature.fastfill.javaScripts.fastInject
import com.czy4201b.fastfill.feature.fastfill.javaScripts.forEachIndexed
import com.czy4201b.fastfill.feature.fastfill.javaScripts.get
import com.czy4201b.fastfill.feature.fastfill.javaScripts.innerText
import com.czy4201b.fastfill.feature.fastfill.javaScripts.simulateInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object TxDocFill : FastFillJS {
    val checkAuthProbe: String
        get() = "https://docs.qq.com/cgi-bin/online_docs/user_info?get_vip_info=1"
    override val name: String
        get() = "TxDocs"
    override val domain: String
        get() = "https://docs.qq.com"
    override val loginUrl: String
        get() = "https://docs.qq.com/desktop"

    override suspend fun checkLogin(): Boolean = withContext(Dispatchers.IO) {
        val cookie = CookieManager.getInstance().getCookie(domain) ?: return@withContext false
        val conn = URL(checkAuthProbe).openConnection() as HttpURLConnection

        Log.d("FastFill", "TxDocFill sent $name checkLogin()")
        conn.instanceFollowRedirects = false
        conn.setRequestProperty("Cookie", cookie)
        Log.d("FastFill", "responseCode: ${conn.responseCode}")
        val body = conn.inputStream.bufferedReader().use { it.readText() }
        val cgicode = JSONObject(body).optInt("cgicode", -1) == 0
        val retcode = JSONObject(body).optInt("retcode", -1) == 0
        val isSuccess = (cgicode && retcode)
        Log.d("FastFill", "code=$isSuccess")
        isSuccess
    }

    override fun fillAction(
        webView: WebView,
        url: String?,
        extraData: ExtraData
    ) {
        extraData.fillTable?.let { stringMap ->
            webView.fastInject {
                // 创建js字面量
                val map = createValueRef(stringMap)
                waitElement("div.dui-tabs-bar-container > ul > li:nth-child(1)", 15000).then { button ->
                    button.click()
                }
                setTimeOut(ms = 400) {
                    val titles = findAllElement(".question-title span")
                    val areas = findAllElement(".form-ui-component-basic-text textarea")
                    titles.forEachIndexed { element, idx ->
                        val textValue = element.innerText()
                        val areaEl = areas[idx]
                        execJs("if (${map.varName}.hasOwnProperty(${textValue.varName}) && ${areaEl.varName}) {")
                        val fillText = FastInjectScope.ValueRef(
                            "${map.varName}[${textValue.varName}]",
                            this@fastInject
                        )
                        areaEl.simulateInput(fillText)
                        execJs("}")
                    }

                    waitElement(".question-commit button", 15000).then { button ->
                        button.click()
                        waitElement("button.dui-modal-footer-ok", 10000).then { button2 ->
                            button2.click()
                        }
                    }
                }
            }
        }
    }

    override fun loginAction(webView: WebView) {
        return
    }

    override fun exitLoginAction(webView: WebView) {
        return
    }

}