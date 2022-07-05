package com.gbvision.player

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    lateinit var  gbVisionPlayer : GbVisionPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gbVisionPlayer = findViewById(R.id.player)
    }

    override fun onResume() {
        super.onResume()
        gbVisionPlayer.onResume()
    }

    override fun onPause() {
        super.onPause()
        gbVisionPlayer.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        gbVisionPlayer.onDestroy()
    }
}