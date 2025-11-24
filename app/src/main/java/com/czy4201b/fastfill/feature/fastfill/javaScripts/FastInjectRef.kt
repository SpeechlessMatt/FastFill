package com.czy4201b.fastfill.feature.fastfill.javaScripts

import com.czy4201b.fastfill.feature.fastfill.javaScripts.FastInjectScope.ElementRef
import com.czy4201b.fastfill.feature.fastfill.javaScripts.FastInjectScope.ElementsRef
import com.czy4201b.fastfill.feature.fastfill.javaScripts.FastInjectScope.ValueRef

/* ---------------------------Ref定义--------------------------------------*/
// 目前问题：每一次访问都会生成一个新的，也不算bug，就是没有智能缓存机制，不太好
operator fun ElementsRef.get(index: Int): ElementRef {
    // 生成一个新的 varName
    val childVar = "${varName}_idxFastInject_$index"
    owner.jsFunc += """
                const $childVar = $varName[$index];
                if (!$childVar) {
                    console.warn('[FastInject] Index $index out of bounds for "$selector", length: $varName.length');
                }
            """.trimIndent()

    return ElementRef(childVar, "$selector[$index]", owner)
}

operator fun ElementsRef.get(indexRef: ValueRef): ElementRef {
    // 生成一个新的 varName
    val childVar = "${varName}_idxFastInject_${indexRef.varName}"
    owner.jsFunc += """
                const $childVar = $varName[${indexRef.varName}];
                if (!$childVar) {
                    console.warn('[FastInject] Index ${indexRef.varName} out of bounds for "$selector", length: $varName.length');
                }
            """.trimIndent()

    return ElementRef(childVar, "$selector[${indexRef.varName}]", owner)
}

fun ElementsRef.size(): ValueRef {
    val childVar = "${varName}_value_size"
    owner.jsFunc += """
                const $childVar = ${varName}.length;
            """.trimIndent()

    return ValueRef(childVar, owner)
}

/**
 * 遍历数组，每个元素执行 block
 */
fun ElementsRef.forEach(block: ElementsRef.(ElementRef) -> Unit) {
    val countVar = "${varName}_length"
    owner.jsFunc += "const $countVar = $varName.length;\n"

    // 生成一个 JS 循环
    owner.jsFunc += "for (let _idxFastInject = 0; _idxFastInject < $countVar; _idxFastInject++) {\n"
    val elementVar = "${varName}_idxFastInject_loop"
    owner.jsFunc += "const $elementVar = $varName[_idxFastInject];\n"

    // 创建临时 ElementRef 并调用 block
    val tempRef = ElementRef(elementVar, "$selector[_idxFastInject]", owner)
    block(this, tempRef)

    owner.jsFunc += "}\n"
}

/**
 * 遍历数组，每个元素执行 block，同时传入一个valueRef表示index
 */
fun ElementsRef.forEachIndexed(block: ElementsRef.(ElementRef, ValueRef) -> Unit) {
    val countVar = "${varName}_length"
    owner.jsFunc += "const $countVar = $varName.length;\n"

    // 生成一个 JS 循环
    owner.jsFunc += "for (let _idxFastInject = 0; _idxFastInject < $countVar; _idxFastInject++) {\n"
    val elementVar = "${varName}_idxFastInject_loop"
    owner.jsFunc += "const $elementVar = $varName[_idxFastInject];\n"

    // 创建临时 ElementRef 并调用 block
    val tempRef = ElementRef(elementVar, "$selector[_idxFastInject]", owner)
    block(this, tempRef, ValueRef("_idxFastInject", owner))
    owner.jsFunc += "}\n"
}

/**
 * 模拟人手输入：可以解决大部分input无法解决的问题
 * @param textRef 拥有String的valueRef
 */
fun ElementRef.simulateInput(textRef: ValueRef) {
    owner.jsFunc += """
        (function() {
            const el = ${this.varName};
            if (!el) {
                console.error('[FastInject] Element ${this.varName} does not exist in JS context!');
                return;
            }
            
            (function (el, value) {
                if (!el) return;
    
                el.focus(); // 保证组件内部 focus 逻辑生效
    
                // 清空并准备 tracker
                const lastValue = el.value;
                el.value = '';
    
                const tracker = el._valueTracker;
                if (tracker) tracker.setValue(lastValue);
    
                // 模拟中文输入（IME）
                el.dispatchEvent(new CompositionEvent('compositionstart', { bubbles: true, composed: true }));
    
                // 填入值
                el.value = value;
    
                el.dispatchEvent(new CompositionEvent('compositionend', { bubbles: true, composed: true, data: value }));
    
                // 触发 React/Vue 的 input 事件
                el.dispatchEvent(new InputEvent('input', { bubbles: true, composed: true, data: value, inputType: 'insertText' }));
    
                // 触发 change + blur 保证校验逻辑触发
                el.dispatchEvent(new Event('change', { bubbles: true, composed: true }));
                el.blur();
            })(el, ${textRef.varName});
        })();
    """.trimIndent()
}

/**
 * 模拟人手输入：可以解决大部分input无法解决的问题
 * @param text 输入的文本
 */
fun ElementRef.simulateInput(text: String) {
    val escaped = text.toJsLiteral()

    owner.jsFunc += """
        (function() {
            const el = ${this.varName};
            if (!el) {
                console.error('[FastInject] Element ${this.varName} does not exist in JS context!');
                return;
            }
            
            (function (el, value) {
                if (!el) return;
    
                el.focus(); // 保证组件内部 focus 逻辑生效
    
                // 清空并准备 tracker
                const lastValue = el.value;
                el.value = '';
    
                const tracker = el._valueTracker;
                if (tracker) tracker.setValue(lastValue);
    
                // 模拟中文输入（IME）
                el.dispatchEvent(new CompositionEvent('compositionstart', { bubbles: true, composed: true }));
    
                // 填入值
                el.value = value;
    
                el.dispatchEvent(new CompositionEvent('compositionend', { bubbles: true, composed: true, data: value }));
    
                // 触发 React/Vue 的 input 事件
                el.dispatchEvent(new InputEvent('input', { bubbles: true, composed: true, data: value, inputType: 'insertText' }));
    
                // 触发 change + blur 保证校验逻辑触发
                el.dispatchEvent(new Event('change', { bubbles: true, composed: true }));
                el.blur();
            })(el, $escaped);
        })();
    """.trimIndent()
}

fun ElementRef.click() {
    owner.jsFunc += """
        (function(){
            const el = ${this.varName};
            el.click();
        })();
    """.trimIndent()
}

fun ElementRef.innerText(): ValueRef {
    val newVar = owner.createVar("${this.varName}.innerText")
    return newVar
}

/**
 * 检查元素是否存在，存在就执行block，使用let一样的做法
 * @param block
 */
fun ElementRef.letIfExists(block: FastInjectScope.(ElementRef) -> Unit) {
    owner.jsFunc += "if (this.element != null) {\n"
    block.invoke(owner, this)
    owner.jsFunc += "\n}"
}
