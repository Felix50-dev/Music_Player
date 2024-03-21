package hoods.com.jetaudio.data.local.model

import java.io.Serializable

data class Audio(
    val uri: String,
    val displayName: String,
    val id: Long,
    val albumId: Long,
    val artist: String,
    val data: String,
    val duration: Int,
    val title: String,
): Serializable
