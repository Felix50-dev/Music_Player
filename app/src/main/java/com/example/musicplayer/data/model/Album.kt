package com.example.musicplayer.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.musicplayer.R

data class Album(
    @DrawableRes val songIcon: Int,
    @StringRes val name: Int,
    @StringRes val artist: Int

)

val albums = listOf(
    Album(R.drawable.ic_music_note, R.string.song_name, R.string.song_artist),
    Album(R.drawable.ic_music_note, R.string.song_name, R.string.song_artist),
    Album(R.drawable.ic_music_note, R.string.song_name, R.string.song_artist),
    Album(R.drawable.ic_music_note, R.string.song_name, R.string.song_artist),
    Album(R.drawable.ic_music_note, R.string.song_name, R.string.song_artist),
    Album(R.drawable.ic_music_note, R.string.song_name, R.string.song_artist),
    Album(R.drawable.ic_music_note, R.string.song_name, R.string.song_artist)
)
