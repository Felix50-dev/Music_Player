package com.example.musicplayer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.musicplayer.ui.screens.AlbumsScreen
import com.example.musicplayer.ui.screens.SongsScreen
import androidx.compose.ui.Modifier

@Composable
fun MainScreenNavigationConfigurations(
    navController: NavHostController,
    modifier: Modifier
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