package hoods.com.jetaudio.ui.audio

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import dagger.hilt.android.lifecycle.HiltViewModel
import hoods.com.jetaudio.data.local.model.Audio
import hoods.com.jetaudio.data.repository.AudioRepository
import hoods.com.jetaudio.player.service.JetAudioServiceHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AlbumViewModel"

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(SavedStateHandleSaveableApi::class)
@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val repository: AudioRepository,
    private val audioServiceHandler: JetAudioServiceHandler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var currentAlbum by savedStateHandle.saveable { mutableStateOf(listOf<Audio>()) }
    private val albumId: Long = checkNotNull(savedStateHandle["id"])

    init {
        getAlbum(albumId)
    }

    private fun getAlbum(albumId: Long): MutableList<Audio> {
        val album = mutableListOf<Audio>()
        viewModelScope.launch {
            val songsList = repository.getAudioData()
            for (audio in songsList) {
                if (audio.albumId == albumId) album += audio
            }
            Log.d(TAG, "getAlbum: songs are : $album")
            currentAlbum = album
        }
        return album
    }

    private fun getAlbumIDs(): MutableList<Long> {
        val albumIds = mutableListOf<Long>()
        viewModelScope.launch {
            val songsList = repository.getAudioData()
            Log.d(TAG, "getAlbumIDs: audioData is: $songsList")
            for (audio in songsList) {
                if (albumIds.isEmpty()) {
                    albumIds.add(audio.albumId)
                } else {
                    for (i in 0..albumIds.size) {
                        if (albumIds.contains(audio.albumId)) {
                            Log.d(TAG, "getAlbumIDs: already added")
                            break
                        } else {
                            albumIds.add(audio.albumId)
                            Log.d(TAG, "getAlbumIDs: added new id")
                        }
                    }
                }
            }
            Log.d(TAG, "getAlbumIDs: albumIds are: $albumIds")
        }
        return albumIds
    }

    private fun getAllAlbums() {
        viewModelScope.launch {
            val albumIds = getAlbumIDs()
            val albums = repository.getAlbumData()
            for (albumId in albumIds) {
                for (album in albums) {
                    if (albumId == album.id) {
                        album.songs = getAlbum(albumId)
                    }
                }
            }
            Log.d(TAG, "getAllAlbums: albums are:" + albums[0].songs)
        }
    }

    private fun setAlbumMediaItems(album: List<Audio>) {
        album.map { audio ->
            MediaItem.Builder()
                .setUri(audio.uri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setAlbumArtist(audio.artist)
                        .setDisplayTitle(audio.title)
                        .setSubtitle(audio.displayName)
                        .build()
                )
                .build()
        }.also {
            audioServiceHandler.setMediaItemList(it)
        }
    }

}