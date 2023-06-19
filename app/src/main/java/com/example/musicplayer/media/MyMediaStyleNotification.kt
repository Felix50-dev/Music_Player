package com.example.musicplayer.media

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.content.ContextCompat
import androidx.media.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.example.musicplayer.R
import androidx.core.app.NotificationCompat as NotificationCompat1

private const val channelId = "CHANNEL_ID"

class MyMediaStyleNotification(mediaSession: MediaSessionCompat?, context: Context) {

    private val controller = mediaSession?.controller
    private val mediaMetadata = controller?.metadata
    private val description = mediaMetadata?.description



    val builder = NotificationCompat1.Builder(context, channelId).apply {
        // Add the metadata for the currently playing track
        setContentTitle(description?.title)
        setContentText(description?.subtitle)
        setSubText(description?.description)
        setLargeIcon(description?.iconBitmap)

        // Enable launching the player by clicking the notification
        setContentIntent(controller?.sessionActivity)

        // Stop the service when the notification is swiped away
        setDeleteIntent(
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                context,
                PlaybackStateCompat.ACTION_STOP
            )
        )

        // Make the transport controls visible on the lockscreen
        setVisibility(NotificationCompat1.VISIBILITY_PUBLIC)

        // Add an app icon and set its accent color
        // Be careful about the color
        setSmallIcon(R.drawable.ic_player_notification)
        color = ContextCompat.getColor(context, R.color.purple_500)

        // Add a pause button
        addAction(
            NotificationCompat1.Action(
                R.drawable.ic_pause,
                context.getString(R.string.pause),
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context,
                    PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
            )
        )

        // Take advantage of MediaStyle features
        if (mediaSession != null) {
            setStyle(NotificationCompat.MediaStyle()
                .setMediaSession(mediaSession.sessionToken)
                .setShowActionsInCompactView(0)

                // Add a cancel button
                .setShowCancelButton(true)
                .setCancelButtonIntent(
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context,
                        PlaybackStateCompat.ACTION_STOP
                    )
                )
            )
        }
    }
}