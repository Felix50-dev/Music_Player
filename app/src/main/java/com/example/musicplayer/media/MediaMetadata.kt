package com.example.musicplayer.media

import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import androidx.annotation.RequiresApi
import com.example.musicplayer.data.repositories.LocalDataSourceRepository

@RequiresApi(Build.VERSION_CODES.Q)
suspend fun getMediaMetadata(localDataSourceRepository: LocalDataSourceRepository): MediaMetadataCompat {

    lateinit var mediaMetadata: MediaMetadataCompat

    localDataSourceRepository.getSongs().map { song ->

        mediaMetadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.id.toString())
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.album)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.artist)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.duration.toLong())
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, song.uri.toString())
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, song.displayName)
            .putBitmap(MediaMetadataCompat.METADATA_KEY_MEDIA_URI,song.coverArt).build()

    }
    return mediaMetadata

}