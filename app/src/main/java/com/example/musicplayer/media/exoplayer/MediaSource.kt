package com.example.musicplayer.media.exoplayer

import android.media.MediaDescription
import android.media.browse.MediaBrowser
import android.media.browse.MediaBrowser.MediaItem.FLAG_PLAYABLE
import android.os.Build
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.media3.common.MediaItem
import androidx.annotation.RequiresApi
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.source.ConcatenatingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.example.musicplayer.data.repositories.LocalDataSourceRepository
import javax.inject.Inject

class MediaSource
@Inject constructor(private val repository: LocalDataSourceRepository) {

    private val onReadyListeners: MutableList<OnReadyListener> = mutableListOf()

    var audioMediaMetadata: List<MediaMetadataCompat> = emptyList()

    private var state: AudioSourceState = AudioSourceState.STATE_CREATED
        set(value) {

            if (value == AudioSourceState.STATE_CREATED || value == AudioSourceState.STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach { listener: OnReadyListener ->
                        listener.invoke(isReady)
                    }
                }
            } else {
                field = state
            }
        }

    private val isReady: Boolean
        get() = state == AudioSourceState.STATE_INITIALIZED

    fun whenReady(listener: OnReadyListener): Boolean {
        return if (
            state == AudioSourceState.STATE_CREATED ||
            state == AudioSourceState.STATE_INITIALIZING
        ) {
            onReadyListeners += listener
            false
        } else {
            listener.invoke(isReady)
            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun load() {
        state = AudioSourceState.STATE_INITIALIZING
        val data = repository.getSongs()
        audioMediaMetadata = data.map { song ->
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.id.toString())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.album)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.artist)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.duration.toLong())
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, song.uri.toString())
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, song.displayName)
                .putBitmap(MediaMetadataCompat.METADATA_KEY_MEDIA_URI,song.coverArt)
                .build()
        }
        state = AudioSourceState.STATE_INITIALIZED
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun asMediaSource(dataSource: CacheDataSource.Factory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        audioMediaMetadata.forEach { mediaMetadataCompat ->
            val mediaItem = MediaItem.fromUri(mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI))
            val mediaSource = ProgressiveMediaSource.Factory(dataSource).createMediaSource(mediaItem)

            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    fun asMediaItem() = audioMediaMetadata.map { mediaMetadata ->
        val description = MediaDescriptionCompat.Builder()
            .setMediaUri(mediaMetadata.description.mediaUri)
            .setTitle(mediaMetadata.description.title)
            .setSubtitle(mediaMetadata.description.subtitle)
            .setMediaId(mediaMetadata.description.mediaId)
            .setIconBitmap(mediaMetadata.description.iconBitmap)
            .build()

        MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }.toMutableList()

}

enum class AudioSourceState {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}

typealias OnReadyListener = (Boolean) -> Unit