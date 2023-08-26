package com.example.musicplayer.media.exoplayer

import android.media.session.PlaybackState
import android.os.SystemClock
import android.support.v4.media.session.PlaybackStateCompat

inline val PlaybackStateCompat.isPlaying: Boolean
    get() = state == PlaybackState.STATE_PLAYING || state == PlaybackState.STATE_BUFFERING

inline val PlaybackStateCompat.currentPosition: Long
    get() = if (state == PlaybackStateCompat.STATE_PLAYING) {
        val timeDelta = SystemClock.elapsedRealtime() - lastPositionUpdateTime
        (position + (timeDelta * playbackSpeed)).toLong()
    } else {
        position
    }

