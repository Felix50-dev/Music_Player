package com.example.musicplayer.data.repositories

import com.example.musicplayer.data.dataSource.LocalDataSource
import com.example.musicplayer.data.model.Album
import com.example.musicplayer.data.model.Audio

class LocalDataSourceRepository(private val localDataSource: LocalDataSource) {

    fun getSongs(): List<Audio> {
        return localDataSource.getAudio()
    }

    fun getAlbums(): List<Album> {
        return localDataSource.getAlbums()
    }


}