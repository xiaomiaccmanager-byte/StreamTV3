package com.streamtv
import java.util.*
object Scheduler {
    fun build(ch: Channel, offsetHours: Int = 8): List<EpgItem> {
        val result = mutableListOf<EpgItem>()
        val all = ch.shows.flatMap { s -> s.episodes.map { Pair(s, it) } }
        if (all.isEmpty()) return result
        val cal = Calendar.getInstance().apply {
            add(Calendar.HOUR_OF_DAY, -offsetHours)
            set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        val endCal = Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, 24 - offsetHours) }
        var i = 0
        while (cal.before(endCal)) {
            val (show, ep) = all[i % all.size]
            val s = cal.time
            cal.add(Calendar.MINUTE, ep.durationMin)
            result.add(EpgItem(show, ep, s, cal.time, ch.id))
            i++
        }
        return result
    }
    fun current(ch: Channel) = build(ch).firstOrNull { it.isNow }
    fun upcoming(ch: Channel, n: Int = 12) = build(ch).filter { !it.isPast || it.isNow }.take(n)
    fun fmt(d: Date): String {
        val c = Calendar.getInstance().apply { time = d }
        return "%02d:%02d".format(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))
    }
}