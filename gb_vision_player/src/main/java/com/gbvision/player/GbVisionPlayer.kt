package com.gbvision.player

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
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

    var url = ""
    var titleOn = ""
    var titleOff = ""
    var backgroundColor: Int? = null
    var textColor: Int? = null
    private var isPlaying = true

    init {
        if(!isInEditMode){
            configureNotification()
        }
        initView()
        getAttrs(attrs)
        build()
        initData()
    }

    private fun initView() {

        if (!isInEditMode) {
            binding = GbVisionPlayerMainBinding.inflate(LayoutInflater.from(context), this, true)
        } else {
            bindingPreview =
                GbVisionPlayerControllerPreviewBinding.inflate(
                    LayoutInflater.from(context),
                    this,
                    true
                )
        }

    }

    private fun build() {
        if (!isInEditMode) {
            configurePlayer()
        }

    }


    fun getAttrs(attrs: AttributeSet?) {
        val attrsArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.GbVisionPlayer, 0, 0
        )
        try {
            titleOff = attrsArray.getString(R.styleable.GbVisionPlayer_gbTitleOff) ?: context.getString(R.string.play_audio_commentary)
            titleOn = attrsArray.getString(R.styleable.GbVisionPlayer_gbTitleOn) ?: context.getString(R.string.stop_audio_commentary)
            url = attrsArray.getString(R.styleable.GbVisionPlayer_url) ?:context.getString(R.string.media_url)
            backgroundColor = attrsArray.getColor(R.styleable.GbVisionPlayer_gbBackgroundColor, ContextCompat.getColor(context,R.color.color_main))
            textColor = attrsArray.getColor(R.styleable.GbVisionPlayer_gbTextColor, ContextCompat.getColor(context,R.color.color_white))
        } finally {
            attrsArray.recycle()
        }
    }

    private fun initData() {
        if(isInEditMode){
            bindingPreview.txtMessage.text =  if (isPlaying) titleOn else titleOff
            bindingPreview.llBg.background.setTint(backgroundColor!!)
            bindingPreview.txtMessage.setTextColor(textColor!!)
        }else{
            val textView = binding.videoView.findViewById<TextView>(R.id.txtMessage)
            textView.text = if (isPlaying) {
                titleOff
            } else {
                titleOn
            }
            binding.videoView.findViewById<LinearLayout>(R.id.llBg).background.setTint(backgroundColor!!)
            binding.videoView.findViewById<LinearLayout>(R.id.llBg).setOnClickListener {
                player.playWhenReady = !player.isPlaying
            }
            textView.setTextColor(textColor!!)
        }
    }

    private fun configurePlayer() {
        val mediaSource = buildMediaSource(Uri.parse(url))
        player = ExoPlayer.Builder(context).build()
        player.setMediaSource(mediaSource)
        player.prepare()

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                val textView = binding.videoView.findViewById<TextView>(R.id.txtMessage)
                textView.text = if (isPlaying) {
                    titleOff

                } else {
                    titleOn
                }

                if(isPlaying){
                    showNotification()
                }else{
                    hideNotification()
                }
            }
        })

        binding.videoView.controllerHideOnTouch = false
        binding.videoView.controllerAutoShow = true
        binding.videoView.player = player

    }

    fun onResume() {
        val isPause = (context as AppCompatActivity).intent.getBooleanExtra("isPause",false)
        if(isPause){
            hideNotification()
        }
    }



    fun onDestroy() {
        player.release()
        hideNotification()
    }

    private fun buildMediaSource(uri: Uri): MediaSource {

        val mediaSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()

        val mediaItem: MediaItem = MediaItem.Builder()
            .setUri(uri)
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .build()

        return HlsMediaSource.Factory(mediaSourceFactory).createMediaSource(mediaItem)
    }

    private fun hideNotification(){
        val notificationManager  =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(2)
    }

    var notificationBuilder: NotificationCompat.Builder?=null

    @SuppressLint("UnspecifiedImmutableFlag", "LaunchActivityFromNotification")
    private fun configureNotification(){

        val currentTime = System.currentTimeMillis().toInt()
        val reorderIntent = Intent("broadCastPlayerStop")
        reorderIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        val pendingIntent2 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(context,currentTime+2,reorderIntent,PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getBroadcast(context,currentTime+2,reorderIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        }


        val reorderIntent2 = (context as AppCompatActivity).intent
        reorderIntent2.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                context, currentTime+1 , reorderIntent2,
                PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(
                context, currentTime+1 , reorderIntent2,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val channelId = context.getString(R.string.gb_vision_notification_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
         notificationBuilder = NotificationCompat.Builder(
            context,
            channelId
        )
            .setSmallIcon(R.drawable.main_shape)
            .setContentText(context.getString(R.string.notification_text))
            .setAutoCancel(false)
            .setOngoing(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .addAction(NotificationCompat.Action(0,context.getString(R.string.turn_off),pendingIntent2))
        val notificationManager  =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                context.getString(R.string.gb_vision_notification_channel),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val filter = IntentFilter()
        filter.addAction("broadCastPlayerOpen")
        filter.addAction("broadCastPlayerStop")

        val broadCast = object : BroadcastReceiver(){
            override fun onReceive(p0: Context, p1: Intent) {
                player.playWhenReady = false
            }
        }
        context.registerReceiver(broadCast,filter)

    }

    private fun showNotification(){
        val notificationManager  =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(
            2,
            notificationBuilder?.build()
        )
    }



}