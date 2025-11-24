package com.czy4201b.fastfill.feature.fastfill.javaScripts

import android.util.Log
import android.webkit.WebView
import java.util.Date

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class FastInjectDsl

@FastInjectDsl
class FastInjectScope {
    internal val jsFunc = mutableListOf<String>()

    private var valueId = 0
    private var elementId = 0

    /**
     * 使用ElementRef封装value对象
     */
    class ValueRef internal constructor(
        val varName: String,
        internal val owner: FastInjectScope
    )

    fun createVar(jsExpression: String, hint: String? = null): ValueRef {
        val newVar = "_valFastInject_${valueId++}"
        jsFunc += "let $newVar = $jsExpression;\n"
        return ValueRef(newVar, this)
    }

    sealed class DomRef(open val varName: String, open val selector: String)

    /**
     * 使用ElementRef封装Dom对象
     */
    class ElementRef internal constructor(
        override val varName: String,    // 生成的 JS 变量名，如 "_elFastInject_0"
        override val selector: String,  // CSS 选择器
        internal val owner: FastInjectScope
    ) : DomRef(varName, selector)

    /**
     * 使用ElementRefs封装Dom对象数组
     */
    class ElementsRef internal constructor(
        override val varName: String,
        override val selector: String,
        internal val owner: FastInjectScope
    ) : DomRef(varName, selector)

    /**
     * 自定义js
     * @param js 自定义js
     */
    fun execJs(js: String) {
        jsFunc += js
    }

    /**
     * 运行在匿名函数中的js，不会污染全局变量，相对更加安全
     * @param js 自定义js
     */
    fun execIsolatedJs(js: String) {
        jsFunc += """
            (function() {$js})();
        """.trimIndent()
    }

    private var isDateHacked = false

    fun disableTimeCheck(toDate: Date) {
        if (isDateHacked) {
            Log.w("FastInject", "Date already hacked in this scope, ignoring duplicate call")
            return
        }

        val targetTs = toDate.time
        jsFunc += """
            (function(){
                if (window.__dateHacked) {
                    console.warn('[FastInject] Date already hacked in page, skipping...');
                    return;
                }
                
                const RealDate = Date;
                const offset = $targetTs - RealDate.now();   // 直接算差值
                
                function FakeDate(...args) {
                    // 构造时直接给 RealDate 注入偏移后的时间值
                    if (this instanceof FakeDate) {
                        if (args.length === 0) {
                            return new RealDate(RealDate.now() + offset);
                        } else {
                            return new RealDate(...args);
                        }
                    }
                    // 当作普通函数调用，如 Date()
                    return new RealDate(RealDate.now() + offset).toString();
                }
                
                // 保持原型链完整
                Object.setPrototypeOf(FakeDate, RealDate);
                FakeDate.prototype = RealDate.prototype;
                
                FakeDate.now = () => RealDate.now() + offset;
                FakeDate.parse = RealDate.parse;
                FakeDate.UTC   = RealDate.UTC;
                
                // 保留 toString，以减少被工具检测
                FakeDate.toString = RealDate.toString.bind(RealDate);
                
                // 保存原始对象并提供恢复方法
                window.__originalDate = RealDate;
                window.__dateHacked = true;
                window.__restoreDate = () => {
                    window.Date = window.__originalDate;
                    delete window.__originalDate;
                    delete window.__restoreDate;
                    delete window.__dateHacked;
                };
            
                window.Date = FakeDate;
            })();
        """.trimIndent()
        isDateHacked = true
    }

    fun restoreDate() {
        jsFunc += """
            (function(){
                if (window.__restoreDate) {
                    window.__restoreDate();
                } else {
                    console.warn('[FastInject] No Date hack found to restore');
                }
            })();
        """.trimIndent()

        isDateHacked = false
    }

    /**
     * 通过选择器获取Dom元素，相当于document.querySelector
     * @param selector 元素选择器
     */
    fun findElement(selector: String): ElementRef {
        val varName = "_elFastInject_${elementId++}"

        jsFunc += """
            const $varName = document.querySelector("$selector");
        """.trimIndent()
        return ElementRef(varName, selector, this)
    }

    /**
     * 通过选择器获取Dom元素数组，相当于document.querySelectorAll
     * @param selector 元素选择器
     */
    fun findAllElement(selector: String): ElementsRef {
        val varName = "_elFastInject_${elementId++}"

        jsFunc += """
            const $varName = document.querySelectorAll("$selector");
        """.trimIndent()
        return ElementsRef(varName, selector, this)
    }

    fun setTimeOut(
        ms: Int,
        block: FastInjectScope.() -> Unit
    ) {
        jsFunc += "setTimeout(function() {\n"
        this.block()
        jsFunc += "}, $ms);"
    }

    private var waitHelperInjected = false

    private fun ensureWaitHelper() {
        if (waitHelperInjected) return
        waitHelperInjected = true

        jsFunc += """
            window.__waitForElement = function(selector, timeout) {
                return new Promise(function (resolve, reject) {
                    var start = Date.now();
    
                    var el = document.querySelector(selector);
                    if (el) { resolve(el); return; }
    
                    var ob = new MutationObserver(function () {
                        var el2 = document.querySelector(selector);
                        if (el2) {
                            ob.disconnect();
                            resolve(el2);
                        } else if (Date.now() - start > timeout) {
                            ob.disconnect();
                            reject('超时未找到 ' + selector);
                        }
                    });
    
                    ob.observe(document.body, { childList: true, subtree: true });
    
                    setTimeout(function () {
                        ob.disconnect();
                        reject('超时未找到 ' + selector);
                    }, timeout);
                });
            };
        """.trimIndent()
    }

    fun waitElement(selector: String, timeout: Int): WaitedElementRef {
        val varName = "_elFastInject_${elementId++}"
        ensureWaitHelper()
        return WaitedElementRef(varName, selector, timeout, this)
    }

    class WaitedElementRef internal constructor(
        private val varName: String,
        private val selector: String,
        private val timeout: Int,
        private val owner: FastInjectScope
    ) {
        fun then(block: FastInjectScope.(ElementRef) -> Unit) {
            val tempRef = ElementRef(varName, selector, owner)

            owner.jsFunc += """
                __waitForElement("$selector", $timeout).then(el => {
                    window.$varName = el;
            """.trimIndent()
            // 调用block
            block.invoke(owner, tempRef)
            owner.jsFunc += """
                }).catch(err => console.error("[FastInject waitElement] " + err));
            """.trimIndent()
        }
    }

    /**
     * 创建js变量，并返回可操作的FastInject可操作的ValueRef对象
     * 注意：暂时只能支持基本类型String、Int、Boolean、数组类型Map(Map里面的类型也只能基本类型)
     * 如果需要使用对象，请使用execJs()自定义
     * @param kValue kotlin变量
     */
    fun createValueRef(kValue: Any?): ValueRef {
        val jsLiteral = kValue.toJsLiteral()
        val ret = createVar(jsLiteral)
        return ret
    }

    /**
     * 将自定义的变量名转换为FastInject可操作的ElementRef对象
     * @param varName 变量名字
     */
    fun wrapElement(varName: String, selectorHint: String? = null): ElementRef {
        jsFunc += """
            (function(){
                try {
                    if (typeof $varName === 'undefined') {
                        console.error('[FastInject] Variable "$varName" does not exist in JS context!');
                    }
                } catch (e) {
                    console.error('[FastInject] wrapElement("$varName") failed: ' + e);
                }
            })();
        """.trimIndent()
        return ElementRef(varName, selectorHint ?: "<external>", this)
    }

    /**
     * 将自定义的变量名转换为FastInject可操作的ElementsRef对象
     * @param varName 变量名字
     */
    fun wrapElements(varName: String, selectorHint: String? = null): ElementsRef {
        jsFunc += """
            (function(){
                try {
                    if (typeof $varName === 'undefined') {
                        console.error('[FastInject] Variable "$varName" does not exist in JS context!');
                    }
                } catch (e) {
                    console.error('[FastInject] wrapElement("$varName") failed: ' + e);
                }
            })();
        """.trimIndent()
        return ElementsRef(varName, selectorHint ?: "<external>", this)
    }

    fun WebView.build() {
        if (jsFunc.isEmpty()) {
            Log.w("FastInject", "[FastInject] No JS to build, ignored.")
            return
        }

        // 将所有 DSL 收集的 JavaScript 拼成一个脚本
        val finalJs = "(function() {\n${jsFunc.joinToString("\n")}\n})();"
        
//        Log.d("FastInject", finalJs)
        // 注入进 WebView
        this.evaluateJavascript(finalJs) { result ->
            Log.d("FastInject", "JS executed, result=$result")
        }

        // 注入后自动清空，避免下一次重复注入
        jsFunc.clear()
    }
}

inline fun WebView.fastInject(block: FastInjectScope.() -> Unit) {
    val scope = FastInjectScope().apply(block)
    scope.run { build() }   // 调用你刚补完的 build
}