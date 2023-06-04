package com.example.musicplayer.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Phone
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.musicplayer.R

sealed class BottomNavigationScreens(val route: String, @StringRes val resourceId: Int, val icon: ImageVector) {
    object Song: BottomNavigationScreens("Song", R.string.song, Icons.Filled.Phone )
    object Album: BottomNavigationScreens("Album", R.string.Album, Icons.Filled.Call)
}
