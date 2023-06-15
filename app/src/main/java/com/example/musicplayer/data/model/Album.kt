package com.example.musicplayer.data.model

import android.net.Uri

data class Album(
    val id: Long,
    val name: String,
    val artist: String,
    val numOfSongs: Int,
    val uri: Uri
)


