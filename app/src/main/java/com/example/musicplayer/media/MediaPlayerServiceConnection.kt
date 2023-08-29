package com.example.musicplayer.media

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.runtime.mutableStateOf
import com.example.musicplayer.constants.K
import com.example.musicplayer.data.model.Audio
import com.example.musicplayer.media.exoplayer.currentPosition
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class MediaPlayerServiceConnection @Inject constructor(@ApplicationContext context: Context) {

    private val _playBackState: MutableStateFlow<PlaybackStateCompat?> = MutableStateFlow(null)
    val playbackState: StateFlow<PlaybackStateCompat?> get() = _playBackState

    private val _isConnected: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> get() = _isConnected

    val currentlyPlayingAudio = mutableStateOf<Audio?>(null)

    lateinit var mediaControllerCompat: MediaControllerCompat

    private val mediaBrowserServiceCallback = MediaBrowserConnectionCallback(context)

    private val mediaBrowser = MediaBrowserCompat(context, ComponentName(context,MediaPlaybackService::class.java),mediaBrowserServiceCallback,null).apply {
        connect()
    }

    private var audioList = listOf<Audio>()

    val mediaRootId: String
    get() = mediaBrowser.root

    val transportControls: MediaControllerCompat.TransportControls
    get() = mediaControllerCompat.transportControls

    fun playAudio(audios: List<Audio>) {
        audioList = audios
        mediaBrowser.sendCustomAction(K.START_MEDIA_PLAY_ACTION,null,null)
    }

    fun fastForward(seconds:Int = 10) {
        playbackState.value?.currentPosition?.let {
            transportControls.seekTo(it + seconds * 1000 )
        }
    }

    fun rewind(seconds:Int = 10) {
        playbackState.value?.currentPosition?.let {
            transportControls.seekTo(it - seconds * 1000 )
        }
    }

    fun skipToNext() {
        transportControls.skipToNext()
    }

    fun skipToPrev() {
        transportControls.skipToPrevious()
    }

    fun subscribe(
        parentId: String,
        callBack: MediaBrowserCompat.SubscriptionCallback
    ) {
        mediaBrowser.subscribe(parentId,callBack)
    }

    fun unsubscribe(
        parentId: String,
        callBack: MediaBrowserCompat.SubscriptionCallback
    ) {
        mediaBrowser.unsubscribe(parentId,callBack)
    }

    fun refreshMediaBrowserChildren() {
        mediaBrowser.sendCustomAction(K.REFRESH_MEDIA_PLAY_ACTION, null, null)
    }



    private inner class MediaBrowserConnectionCallback(private val context: Context): MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            _isConnected.value = true
            mediaControllerCompat = MediaControllerCompat(
                context,
                mediaBrowser.sessionToken
            ).apply {
                registerCallback(MediaControllerCallBack())
            }
        }

        override fun onConnectionSuspended() {
            _isConnected.value = false
        }

        override fun onConnectionFailed() {
            _isConnected.value = false
        }
    }

    private inner class MediaControllerCallBack: MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            _playBackState.value = state
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            currentlyPlayingAudio.value = metadata?.let { data ->
                audioList.find {
                    it.id.toString() == data.description.mediaId
                }
            }
        }

        override fun onSessionDestroyed() {
            mediaBrowserServiceCallback.onConnectionSuspended()
        }

    }
}