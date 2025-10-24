package com.czy4201b.fastfill.feature.fastfill.javaScripts.impl

import android.util.Log
import android.webkit.CookieManager
import com.czy4201b.fastfill.feature.fastfill.javaScripts.BaseFastFillJS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object TxDocFill : BaseFastFillJS() {
    override val name: String
        get() = "TxDocs"
    override val domain: String
        get() = "https://docs.qq.com"
    override val checkAuthProbe: String
        get() = "https://docs.qq.com/cgi-bin/online_docs/user_info?get_vip_info=1"
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

    override fun fillAction(targetMap: Map<String, String>): String {
        // 1. 用系统 JSON 工具一次性转义
        val jsObj = JSONObject(targetMap).toString()

        // 2. 全部用 function/var，不要用 ES6
        return """
        (function(){
          var obj = $jsObj;
        
          function clickWhenReady(text, maxTry, done) {
            var cnt = 0;
            var id = setInterval(function() {
                var li = [].slice.call(document.querySelectorAll('li'))
                            .find(function(el){ return el.textContent.trim() === text; });
                if (li) { li.click(); clearInterval(id); done(); }
                else if (++cnt >= maxTry) { clearInterval(id); }
            }, 300);
          }
        
          function clickOnceWhenAppear(selector, timeout) {
            return new Promise(function (resolve, reject) {
                var start = Date.now();
        
                // 1. 已经存在就直接点
                var el = document.querySelector(selector);
                if (el) { el.click(); resolve(); return; }
        
                // 2. 监听 DOM 变化
                var ob = new MutationObserver(function () {
                    var btn = document.querySelector(selector);
                    if (btn) {
                        ob.disconnect();
                        setTimeout(() => { btn.click(); }, 100);
                        resolve();
                    } else if (Date.now() - start > timeout) {
                        ob.disconnect();
                        reject('超时未找到 ' + selector);
                    }
                });
                ob.observe(document.body, { childList: true, subtree: true });
        
                // 3. 兜底超时
                setTimeout(function () {
                    ob.disconnect();
                    reject('超时未找到 ' + selector);
                }, timeout);
            });
          }
        
          function simulateInput(el, text) {
            // 1. 聚焦并清空
            el.focus();
            el.value = '';
            el.setAttribute('value', '');          // 某些框架读 attribute
            el.dispatchEvent(new Event('input',     { bubbles: true }));
            el.dispatchEvent(new Event('change',    { bubbles: true }));
        
            // 2. 逐字符输入
            for (var i = 0; i < text.length; i++) {
                var ch = text.charAt(i);
        
                // 2-1 键盘事件（React 17 依赖 keyCode）
                var keyEvent = new KeyboardEvent('keydown', {
                    key: ch, code: 'Key' + ch.toUpperCase(), keyCode: ch.charCodeAt(0),
                    which: ch.charCodeAt(0), bubbles: true, cancelable: true
                });
                el.dispatchEvent(keyEvent);
        
                // 2-2 改值 + 触发 input
                el.value += ch;
                el.setAttribute('value', el.value);
                el.dispatchEvent(new Event('input',  { bubbles: true }));
            }
        
            // 3. 失焦并触发 change / blur
            el.dispatchEvent(new Event('change', { bubbles: true }));
            el.blur();
            el.dispatchEvent(new Event('blur',   { bubbles: true }));
        
            // 4. 主动触发表单校验（antd / element 通用）
            var fakeEvent = new Event('change', { bubbles: true });
            Object.defineProperty(fakeEvent, 'target', { value: el, enumerable: true });
            el.dispatchEvent(fakeEvent);
          }
        
          function fillAll() {
            var titles = document.querySelectorAll('.question-title span');
            var areas  = document.querySelectorAll('.form-ui-component-basic-text textarea');
            for (var i = 0; i < titles.length; i++) {
                var key = titles[i].textContent.trim();
                if (obj.hasOwnProperty(key) && areas[i]) {
                    simulateInput(areas[i], obj[key]);
                }
            }
        
            // 提交
            clickOnceWhenAppear('.question-commit button', 15000)
            .then(function () {
                // 提交之后，等“确认”按钮出现再点一次
                return clickOnceWhenAppear('button.dui-modal-footer-ok', 10000);
            })
            .then(function () {
                console.log('提交+确认 全部完成');
            });
          }
        
          // 主流程
          clickWhenReady('填写', 50, function(){
            setTimeout(fillAll, 400);   // 等界面切完再填
          });
        })();
    """.trimIndent()
    }

    override fun exitLogin() {
    }
}