package com.example.musicplayer.media

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicplayer.data.model.Audio
import com.example.musicplayer.data.repositories.LocalDataSourceRepository
import androidx.media3.common.MediaItem as ExoplayerMediaItem


private const val MY_MEDIA_ROOT_ID = "media_root_id"
private const val TAG = "MediaPlaybackService"

class MediaPlaybackService(private val localDataSourceRepository: LocalDataSourceRepository) :
    MediaBrowserServiceCompat() {

    private var mediaSession: MediaSessionCompat? = null
    private lateinit var stateBuilder: PlaybackStateCompat.Builder

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
            //setCallback(MySessionCallback())

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
