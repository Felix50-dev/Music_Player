package hoods.com.jetaudio.data.local.model

import java.io.Serializable

data class Album(
    val id: Long,
    val name: String,
    val artist: String,
    val numOfSongs: Int? = 16,
    var songs: MutableList<Audio> = emptyList<Audio>().toMutableList(),
    val uri: String
): Serializable
