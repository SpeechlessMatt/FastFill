package com.czy4201b.fastfill.feature.update.data

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

data class UpdateInfo(
    val version: String,
    val publishedAt: String,
    val apkUrl: String,
    val changelog: String
)

/**
 * 接口：以后想换数据源（Gitee、自建服务器）只需再实现一个类
 */
interface UpdateRepository {
    suspend fun getLatest(owner: String, repo: String): UpdateInfo
}

/**
 * 实现：用 GitHub Release 作为数据源
 */
@Singleton
class GitHubUpdateRepository @Inject constructor(
    private val client: OkHttpClient  // 注入，而不是自己创建
) : UpdateRepository {
    override suspend fun getLatest(owner: String, repo: String): UpdateInfo {
        val url = "https://api.github.com/repos/$owner/$repo/releases/latest"
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { resp ->
            Log.d("Update","get resp: ${resp.code}")
            if (!resp.isSuccessful) throw IllegalStateException("HTTP ${resp.code}")
            val json = JSONObject(resp.body.string())

            val version = json.getString("tag_name")
            val publishedAt = json.getString("published_at")
            val changelog = json.optString("body") ?: ""

            val assets = json.getJSONArray("assets")
            val apkUrl = (0 until assets.length())
                .map { assets.getJSONObject(it) }
                .firstOrNull { it.getString("name").endsWith(".apk", true) }
                ?.getString("browser_download_url")
                ?: throw NoSuchElementException("本 Release 没有 APK")

            Log.d("Update","$version")
            Log.d("Update","$publishedAt")
            Log.d("Update", apkUrl)
            Log.d("Update", changelog)

            return UpdateInfo(version, publishedAt, apkUrl, changelog)
        }
    }
}