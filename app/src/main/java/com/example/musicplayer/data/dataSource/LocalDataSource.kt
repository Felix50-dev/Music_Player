package com.example.musicplayer.data.dataSource

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import com.example.musicplayer.data.model.Album
import com.example.musicplayer.data.model.Audio
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val TAG = "LocalDataSource"

class LocalDataSource @Inject constructor(@ApplicationContext private val context: Context) {


    private var cursor: Cursor? = null

    //represents audio details
    private val displayNameColumn = MediaStore.Audio.AudioColumns.DISPLAY_NAME
    private val idColumn = MediaStore.Audio.AudioColumns._ID
    private val artistColumn = MediaStore.Audio.AudioColumns.ARTIST
    private val durationColumn = MediaStore.Audio.AudioColumns.DURATION
    private val dataColumn = MediaStore.Audio.AudioColumns.DATA
    private val titleColumn = MediaStore.Audio.AudioColumns.TITLE
    private val albumColumn = MediaStore.Audio.AudioColumns.ALBUM

    //represents album details
    private val albumNameColumn = MediaStore.Audio.Albums.ALBUM
    private val albumIDColumn = MediaStore.Audio.Albums.ALBUM_ID
    private val albumArtistColumn = MediaStore.Audio.AlbumColumns.ARTIST
    private val totalSongsForAlbum = MediaStore.Audio.Albums.NUMBER_OF_SONGS


    private val songsColumns: Array<String> = arrayOf(
        displayNameColumn,
        artistColumn,
        idColumn,
        durationColumn,
        dataColumn,
        titleColumn,
        albumColumn,
    )

    private val albumColumns: Array<String> = arrayOf(
        albumNameColumn,
        albumArtistColumn,
        albumIDColumn,
        totalSongsForAlbum,
    )

    private var selectionClause: String = "${MediaStore.Audio.AudioColumns.IS_MUSIC} = ?"

    private var selectionArgument = arrayOf("1")

    private val sortOrder = "${MediaStore.Audio.AudioColumns.DISPLAY_NAME} ASC"

    @RequiresApi(Build.VERSION_CODES.Q)
    @WorkerThread
    fun getAudio(): List<Audio> {
        return getSongsData()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @WorkerThread
    fun getAlbums(): List<Album> {
        return getAlbumsData()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getSongsData(): MutableList<Audio> {
        val audioList = mutableListOf<Audio>()
        cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            songsColumns,
            selectionClause,
            selectionArgument,
            sortOrder
        )

        cursor?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(idColumn)
            val displayNameColumn = cursor.getColumnIndexOrThrow(displayNameColumn)
            val albumColumn = cursor.getColumnIndexOrThrow(albumColumn)
            val durationColumn = cursor.getColumnIndexOrThrow(durationColumn)
            val dataColumn = cursor.getColumnIndexOrThrow(dataColumn)
            val titleColumn = cursor.getColumnIndexOrThrow(titleColumn)
            val artistColum = cursor.getColumnIndexOrThrow(artistColumn)

            cursor.apply {
                if (count == 0) {
                    Log.e(TAG, "cursor is null")
                } else {
                    while (cursor.moveToNext()) {
                        val id = getLong(idColumn)
                        val displayName = getString(displayNameColumn)
                        val album = getString(albumColumn)
                        val duration = getString(durationColumn)
                        val data = getString(dataColumn)
                        val title = getString(titleColumn)
                        val artist = getString(artistColum)

                        val uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                        )

                        val coverArt = context.contentResolver.loadThumbnail(uri, Size(640, 480), null)

                        audioList += Audio(id,uri, title, artist, duration, album, data, displayName, coverArt)
                    }
                }
            }
        }
        return audioList
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAlbumsData(): MutableList<Album> {
        val albumList = mutableListOf<Album>()
        cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            albumColumns,
            selectionClause,
            selectionArgument,
            sortOrder
        )

        cursor?.use { cursor ->
            val albumIDColumn = cursor.getColumnIndexOrThrow(albumIDColumn)
            val albumNameColumn = cursor.getColumnIndexOrThrow(albumNameColumn)
            val albumArtistColumn = cursor.getColumnIndexOrThrow(albumArtistColumn)
            val noOfSongsColumn = cursor.getColumnIndexOrThrow(totalSongsForAlbum)

            cursor.apply {
                if (count == 0) {
                    Log.e(TAG, "cursor is null")
                } else {
                    while (cursor.moveToNext()) {
                        val albumID = getLong(albumIDColumn)
                        val albumName = getString(albumNameColumn)
                        val albumArtist = getString(albumArtistColumn)
                        val numOfSongs = getInt(noOfSongsColumn)
                        val albumUri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            albumID
                        )

                        context.contentResolver.loadThumbnail(albumUri, Size(640, 480), null )

                        albumList += Album(albumID, albumName, albumArtist, numOfSongs, albumUri)
                    }
                }
            }
        }
        return albumList
    }

}