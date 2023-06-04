package com.example.musicplayer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.musicplayer.ui.screens.AlbumsScreen
import com.example.musicplayer.ui.screens.SongsScreen

@Composable
private fun MainScreenNavigationConfigurations(
    navController: NavHostController
) {
    NavHost(navController, startDestination = BottomNavigationScreens.Song.route) {
        composable(BottomNavigationScreens.Song.route) {
            SongsScreen()
        }
        composable(BottomNavigationScreens.Album.route) {
            AlbumsScreen()
        }
    }
}