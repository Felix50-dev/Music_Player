package com.example.musicplayer.media

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media3.common.Player
import androidx.media3.ui.PlayerNotificationManager
import androidx.media3.ui.PlayerNotificationManager.NotificationListener
import com.example.musicplayer.R

private const val channelId = "CHANNEL_ID"

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)

internal class MyMediaStyleNotification(sessionToken: MediaSessionCompat.Token, notificationListener: NotificationListener, context: Context) {

    private val notificationManager: PlayerNotificationManager

    init {
        val mediaController = MediaControllerCompat(context, sessionToken)
        val builder = PlayerNotificationManager.Builder(
            context,
            notificationId,
            channelId
        )

        with(builder) {
            setMediaDescriptionAdapter(DescriptionAdapter(mediaController))
            setNotificationListener(notificationListener)
            setChannelNameResourceId(R.string.notification_channel)
            setChannelDescriptionResourceId(R.string.notification_channel_description)
        }

        notificationManager = builder.build()

        with(notificationManager) {
            setMediaSessionToken(sessionToken)
            setSmallIcon(R.drawable.ic_music_note)
            setUseFastForwardAction(true)
            setUseRewindAction(true)
        }

    }

    private fun showNotification(player: Player) {
        notificationManager.setPlayer(player)
    }



    inner class DescriptionAdapter(private val controller: MediaControllerCompat): PlayerNotificationManager.MediaDescriptionAdapter {
        override fun getCurrentContentTitle(player: Player): CharSequence = controller.metadata.description.title.toString()
        override fun createCurrentContentIntent(player: Player): PendingIntent? = controller.sessionActivity

        override fun getCurrentContentText(player: Player): CharSequence? = controller.metadata.description.subtitle

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? = controller.metadata.description.iconBitmap

    }

}