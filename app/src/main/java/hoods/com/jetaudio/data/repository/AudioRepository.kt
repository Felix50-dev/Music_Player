package hoods.com.jetaudio.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import hoods.com.jetaudio.data.local.ContentResolverHelper
import hoods.com.jetaudio.data.local.model.Album
import hoods.com.jetaudio.data.local.model.Audio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AudioRepository @Inject constructor(
    private val contentResolver: ContentResolverHelper,
) {
    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun getAudioData(): List<Audio> = withContext(Dispatchers.IO) {
        contentResolver.getAudioData()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun getAlbumData(): List<Album> = withContext(Dispatchers.IO) {
        contentResolver.getAlbums()
    }
}