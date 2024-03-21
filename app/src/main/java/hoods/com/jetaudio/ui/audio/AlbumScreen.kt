package hoods.com.jetaudio.ui.audio

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

private const val TAG = "AlbumScreen"
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AlbumScreen(
    context: Context,
    albumViewModel: AlbumViewModel = hiltViewModel(),
    audioViewModel: AudioViewModel,
    onClick: () -> Unit,
    navigateUp: () -> Unit,
    startService: () -> Unit
) {
    Scaffold(
        topBar = {
                 MusicPlayerTopAppBar(
                     title = "Album",
                     canNavigateBack = true,
                     navigateUp = navigateUp
                 )
        },
        bottomBar = {
            BottomBarPlayer(
                progress = audioViewModel.progress,
                onProgress = { audioViewModel.onUiEvents(UIEvents.SeekTo(it)) },
                isAudioPlaying = audioViewModel.isPlaying,
                audio = audioViewModel.currentSelectedAudio,
                onStart = { audioViewModel.onUiEvents(UIEvents.PlayPause) },
                onNext = {audioViewModel.onUiEvents(UIEvents.SeekToNext)}
            ) {
                onClick()
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            itemsIndexed(albumViewModel.currentAlbum) { index, audio ->
                AudioItem(
                    context = context,
                    audio = audio,
                    onItemClick = {
                        Log.d(TAG, "AlbumScreen: current album songs: ${albumViewModel.currentAlbum}")
                        val songs = albumViewModel.currentAlbum
                        audioViewModel.setMediaItems(songs)
                        audioViewModel.onUiEvents(UIEvents.SelectedAudioChange(index))
                        startService()
                    }
                )
            }
        }
    }
}