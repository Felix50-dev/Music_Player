package com.example.musicplayer.media

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.service.media.MediaBrowserService
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.media.MediaBrowserServiceCompat
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicplayer.data.model.Audio
import com.example.musicplayer.data.repositories.LocalDataSourceRepository
import com.example.musicplayer.media.exoplayer.MediaSource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.media3.common.MediaItem as ExoplayerMediaItem


private const val MY_MEDIA_ROOT_ID = "media_root_id"
private const val TAG = "MediaPlaybackService"
private const val notificationId = 1

@AndroidEntryPoint
class MediaPlaybackService(private val localDataSourceRepository: LocalDataSourceRepository) :
    MediaBrowserServiceCompat() {

    @Inject
    lateinit var dataSourceFactory: CacheDataSource.Factory

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @Inject
    lateinit var mediaSource: MediaSource

    var mediaSession: MediaSessionCompat? = null
    private var player: ExoPlayer? = null
    private lateinit var stateBuilder: PlaybackStateCompat.Builder

    private lateinit var afChangeListener: AudioManager.OnAudioFocusChangeListener
    private lateinit var audioFocusRequest: AudioFocusRequest
    private val intentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)

    private val callback = object: MediaSessionCompat.Callback () {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onPlay() {
            super.onPlay()

            val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
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
                startService(Intent(applicationContext, MediaBrowserService::class.java))
                // Set the session active  (and update metadata and state)
                mediaSession?.isActive  = true
                // start the player (custom call)
                initializeMediaPlayer(audio = localDataSourceRepository.getSongs()[10])
                // Register BECOME_NOISY BroadcastReceiver
                registerReceiver(myNoisyAudioStreamReceiver, intentFilter)
                // Put the service in the foreground, post notification
                startForeground(notificationId, builder)
            }

        }
    }

    override fun onCreate() {
        super.onCreate()

        initializeMediaSession()

    }


    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot(MY_MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        val audioFiles = localDataSourceRepository.getSongs()
        val mediaItems = mutableListOf<MediaBrowserCompat.MediaItem>()

        audioFiles.map { song ->

            val description = MediaDescriptionCompat.Builder()
                .setTitle(song.title)
                .setMediaUri(song.uri)
                .setIconUri(song.uri)
                .setSubtitle(song.displayName)
                .build()

            val mediaItem: MediaBrowserCompat.MediaItem = MediaBrowserCompat.MediaItem(
                description,
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
            )

            mediaItems.add(mediaItem)
        }
        result.sendResult(mediaItems)
    }

    private fun initializeMediaSession() {
        // Create a MediaSessionCompat
        mediaSession = MediaSessionCompat(baseContext, TAG).apply {

            // Enable callbacks from MediaButtons and TransportControls
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                        or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )

            // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
            stateBuilder = PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY
                            or PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
            setPlaybackState(stateBuilder.build())

            // MySessionCallback() has methods that handle callbacks from a media controller
            setCallback(MediaSessionCallback(applicationContext).callback)

            // Set the session's token so that client activities can communicate with it.
            setSessionToken(sessionToken)
        }
    }

    private fun initializeMediaPlayer(audio: Audio) {
        val context = this.applicationContext
        val player = ExoPlayer.Builder(context).build()

        val mediaItem = ExoplayerMediaItem.Builder()
            .setMediaId(audio.id.toString())
            .setUri(audio.uri)
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }
}
