package com.example.musicplayer.data.model

import android.graphics.Bitmap
import android.net.Uri


data class Audio(
    private val uri: Uri,
    private val title: String,
    private val artist:String,
    private val duration: String,
    val album: String,
    private val data: String,
    private val displayName: String,
    private val coverArt: Bitmap
    )
