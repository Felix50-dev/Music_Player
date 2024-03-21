package hoods.com.jetaudio.ui.audio

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import hoods.com.jetaudio.R
import hoods.com.jetaudio.data.local.model.Audio
import java.io.FileNotFoundException
import java.io.IOException

private const val TAG = "AudioDetailsScreen"

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioDetailsScreen(
    context: Context,
    progress: Float,
    onProgress: (Float) -> Unit,
    audio: Audio,
    isAudioPlaying: Boolean,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    navigateBack: () -> Unit,
    onStart: () -> Unit
) {
    val bitmap: Bitmap = try {
        context.contentResolver.loadThumbnail(audio.uri.toUri(), Size(60, 60), null)
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

    Scaffold(
        topBar = {
            MusicPlayerTopAppBar(
                title = audio.title,
                canNavigateBack = true,
                navigateUp = navigateBack,
            )
                 },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "song image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.weight(1f))
            SongDetails(audio = audio)
            Spacer(modifier = Modifier.weight(1f))
            Slider(
                value = progress,
                onValueChange = {
                    onProgress(it)
                },
                valueRange = 0f..100f
            )
            Spacer(modifier = Modifier.weight(1f))
            AudioPlayerController(
                isAudioPlaying = isAudioPlaying,
                onStart = onStart,
                onNext = onNext,
                onPrevious = onPrevious
            )
        }
    }
}

@Composable
fun SongDetails(
    audio: Audio
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = audio.title,
            style = MaterialTheme.typography.titleLarge,
            overflow = TextOverflow.Clip,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = audio.artist,
            style = MaterialTheme.typography.bodySmall,
            overflow = TextOverflow.Clip,
            maxLines = 1
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayerTopAppBar(audio: Audio) {
    TopAppBar(title = { audio.displayName })
}

@Composable
fun AudioPlayerController(
    isAudioPlaying: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .height(56.dp)
            .padding(bottom = 16.dp)
            .fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.SkipPrevious,
            modifier = Modifier
                .clickable {
                    onPrevious()
                }
                .size(48.dp),
            contentDescription = null
        )
        Spacer(modifier = Modifier.size(16.dp))
        PlayerIconItem(
            icon = if (isAudioPlaying) Icons.Default.Pause
            else Icons.Default.PlayArrow
        ) {
            onStart()
        }
        Spacer(modifier = Modifier.size(16.dp))
        Icon(
            imageVector = Icons.Default.SkipNext,
            modifier = Modifier
                .clickable {
                    onNext()
                }
                .size(48.dp),
            contentDescription = null
        )
    }
}