package com.example.musicplayer.ui

import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.constants.K
import com.example.musicplayer.data.model.Audio
import com.example.musicplayer.data.repositories.LocalDataSourceRepository
import com.example.musicplayer.media.MediaPlaybackService
import com.example.musicplayer.media.MediaPlayerServiceConnection
import com.example.musicplayer.media.exoplayer.currentPosition
import com.example.musicplayer.media.exoplayer.isPlaying
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.Q)
class AudioViewModel @Inject constructor(
    private val repository: LocalDataSourceRepository,
    serviceConnection: MediaPlayerServiceConnection
) : ViewModel() {

    var audioList = mutableListOf<Audio>()
    val currentlyPlayingAudio = serviceConnection.currentlyPlayingAudio
    private val isConnected = serviceConnection.isConnected
    lateinit var rootMediaId: String
    var currentPlaybackPosition by mutableStateOf(0L)
    private var updatePosition = true
    private val playbackState = serviceConnection.playbackState
    val isAudioPlaying: Boolean
        get() = playbackState.value?.isPlaying == true
    val currentDuration = MediaPlaybackService.currentDuration
    var currentAudioProgress = mutableStateOf(0f)


    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {

        override fun onChildrenLoaded(
            parentId: String,
            children: MutableList<MediaBrowserCompat.MediaItem>,
            options: Bundle
        ) {
            super.onChildrenLoaded(parentId, children, options)
        }

    }
    private val serviceConnection = serviceConnection.also {

    }

    init {
        viewModelScope.launch {
            audioList += getAndFormatAudioData()
            isConnected.collect {
                if (it) {
                    rootMediaId = serviceConnection.mediaRootId
                    serviceConnection.playbackState.value?.apply {
                        currentPlaybackPosition = position
                    }
                    serviceConnection.subscribe(rootMediaId, subscriptionCallback)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAndFormatAudioData(): List<Audio> {
        return repository.getSongs().map {
            val displayName = it.displayName.substringBefore(".")
            val artist = if (it.artist.contains("<unknown>"))
                "Unknown Artist" else it.artist
            it.copy(
                displayName = displayName,
                artist = artist
            )
        }
    }

    fun playAudio(currentAudio: Audio) {
        serviceConnection.playAudio(audioList)
        if (currentAudio.id == currentlyPlayingAudio.value?.id) {
            if (isAudioPlaying) serviceConnection.transportControls.pause()
            else serviceConnection.transportControls.play()
        } else {
            serviceConnection.transportControls.playFromMediaId(
                currentAudio.id.toString(),
                null
            )
        }
    }

    fun stopPlayback() {
        serviceConnection.transportControls.stop()
    }

    fun fastForward() {
        serviceConnection.fastForward()
    }

    fun rewind() {
        serviceConnection.rewind()
    }

    fun skipToNext() {
        serviceConnection.skipToNext()
    }

    fun skipToPrev() {
        serviceConnection.skipToPrev()
    }

    fun seekTo(value: Float) {
        serviceConnection.transportControls.seekTo((currentDuration * value / 100f).toLong())
    }

    private fun updatePlayback() {
        viewModelScope.launch {
            val position = playbackState.value?.currentPosition ?: 0
            if (currentPlaybackPosition != position) currentPlaybackPosition = position

            if (currentDuration > 0) {
                currentAudioProgress.value = (currentPlaybackPosition.toFloat() / currentDuration * 100f)
            }
            delay(K.PLAYBACK_UPDATE_INTERVAL)
            if (updatePosition) updatePlayback()
        }
    }

    override fun onCleared() {
        super.onCleared()
        serviceConnection.unsubscribe(K.MY_MEDIA_ROOT_ID, object : MediaBrowserCompat.SubscriptionCallback(){})
        updatePosition = false
    }

}