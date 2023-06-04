package com.example.musicplayer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicplayer.data.model.Album
import com.example.musicplayer.data.model.albums

@Composable
fun AlbumsScreen() {
    LazyColumn(
        modifier = Modifier
            .padding(8.dp)
            .background(MaterialTheme.colors.background)
    ) {
        items(albums) {
            AlbumItem(album = it)
        }
    }
}

@Composable
fun AlbumItem(album: Album, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(8.dp),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            SongIcon(album.songIcon)
            SongInformation(album.name, album.artist)
            Spacer(modifier = Modifier.weight(1f))
            MoreIcon()
        }
    }
}

@Preview
@Composable
fun AlbumsScreenPreview() {
    AlbumsScreen()
}