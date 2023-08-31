package com.example.musicplayer.ui.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.R
import com.example.musicplayer.data.model.Song
import com.example.musicplayer.data.model.songs


@Composable
fun SongsScreen() {
    LazyColumn(
        modifier = Modifier
            .padding(8.dp)
            .background(MaterialTheme.colors.background)
    ) {
        items(songs) {
            SongItem(song = it)
        }
    }
}

@Composable
fun SongItem(song: Song, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(8.dp),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SongIcon(song.songIcon)
            SongInformation(song.name, song.artist)
            Spacer(modifier = Modifier.weight(1f))
            MoreIcon()
        }
    }
}

@Composable
fun SongIcon(@DrawableRes songIcon: Int, modifier: Modifier = Modifier) {

        Image(
            modifier = modifier
                .size(64.dp)
                .clip(RoundedCornerShape(50))
                .padding(8.dp),
            contentScale = ContentScale.Crop,
            painter = painterResource(id = songIcon),
            /*
         * Content Description is not needed here - image is decorative, and setting a null content
         * description allows accessibility services to skip this element during navigation.
         */
            contentDescription = null
        )
}

@Composable
fun SongInformation(
    @StringRes songName: Int,
    @StringRes songArtist: Int,
    modifier: Modifier = Modifier,
) {
    Column {
        Text(
            text = stringResource(id = songName),
            style = MaterialTheme.typography.h2,
            fontSize = 24.sp,
            modifier = modifier.padding(top = 10.dp)
        )
        Text(
            text = stringResource(id = songArtist),
            style = MaterialTheme.typography.h2,
            fontSize = 24.sp,
            modifier = modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun MoreIcon() {
    Icon(
        imageVector = Icons.Filled.MoreVert,
        contentDescription = stringResource(id = R.string.more_content_description),
        tint = MaterialTheme.colors.secondary
    )
}

@Composable
@Preview
fun SongsScreenPreview() {
    SongsScreen()
}

@Composable
@Preview
fun SongItemPreview() {
    SongItem(song = Song(R.drawable.ic_music_note, R.string.song_name, R.string.song_artist))
}