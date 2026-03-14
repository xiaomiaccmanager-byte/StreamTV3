package com.streamtv
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.streamtv.databinding.ActivitySetupBinding
class SetupActivity : AppCompatActivity() {
    private lateinit var b: ActivitySetupBinding
    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        b = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(b.root)
        val prefs = getSharedPreferences("stv", MODE_PRIVATE)
        val saved = prefs.getString("url", null)
        if (saved != null) {
            launch(saved)
            return
        }
        b.btnLostfilm.setOnClickListener { b.inputUrl.setText("https://www.lostfilm.tv/") }
        b.btnRezka.setOnClickListener { b.inputUrl.setText("https://rezka.ag/") }
        b.btnKinogo.setOnClickListener { b.inputUrl.setText("https://kinogo.co/") }
        b.btnLaunch.setOnClickListener {
            val url = b.inputUrl.text.toString().trim()
            if (url.isNotEmpty()) {
                prefs.edit().putString("url", url).apply()
                launch(url)
            }
        }
        b.btnDemo.setOnClickListener { launch("demo") }
    }
    private fun launch(url: String) {
        startActivity(Intent(this, MainActivity::class.java).putExtra("url", url))
    }
}