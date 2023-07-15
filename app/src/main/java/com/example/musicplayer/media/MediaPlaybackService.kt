package com.example.musicplayer.media

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerNotificationManager
import com.example.musicplayer.data.repositories.LocalDataSourceRepository
import com.example.musicplayer.media.exoplayer.MediaSource
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    private lateinit var mediaPlayerNotification: MyMediaStyleNotification

    private var currentlyPlayingMedia: MediaMetadataCompat? = null
    private var isPlayerInitialized = false
    var isForegroundService: Boolean = false

    companion object {
        private const val TAG = "MediaPlayerService"

        var currentDuration: Long = 0L
            private set

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @UnstableApi
    override fun onCreate() {
        super.onCreate()
        //not sure how this is helpful
        val sessionActivityIntent =
            packageManager?.getLaunchIntentForPackage(packageName)?.let { sessionIntent ->
                    PendingIntent.getActivity(
                        this,
                        0,
                        sessionIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                }

        mediaSession = MediaSessionCompat(this, TAG).apply {
            setSessionActivity(sessionActivityIntent)
            isActive = true
        }
        val sessionToken = mediaSession.sessionToken
        mediaPlayerNotification =
            MyMediaStyleNotification(this,
                sessionToken,
                NotificationListener()
            )

        serviceScope.launch {
            mediaSource.load()
        }
        mediaSessionConnector = MediaSessionConnector(mediaSession).apply {
            setPlaybackPreparer()
            setQueueNavigator()
        }
    }


    override fun onGetRoot(
        clientPackageName: String, clientUid: Int, rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot(MY_MEDIA_ROOT_ID, null)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onLoadChildren(
        parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        val audioFiles = localDataSourceRepository.getSongs()
        val mediaItems = mutableListOf<MediaBrowserCompat.MediaItem>()

        audioFiles.map { song ->

            val description =
                MediaDescriptionCompat.Builder().setTitle(song.title).setMediaUri(song.uri)
                    .setIconUri(song.uri).setSubtitle(song.displayName).build()

            val mediaItem: MediaBrowserCompat.MediaItem = MediaBrowserCompat.MediaItem(
                description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
            )

            mediaItems.add(mediaItem)
        }
        result.sendResult(mediaItems)
    }

    @UnstableApi
    inner class NotificationListener : PlayerNotificationManager.NotificationListener {
        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            stopForeground(true)
            isForegroundService = false
            stopSelf()
        }

        override fun onNotificationPosted(
            notificationId: Int, notification: Notification, ongoing: Boolean
        ) {
            if (ongoing && !isForegroundService) {
                ContextCompat.startForegroundService(
                    applicationContext,
                    Intent(applicationContext, this@MediaPlaybackService.javaClass)
                )
                startForeground(notificationId, notification)
                isForegroundService = true
            }
        }
    }

    inner class AudioMediaPlaybackPreparer: MediaSessionConnector.PlaybackPreparer{
        override fun onCommand(
            player: com.google.android.exoplayer2.Player?,
            command: String?,
            extras: Bundle?,
            cb: ResultReceiver?
        ): Boolean {
            TODO("Not yet implemented")
        }

        override fun getSupportedPrepareActions(): Long {
            TODO("Not yet implemented")
        }

        override fun onPrepare(playWhenReady: Boolean) {
            TODO("Not yet implemented")
        }

        override fun onPrepareFromMediaId(
            mediaId: String?,
            playWhenReady: Boolean,
            extras: Bundle?
        ) {
            TODO("Not yet implemented")
        }

        override fun onPrepareFromSearch(query: String?, playWhenReady: Boolean, extras: Bundle?) {
            TODO("Not yet implemented")
        }

        override fun onPrepareFromUri(uri: Uri?, playWhenReady: Boolean, extras: Bundle?) {
            TODO("Not yet implemented")
        }

    }
}
