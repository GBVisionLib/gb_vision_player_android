package com.gbvision.player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    lateinit var  gbVisionPlayer : GbVisionPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gbVisionPlayer = findViewById(R.id.player)

        gbVisionPlayer.titleOn = resources.getString(R.string.stop_audio_commentary)
        gbVisionPlayer.titleOff = resources.getString(R.string.play_audio_commentary)
        gbVisionPlayer.backgroundColor = ContextCompat.getColor(this, R.color.main)
        gbVisionPlayer.textColor = ContextCompat.getColor(this,R.color.white)
        gbVisionPlayer.url = resources.getString(R.string.url)
    }

    override fun onResume() {
        super.onResume()
        gbVisionPlayer.onResume()
    }


    override fun onDestroy() {
        super.onDestroy()
        gbVisionPlayer.onDestroy()
    }
}