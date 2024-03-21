package hoods.com.jetaudio.ui.audio

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import hoods.com.jetaudio.R
import hoods.com.jetaudio.data.local.model.Album
import hoods.com.jetaudio.data.local.model.Audio
import java.io.FileNotFoundException
import java.io.IOException

private const val TAG = "MainScreen"

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    context: Context,
    songsList: List<Audio>,
    albumsList: List<Album>,
    onSongClick: (Int) -> Unit,
    progress: Float,
    onProgress: (Float) -> Unit,
    isAudioPlaying: Boolean,
    currentPlayingAudio: Audio,
    onStart: () -> Unit,
    onNext: () -> Unit,
    onClick: () -> Unit,
    onAlbumClick: (Long) -> Unit,
) {

    Scaffold(
        topBar = {
            MusicPlayerTopAppBar(
                title = stringResource(id = R.string.app_name),
                canNavigateBack = false
            )
        },
        bottomBar = {
            BottomBarPlayer(
                progress = progress,
                onProgress = onProgress,
                audio = currentPlayingAudio,
                onStart = onStart,
                onNext = onNext,
                isAudioPlaying = isAudioPlaying,
                onClick = onClick
            )
        }
    ) { paddingValues ->

        var selectedTabIndex by remember {
            mutableIntStateOf(0)
        }

        val tabItems = listOf(TabItem("Songs"), TabItem("Albums"))
        val pagerState = rememberPagerState { tabItems.size }
        LaunchedEffect(selectedTabIndex) {
            pagerState.animateScrollToPage(selectedTabIndex)
        }
        LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
            if (!pagerState.isScrollInProgress) {
                selectedTabIndex = pagerState.currentPage
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex
            ) {
                tabItems.forEachIndexed { index, tabItem ->
                    Tab(
                        selected = index == selectedTabIndex,
                        onClick = {
                            selectedTabIndex = index
                        },
                        text = {
                            Text(text = tabItem.title)
                        }
                    )
                }
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if (it == 0) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(songsList) { index, audio ->
                            AudioItem(
                                context = context,
                                audio = audio,
                                onItemClick = {
                                    onSongClick(index)
                                }
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(albumsList) { _, album ->
                            AlbumItem(
                                context = context,
                                album = album,
                                onItemClick = {
                                    onAlbumClick(album.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AlbumItem(
    context: Context,
    album: Album,
    onItemClick: () -> Unit,
) {

    val bitmap: Bitmap = try {
        context.contentResolver.loadThumbnail(album.uri.toUri(), Size(60, 60), null)
    } catch (e: FileNotFoundException) {
        Log.e(TAG, "No image available")
        val drawable =
            ContextCompat.getDrawable(context, R.drawable.ic_action_name)
        (drawable?.toBitmap())!!
    } catch (e: IOException) {
        Log.e(TAG, "IOException occurred")
        val drawable =
            ContextCompat.getDrawable(context, R.drawable.ic_action_name)
        (drawable?.toBitmap())!!
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(16.dp)
            .clickable {
                onItemClick()
            }
    )
    {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "song Image",
            modifier = Modifier.size(50.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.size(4.dp))
        Spacer(modifier = Modifier.size(4.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = album.name,
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Clip,
                maxLines = 1
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = album.artist,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Clip
            )

        }
    }
    Divider()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayerTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {}
) {
    if (canNavigateBack) {
        TopAppBar(
            title = {
                Row (
                    horizontalArrangement = Arrangement.Center,
                    modifier = modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                    )
                }
            },
            modifier = modifier,
            navigationIcon = {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        )
    } else {
        TopAppBar(title = { Text(title) }, modifier = modifier)
    }
}

//@RequiresApi(Build.VERSION_CODES.Q)
//@Preview
//@Composable
//fun MainScreenPreview() {
//    JetAudioTheme {
//        val bitmap = Bitmap.createBitmap(60, 60, Bitmap.Config.ARGB_8888)
//        MainScreen(
//            songsList = listOf(
//                Audio("".toUri(), "Title One", 0L, 0L, "Said", "", 0, "Title One", bitmap),
//                Audio("".toUri(), "Title Two", 0L, 0L, "Unknown", "", 0, "Title two", bitmap),
//            ),
//            albumsList = listOf(
//                Album(
//                    "".toLong(),
//                    "Mr Morale",
//                    "Kendrick",
//                    16,
//                    emptyList<Audio>().toMutableList(),
//                    "".toUri(),
//                    bitmap
//                ),
//                Album(
//                    "".toLong(),
//                    "Nothing Was The Same",
//                    "Drake",
//                    12,
//                    emptyList<Audio>().toMutableList(),
//                    "".toUri(),
//                    bitmap
//                ),
//            ),
//            onSongClick = {},
//            onAlbumClick = {},
//            progress = 50f,
//            onProgress = {},
//            isAudioPlaying = false,
//            currentPlayingAudio = Audio(
//                "".toUri(),
//                "Title One",
//                0L,
//                0L,
//                "Said",
//                "",
//                0,
//                "Title One",
//                bitmap
//            ),
//            onStart = {},
//            onNext = {}
//        )
//    }
//
//}

data class TabItem(
    val title: String
)