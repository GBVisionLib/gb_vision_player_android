package com.gbvision.player

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.gbvision.player.databinding.GbVisionPlayerControllerBinding
import com.gbvision.player.databinding.GbVisionPlayerControllerPreviewBinding
import com.gbvision.player.databinding.GbVisionPlayerMainBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.MimeTypes


class GbVisionPlayer(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private lateinit var binding: GbVisionPlayerMainBinding
    private lateinit var bindingPreview: GbVisionPlayerControllerPreviewBinding
    private lateinit var player: ExoPlayer

    init {
        initView()
    }

    private fun initView() {

        if (!isInEditMode) {
            binding = GbVisionPlayerMainBinding.inflate(LayoutInflater.from(context), this, true)
            build()
        } else {
            bindingPreview =
                GbVisionPlayerControllerPreviewBinding.inflate(LayoutInflater.from(context), this, true)
        }
    }

    private fun build() {
        configurePlayer()
    }

    private fun configurePlayer() {
        val mediaSource = buildMediaSource(Uri.parse(context.getString(R.string.media_url)))
        player = ExoPlayer.Builder(context).build()
        player.setMediaSource(mediaSource)
        player.prepare()

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                val textView = binding.videoView.findViewById<TextView>(R.id.txtMessage)
                textView.text = if (isPlaying) {
                    context.getString(R.string.stop_audio_commentary)
                } else {
                    context.getString(R.string.play_audio_commentary)
                }
            }
        })

        binding.videoView.controllerHideOnTouch = false
        binding.videoView.controllerAutoShow = true
        binding.videoView.player = player
    }

    fun onResume() {
        player.playWhenReady = true
    }

    fun onPause() {
        player.playWhenReady = false
    }

    fun onDestroy() {
        player.release()
    }

    private fun buildMediaSource(uri: Uri): MediaSource {

        val mediaSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()

        val mediaItem: MediaItem = MediaItem.Builder()
            .setUri(uri)
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .build()

        return HlsMediaSource.Factory(mediaSourceFactory).createMediaSource(mediaItem)
    }


}