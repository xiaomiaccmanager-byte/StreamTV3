package com.streamtv
import android.content.Intent
import android.os.Bundle; import android.os.Handler; import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.streamtv.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat; import java.util.*
class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    private lateinit var chAdapter: ChannelAdapter
    private lateinit var epgAdapter: EpgAdapter
    private var channels = DemoData.channels
    private var cur = 0
    private var showEpg = false
    private val handler = Handler(Looper.getMainLooper())
    private val tick = object : Runnable {
        override fun run() {
            b.tvClock.text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            updatePlayer()
            handler.postDelayed(this, 1000)
        }
    }
    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        val url = intent.getStringExtra("url") ?: "demo"
        val host = if (url == "demo") "demo" else try { java.net.URL(url).host } catch (e: Exception) { url }
        b.tvSource.text = host
        b.tvSource.setOnClickListener {
            getSharedPreferences("stv", MODE_PRIVATE).edit().remove("url").apply()
            startActivity(Intent(this, SetupActivity::class.java))
            finish()
        }
        chAdapter = ChannelAdapter { switchCh(it) }
        epgAdapter = EpgAdapter { openPlayer(it) }
        b.rvChannels.layoutManager = LinearLayoutManager(this)
        b.rvChannels.adapter = chAdapter
        b.rvEpg.layoutManager = LinearLayoutManager(this)
        b.rvEpg.adapter = epgAdapter
        b.btnPrev.setOnClickListener { switchCh((cur - 1 + channels.size) % channels.size) }
        b.btnNext.setOnClickListener { switchCh((cur + 1) % channels.size) }
        b.btnWatch.setOnClickListener { Scheduler.current(channels[cur])?.let { openPlayer(it) } }
        b.playerPreview.setOnClickListener { Scheduler.current(channels[cur])?.let { openPlayer(it) } }
        b.tabChannels.setOnClickListener { showTab(false) }
        b.tabEpg.setOnClickListener { showTab(true) }
        if (url == "demo") {
            channels = DemoData.channels
            chAdapter.submit(channels)
            switchCh(0)
        } else {
            b.loading.visibility = View.VISIBLE
            lifecycleScope.launch {
                channels = Parser.parse(url)
                b.loading.visibility = View.GONE
                chAdapter.submit(channels)
                if (channels.isNotEmpty()) switchCh(0)
            }
        }
        handler.post(tick)
    }
    private fun showTab(epg: Boolean) {
        showEpg = epg
        b.rvChannels.visibility = if (epg) View.GONE else View.VISIBLE
        b.rvEpg.visibility = if (epg) View.VISIBLE else View.GONE
        b.tabChannels.setTextColor(if (!epg) 0xFFE8420A.toInt() else 0xFF555555.toInt())
        b.tabChannels.setBackgroundColor(if (!epg) 0xFF0A0A0C.toInt() else 0xFF111116.toInt())
        b.tabEpg.setTextColor(if (epg) 0xFFE8420A.toInt() else 0xFF555555.toInt())
        b.tabEpg.setBackgroundColor(if (epg) 0xFF0A0A0C.toInt() else 0xFF111116.toInt())
    }
    private fun switchCh(i: Int) {
        cur = i
        chAdapter.setActive(i)
        b.rvChannels.scrollToPosition(i)
        updatePlayer()
        val items = Scheduler.upcoming(channels[i])
        epgAdapter.submit(items)
        val nowIdx = items.indexOfFirst { it.isNow }
        if (nowIdx > 0) b.rvEpg.scrollToPosition((nowIdx - 1).coerceAtLeast(0))
    }
    private fun updatePlayer() {
        val ch = channels[cur]
        val item = Scheduler.current(ch)
        b.tvChannel.text = "${ch.emoji} ${ch.name}"
        b.tvTitle.text = item?.show?.title ?: "—"
        b.tvEpisode.text = item?.episode?.title ?: ""
        b.progress.progress = item?.progress ?: 0
    }
    private fun openPlayer(item: EpgItem) {
        startActivity(Intent(this, PlayerActivity::class.java).apply {
            putExtra("ch", channels[cur].name)
            putExtra("show", item.show.title)
            putExtra("url", item.episode.streamUrl)
            putExtra("page", item.episode.pageUrl)
        })
    }
    override fun onResume() { super.onResume(); handler.post(tick) }
    override fun onPause() { super.onPause(); handler.removeCallbacks(tick) }
}