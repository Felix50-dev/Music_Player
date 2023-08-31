package com.example.musicplayer.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.musicplayer.ui.navigation.BottomNavigationBar
import com.example.musicplayer.ui.navigation.BottomNavigationScreens
import com.example.musicplayer.ui.navigation.MainScreenNavigationConfigurations
import androidx.compose.ui.Modifier

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    val bottomNavigationItems = listOf(
        BottomNavigationScreens.Song,
        BottomNavigationScreens.Album
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, items = bottomNavigationItems)
        },
    ) {
        MainScreenNavigationConfigurations(navController = navController, modifier = Modifier.padding(it))
    }
}