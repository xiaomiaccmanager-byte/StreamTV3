package com.streamtv
import java.util.Date
data class Channel(val id: Int, val name: String, val emoji: String, val shows: List<Show>)
data class Show(val title: String, val episodes: List<Episode>)
data class Episode(val title: String, val streamUrl: String = "", val pageUrl: String = "", val durationMin: Int = 45)
data class EpgItem(val show: Show, val episode: Episode, val start: Date, val end: Date, val channelId: Int) {
    val isNow: Boolean get() { val n = Date(); return n.after(start) && n.before(end) }
    val isPast: Boolean get() = Date().after(end)
    val progress: Int get() {
        if (!isNow) return if (isPast) 100 else 0
        return ((Date().time - start.time) * 100 / (end.time - start.time)).toInt().coerceIn(0, 100)
    }
    val minLeft: Int get() = ((end.time - Date().time) / 60000).toInt().coerceAtLeast(0)
}