package com.streamtv
import kotlinx.coroutines.Dispatchers; import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient; import okhttp3.Request
import org.jsoup.Jsoup; import java.util.concurrent.TimeUnit
object Parser {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            chain.proceed(chain.request().newBuilder().header("User-Agent", "Mozilla/5.0 (Android 14)").build())
        }.build()
    suspend fun parse(url: String): List<Channel> = withContext(Dispatchers.IO) {
        try {
            val html = client.newCall(Request.Builder().url(url).build()).execute().use { it.body?.string() ?: "" }
            val doc = Jsoup.parse(html)
            val genreMap = mutableMapOf<String, MutableList<Show>>()
            doc.select("a:has(img)").forEach { el ->
                val title = (el.attr("title").takeIf { it.isNotBlank() }
                    ?: el.select("img").firstOrNull()?.attr("alt")
                    ?: el.text()).takeIf { it.length > 2 } ?: return@forEach
                val pageUrl = el.attr("abs:href")
                if (pageUrl.isBlank()) return@forEach
                val genre = el.parents().take(5).mapNotNull {
                    it.select("[class*=genre],[class*=cat],[class*=type]").firstOrNull()?.text()
                }.firstOrNull()?.let { mapGenre(it) } ?: "ВИДЕО"
                genreMap.getOrPut(genre) { mutableListOf() }
                    .add(Show(title, listOf(Episode(title, pageUrl = pageUrl, durationMin = 60))))
            }
            if (genreMap.size >= 2) {
                genreMap.entries.sortedByDescending { it.value.size }.take(8).mapIndexed { i, (g, shows) ->
                    Channel(i, g, emojiFor(g), shows)
                }
            } else DemoData.channels
        } catch (e: Exception) { DemoData.channels }
    }
    private fun mapGenre(r: String): String {
        val l = r.lowercase()
        return when {
            l.contains("боевик") || l.contains("action") -> "БОЕВИК"
            l.contains("фантаст") || l.contains("sci-fi") -> "ФАНТАСТИКА"
            l.contains("драм") || l.contains("drama") -> "ДРАМА"
            l.contains("комед") || l.contains("comedy") -> "КОМЕДИЯ"
            l.contains("ужас") || l.contains("horror") -> "УЖАСЫ"
            l.contains("триллер") || l.contains("thriller") -> "ТРИЛЛЕР"
            l.contains("аниме") || l.contains("anime") -> "АНИМЕ"
            l.contains("докум") || l.contains("documentary") -> "ДОКУМЕНТАЛЬНОЕ"
            l.contains("крим") || l.contains("crime") -> "КРИМИНАЛ"
            else -> r.uppercase().take(12)
        }
    }
    private fun emojiFor(g: String) = when(g) {
        "БОЕВИК" -> "&#x1F4A5;"
        "ФАНТАСТИКА" -> "&#x1F680;"
        "ДРАМА" -> "&#x1F3AD;"
        "КОМЕДИЯ" -> "&#x1F604;"
        "УЖАСЫ" -> "&#x1F47B;"
        "ТРИЛЛЕР" -> "&#x1F52A;"
        "АНИМЕ" -> "&#x26E9;"
        "ДОКУМЕНТАЛЬНОЕ" -> "&#x1F30D;"
        "КРИМИНАЛ" -> "&#x1F575;&#xFE0F;"
        else -> "&#x1F4FA;"
    }
}