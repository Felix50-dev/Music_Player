package com.example.musicplayer.media

import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import androidx.annotation.RequiresApi
import androidx.media.MediaBrowserServiceCompat
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.example.musicplayer.data.repositories.LocalDataSourceRepository
import com.example.musicplayer.media.exoplayer.MediaSource
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject


private const val MY_MEDIA_ROOT_ID = "media_root_id"
private const val TAG = "MediaPlaybackService"
const val notificationId = 1

@AndroidEntryPoint
class MediaPlaybackService @Inject constructor(private val localDataSourceRepository: LocalDataSourceRepository) :
    MediaBrowserServiceCompat() {

    @Inject
    lateinit var dataSourceFactory: CacheDataSource.Factory

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @Inject
    lateinit var mediaSource: MediaSource

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var mediaSession: MediaSession
    private lateinit var mediaSessionConnector: MediaSessionConnector


    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot(MY_MEDIA_ROOT_ID, null)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
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
}
