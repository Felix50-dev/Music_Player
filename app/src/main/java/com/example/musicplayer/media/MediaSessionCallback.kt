package com.example.musicplayer.media

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.service.media.MediaBrowserService
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import androidx.media3.exoplayer.ExoPlayer

class MediaSessionCallback(context: Context) {

    // Defined elsewhere...
    private lateinit var afChangeListener: AudioManager.OnAudioFocusChangeListener
    private val myNoisyAudioStreamReceiver = BecomingNoisyReceiver()
    private lateinit var myPlayerNotification: MyMediaStyleNotification
    private lateinit var service: MediaPlaybackService
    private lateinit var player: ExoPlayer

    private lateinit var audioFocusRequest: AudioFocusRequest

    val callback = object: MediaSessionCompat.Callback() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onPlay() {
            val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            // Request audio focus for playback, this registers the afChangeListener

            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
                setOnAudioFocusChangeListener(afChangeListener)
                setAudioAttributes(AudioAttributes.Builder().run {
                    setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    build()
                })
                build()
            }
            val result = am.requestAudioFocus(audioFocusRequest)
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                // Start the service
                context.startService(Intent(context, MediaBrowserService::class.java))
                // Set the session active  (and update metadata and state)
                mediaSession.isActive = true
                // start the player (custom call)
                player.start()
                // Register BECOME_NOISY BroadcastReceiver
                context.registerReceiver(myNoisyAudioStreamReceiver, intentFilter)
                // Put the service in the foreground, post notification
                service.startForeground(id, myPlayerNotification)
            }
        }

        public override fun onStop() {
            val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            // Abandon audio focus
            am.abandonAudioFocusRequest(audioFocusRequest)
            unregisterReceiver(myNoisyAudioStreamReceiver)
            // Stop the service
            service.stopSelf()
            // Set the session inactive  (and update metadata and state)
            mediaSession.isActive = false
            // stop the player (custom call)
            player.stop()
            // Take the service out of the foreground
            service.stopForeground(false)
        }

        public override fun onPause() {
            val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            // Update metadata and state
            // pause the player (custom call)
            player.pause()
            // unregister BECOME_NOISY BroadcastReceiver
            context.unregisterReceiver(myNoisyAudioStreamReceiver)
            // Take the service out of the foreground, retain the notification
            service.stopForeground(false)
        }
    }

}