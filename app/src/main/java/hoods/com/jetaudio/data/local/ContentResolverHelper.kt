package hoods.com.jetaudio.data.local

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import hoods.com.jetaudio.R
import hoods.com.jetaudio.data.local.model.Album
import hoods.com.jetaudio.data.local.model.Audio
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject

private const val TAG = "LocalDataSource"

class ContentResolverHelper @Inject
constructor(@ApplicationContext val context: Context) {
    private var mCursor: Cursor? = null

    //represents song details
    private val projection: Array<String> = arrayOf(
        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
        MediaStore.Audio.AudioColumns._ID,
        MediaStore.Audio.AudioColumns.ARTIST,
        MediaStore.Audio.AudioColumns.DATA,
        MediaStore.Audio.AudioColumns.DURATION,
        MediaStore.Audio.AudioColumns.TITLE,
        MediaStore.Audio.AudioColumns.ALBUM_ID
    )

    //represents album details
    private val albumNameColumn = MediaStore.Audio.Albums.ALBUM
    private val albumIDColumn = MediaStore.Audio.Albums.ALBUM_ID
    private val albumArtistColumn = MediaStore.Audio.Albums.ARTIST

    private val albumColumns: Array<String> = arrayOf(
        albumNameColumn,
        albumArtistColumn,
        albumIDColumn
    )

    private var selectionClause: String? =
        "${MediaStore.Audio.AudioColumns.IS_MUSIC} = ? AND ${MediaStore.Audio.Media.MIME_TYPE} NOT IN (?, ?, ?)"
    private var selectionArg = arrayOf("1", "audio/amr", "audio/3gpp", "audio/aac")

    private val sortOrder = "${MediaStore.Audio.AudioColumns.DISPLAY_NAME} ASC"

    private val sortOrderAlbums = "$albumNameColumn ASC"


    @RequiresApi(Build.VERSION_CODES.Q)
    @WorkerThread
    fun getAudioData(): List<Audio> {
        return getCursorData()
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getCursorData(): MutableList<Audio> {
        val audioList = mutableListOf<Audio>()

        mCursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selectionClause,
            selectionArg,
            sortOrder
        )

        mCursor?.use { cursor ->
            val idColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
            val albumIDColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID)
            val displayNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
            val artistColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
            val dataColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)
            val durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
            val titleColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)

            cursor.apply {
                if (count == 0) {
                    Log.e("Cursor", "getCursorData: Cursor is Empty")
                } else {
                    while (cursor.moveToNext()) {
                        val displayName = getString(displayNameColumn)
                        val id = getLong(idColumn)
                        val albumId = getLong(albumIDColumn)
                        val artist = getString(artistColumn)
                        val data = getString(dataColumn)
                        val duration = getInt(durationColumn)
                        val title = getString(titleColumn)
                        val uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                        ).toString()
                        audioList += Audio(
                            uri, displayName, id, albumId, artist, data, duration, title
                        )


                    }

                }
            }
        }
        return audioList
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @WorkerThread
    fun getAlbums(): List<Album> {
        return getAlbumData()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAlbumData(): MutableList<Album> {
        val albumList = mutableListOf<Album>()
        val albumNamesList = mutableListOf<String>()
        mCursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            albumColumns,
            selectionClause,
            selectionArg,
            sortOrderAlbums
        )

        mCursor?.use { cursor ->
            val albumIDColumn = cursor.getColumnIndexOrThrow(albumIDColumn)
            val albumNameColumn = cursor.getColumnIndexOrThrow(albumNameColumn)
            val albumArtistColumn = cursor.getColumnIndexOrThrow(albumArtistColumn)

            cursor.apply {
                if (count == 0) {
                    Log.e(TAG, "cursor is empty")
                } else {
                    while (cursor.moveToNext()) {
                        val albumID = getLong(albumIDColumn)
                        val albumName = getString(albumNameColumn)
                        val albumArtist = getString(albumArtistColumn)
                        val uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            albumID
                        ).toString()
                        val album = Album(
                            albumID,
                            albumName,
                            albumArtist,
                            10,
                            emptyList<Audio>().toMutableList(),
                            uri
                        )
                        if (!albumNamesList.contains(albumName)) {
                            albumNamesList += album.name
                            albumList += album
                        }

                    }
                    Log.d(TAG, "getAlbumData: albums: $albumList")
                    Log.d(TAG, "getAlbumNamesData: albumNames: $albumNamesList")
                }
            }
        }
        return albumList
    }
}