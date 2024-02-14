package com.example.musicplayer.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.musicplayer.data.dataSource.LocalDataSource
import com.example.musicplayer.data.model.Album
import com.example.musicplayer.data.model.Audio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalDataSourceRepository(private val localDataSource: LocalDataSource) {

    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun getSongs(): List<Audio> = withContext(Dispatchers.IO) {
        localDataSource.getAudio()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun getAlbums(): List<Album> = withContext(Dispatchers.IO) {
        localDataSource.getAlbums()
    }
}