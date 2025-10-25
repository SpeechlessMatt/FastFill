package com.czy4201b.fastfill.core.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class MyColumnDsl

// 1. 内层作用域：只允许用户调一次 content()
@MyColumnDsl
class MyItemScope {
    val contentList = mutableListOf<@Composable () -> Unit>()

    // 唯一入口：把内容收走
    fun content(content: @Composable () -> Unit) {
        contentList += content
    }
}

// 2. 根作用域
@MyColumnDsl
class MyColumnScope {
    val items = mutableListOf<@Composable () -> Unit>()

    inline fun item(block: MyItemScope.() -> Unit) {
        val scope = MyItemScope()
        scope.block()
        items.addAll(scope.contentList)
    }

    // 把收集到的内容一次性画出来
    @Composable
    fun Build() {
        Column {
            items.forEach { it() }
        }
    }
}

// 3. 真正的 Composable 入口
@Composable
inline fun MyColumn(block: MyColumnScope.() -> Unit) {
    val scope = MyColumnScope()
    scope.block()
    scope.Build()
}