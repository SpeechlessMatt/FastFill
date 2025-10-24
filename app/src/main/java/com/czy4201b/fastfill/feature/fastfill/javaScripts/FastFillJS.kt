package com.czy4201b.fastfill.feature.fastfill.javaScripts

import java.util.Date

interface FastFillJS{
    val name: String
    val domain: String
    val checkAuthProbe: String
    val loginUrl: String

    suspend fun checkLogin(): Boolean

    // 返回一个jsString
    fun fillAction(targetMap: Map<String, String>): String

    fun disableTimeCheckAction(toDate: Date): String

    fun exitLogin(): Unit
}