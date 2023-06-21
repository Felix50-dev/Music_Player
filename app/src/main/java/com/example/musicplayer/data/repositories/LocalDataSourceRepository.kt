package com.example.musicplayer.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.musicplayer.data.dataSource.LocalDataSource
import com.example.musicplayer.data.model.Album
import com.example.musicplayer.data.model.Audio

class LocalDataSourceRepository(private val localDataSource: LocalDataSource) {

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getSongs(): List<Audio> {
        return localDataSource.getAudio()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getAlbums(): List<Album> {
        return localDataSource.getAlbums()
    }


}