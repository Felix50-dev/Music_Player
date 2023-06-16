package com.example.musicplayer.data.model

import android.graphics.Bitmap
import android.net.Uri


data class Audio(
    val id: Long,
    val uri: Uri,
    val title: String,
    val artist: String,
    val duration: String,
    val album: String,
    val data: String,
    val displayName: String,
    val coverArt: Bitmap
)
