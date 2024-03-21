package hoods.com.jetaudio.ui.navigation

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import hoods.com.jetaudio.ui.audio.AlbumScreen
import hoods.com.jetaudio.ui.audio.AudioDetailsScreen
import hoods.com.jetaudio.ui.audio.AudioViewModel
import hoods.com.jetaudio.ui.audio.MainScreen
import hoods.com.jetaudio.ui.audio.UIEvents

object MainDestinations {
    const val HOME_ROUTE = "home"
    const val ALBUM_ROUTE = "album"
    const val ALBUM_ID = "id"
    const val ROUTE_WITH_ARGS = "$ALBUM_ROUTE/{$ALBUM_ID}"
    const val SONG_DETAILS_ROUTE = "song"
    const val SONG_ID = "id"
    const val SONG_DETAILS_WITH_ARGS = "$SONG_DETAILS_ROUTE/{$SONG_ID}"
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MusicPlayerNavHost(
    context: Context,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: AudioViewModel,
    startService: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = MainDestinations.HOME_ROUTE,
        modifier = modifier
    ) {
        composable(
            route = MainDestinations.HOME_ROUTE
        ) {

            MainScreen(
                context = context,
                songsList = viewModel.audioList,
                albumsList = viewModel.albumList,
                onSongClick = {
                    viewModel.setMediaItems(viewModel.audioList)
                    viewModel.onUiEvents(UIEvents.SelectedAudioChange(it))
                    startService()
                    navController.navigate("${MainDestinations.SONG_DETAILS_ROUTE}/${viewModel.currentSelectedAudio.id}")
                },
                progress = viewModel.progress,
                onProgress = { viewModel.onUiEvents(UIEvents.SeekTo(it)) },
                isAudioPlaying = viewModel.isPlaying,
                currentPlayingAudio = viewModel.currentSelectedAudio,
                onStart = { viewModel.onUiEvents(UIEvents.PlayPause) },
                onNext = { viewModel.onUiEvents(UIEvents.SeekToNext) },
                onClick = {
                    navController.navigate("${MainDestinations.SONG_DETAILS_ROUTE}/${viewModel.currentSelectedAudio.id}")
                }

            ) { albumId ->
                navController.navigate("${MainDestinations.ALBUM_ROUTE}/${albumId}")
            }
        }
        composable(
            route = MainDestinations.ROUTE_WITH_ARGS,
            arguments = listOf(navArgument(name = MainDestinations.ALBUM_ID) {
                type = NavType.LongType
            })
        ) {
            AlbumScreen(
                context = context,
                audioViewModel = viewModel,
                onClick = {
                          navController.navigate("${MainDestinations.SONG_DETAILS_ROUTE}/${viewModel.currentSelectedAudio.id}")
                },
                navigateUp = { navController.navigateUp() },
                startService = startService
            )
        }
        composable(
            route = MainDestinations.SONG_DETAILS_WITH_ARGS,
            arguments = listOf(navArgument(name = MainDestinations.SONG_ID) {
                type = NavType.LongType
            })
        ) {
            AudioDetailsScreen(
                context = context,
                progress = viewModel.progress,
                onProgress = {viewModel.onUiEvents(UIEvents.SeekTo(it))},
                audio = viewModel.currentSelectedAudio,
                isAudioPlaying = viewModel.isPlaying,
                onNext = { viewModel.onUiEvents(UIEvents.SeekToNext) },
                onPrevious = { viewModel.onUiEvents(UIEvents.SeekToNext) },
                navigateBack = { navController.navigateUp() }
            ) {
                viewModel.onUiEvents(UIEvents.PlayPause)
            }
        }
    }
}
