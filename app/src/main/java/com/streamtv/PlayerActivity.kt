package com.streamtv

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.streamtv.databinding.ActivityPlayerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.regex.Pattern

class PlayerActivity : AppCompatActivity() {
    private lateinit var b: ActivityPlayerBinding
    private var player: ExoPlayer? = null
    private var paused = false
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        b = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(b.root)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.systemBars())
        }
        val ch = intent.getStringExtra("ch") ?: ""
        val show = intent.getStringExtra("show") ?: ""
        b.tvCh.text = "$ch — $show"
        b.btnBack.setOnClickListener { finish() }
        b.btnPp.setOnClickListener {
            paused = !paused
            if (paused) player?.pause() else player?.play()
            b.btnPp.text = if (paused) ">" else "||"
        }
        b.btnRw.setOnClickListener { player?.seekBack() }
        b.btnFf.setOnClickListener { player?.seekForward() }
        b.playerContainer.setOnClickListener {
            b.controls.visibility = if (b.controls.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
        }
        player = ExoPlayer.Builder(this).build()
        b.playerView.player = player
        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                b.loading.visibility = if (state == Player.STATE_BUFFERING) View.VISIBLE else View.GONE
                if (state == Player.STATE_ENDED) finish()
            }
        })
        val url = intent.getStringExtra("url") ?: ""
        val page = intent.getStringExtra("page") ?: ""
        when {
            url.isNotEmpty() -> playUrl(url)
            page.isNotEmpty() -> extractAndPlay(page)
            else -> showError("Нет ссылки на видео")
        }
        startProgressLoop()
    }

    private fun playUrl(url: String) {
        player?.setMediaItem(MediaItem.fromUri(url))
        player?.prepare()
        player?.play()
    }

    private fun extractAndPlay(pageUrl: String) {
        lifecycleScope.launch {
            val found = withContext(Dispatchers.IO) {
                try {
                    val html = OkHttpClient().newCall(
                        Request.Builder().url(pageUrl).header("User-Agent", "Mozilla/5.0").build()
                    ).execute().use { it.body?.string() ?: "" }
                    val p1 = Pattern.compile("\"(https?://[^\"]+\\.m3u8[^\"]*)\"")
                    val p2 = Pattern.compile("\"(https?://[^\"]+\\.mp4[^\"]*)\"")
                    var result: String? = null
                    val m1 = p1.matcher(html)
                    if (m1.find()) result = m1.group(1)
                    if (result == null) {
                        val m2 = p2.matcher(html)
                        if (m2.find()) result = m2.group(1)
                    }
                    result
                } catch (e: Exception) { null }
            }
            if (found != null) playUrl(found) else showError("Не удалось извлечь видео.\n$pageUrl")
        }
    }

    private fun showError(msg: String) {
        b.loading.visibility = View.GONE
        b.tvError.visibility = View.VISIBLE
        b.tvError.text = msg
    }

    private fun startProgressLoop() {
        handler.post(object : Runnable {
            override fun run() {
                player?.let { p ->
                    if (p.duration > 0) {
                        b.tvTime.text = "${fmtMs(p.currentPosition)} / ${fmtMs(p.duration)}"
                    }
                }
                handler.postDelayed(this, 500)
            }
        })
    }

    private fun fmtMs(ms: Long): String {
        val s = ms / 1000
        return "%02d:%02d:%02d".format(s / 3600, s % 3600 / 60, s % 60)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        player?.release()
    }
}
