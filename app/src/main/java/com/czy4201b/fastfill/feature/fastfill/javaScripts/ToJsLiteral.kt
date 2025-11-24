package com.czy4201b.fastfill.feature.fastfill.javaScripts

internal fun Any?.toJsLiteral(): String = when (this) {
    null -> "null"

    is Number, is Boolean -> this.toString()

    is String -> {
        val escaped = this
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
        "\"$escaped\""
    }

    is Map<*, *> -> {
        val entries = this.entries.joinToString(",") { (k, v) ->
            val key = k.toJsLiteral()
            val valJs = v.toJsLiteral()
            "$key:$valJs"
        }
        "{${entries}}"
    }

    is Iterable<*> -> {
        val items = this.joinToString(",") { it.toJsLiteral() }
        "[$items]"
    }

    else -> {
        // 直接拒绝非法类型，而不是瞎猜
        throw IllegalArgumentException(
            "数组元素类型不支持: ${this.let { it::class.simpleName } ?: "null"}" +
                    "\n支持的类型: String, Int, Boolean, null"
        )
    }
}