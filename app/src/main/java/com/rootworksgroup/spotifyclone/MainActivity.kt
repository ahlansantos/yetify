package com.rootworksgroup.spotifyclone

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpotifyCloneTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = DarkBackground) {
                    SpotifyApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpotifyApp() {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentSong by remember { mutableStateOf(songs[0]) }
    var currentPosition by remember { mutableStateOf(0) }
    var maxDuration by remember { mutableStateOf(1) }

    // UPDATES PROGRESS BAR
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(1000)
            mediaPlayer?.let {
                currentPosition = it.currentPosition
                maxDuration = it.duration.takeIf { dur -> dur > 0 } ?: 1
            }
        }
    }

    // FREE RESOURCES
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    fun playSong(song: Song) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, song.resourceId).apply {
            setOnCompletionListener {
                isPlaying = false
            }
            start()
        }
        currentSong = song
        isPlaying = true
        maxDuration = mediaPlayer?.duration ?: 1
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Header()
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(songs) { song ->
                SongItem(
                    song = song,
                    isCurrent = song == currentSong,
                    isPlaying = isPlaying,
                    onItemClick = { playSong(song) }
                )
            }
        }
        PlayerBar(
            song = currentSong,
            isPlaying = isPlaying,
            currentPosition = currentPosition / maxDuration.toFloat(),
            onPlayPauseClick = {
                if (isPlaying) mediaPlayer?.pause() else mediaPlayer?.start()
                isPlaying = !isPlaying
            },
            onNextClick = {
                val nextIndex = (songs.indexOf(currentSong) + 1) % songs.size
                playSong(songs[nextIndex])
            },
            onPreviousClick = {
                val prevIndex = (songs.indexOf(currentSong) - 1 + songs.size) % songs.size
                playSong(songs[prevIndex])
            }
        )
    }
}

@Composable
fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Yetify", color = Color.White, fontSize = 24.sp)
        IconButton(onClick = {}) {
            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
        }
    }
}

@Composable
fun SongItem(
    song: Song,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.ic_music_note),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(song.title, color = if (isCurrent) SpotifyGreen else Color.White, fontSize = 16.sp)
            Text(song.artist, color = Color.Gray, fontSize = 14.sp)
        }
        IconButton(onClick = onItemClick) {
            Icon(
                imageVector = if (isCurrent && isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = if (isCurrent) SpotifyGreen else Color.White
            )
        }
    }
}

@Composable
fun PlayerBar(
    song: Song,
    isPlaying: Boolean,
    currentPosition: Float,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color(0xFF121212), DarkBackground)))
            .padding(16.dp)
    ) {
        Slider(
            value = currentPosition.coerceIn(0f, 1f),
            onValueChange = {},
            colors = SliderDefaults.colors(
                thumbColor = SpotifyGreen,
                activeTrackColor = SpotifyGreen,
                inactiveTrackColor = Color.Gray
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(song.title, color = Color.White, fontSize = 14.sp, maxLines = 1)
                Text(song.artist, color = Color.Gray, fontSize = 12.sp, maxLines = 1)
            }
            Row {
                IconButton(onClick = onPreviousClick) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = Color.White)
                }
                IconButton(
                    onClick = onPlayPauseClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(SpotifyGreen, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = onNextClick) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = Color.White)
                }
            }
        }
    }
}

data class Song(val title: String, val artist: String, val resourceId: Int)

val songs = listOf(
    Song("WW3", "Ye", R.raw.ww3),
    Song("HH", "Ye", R.raw.hh) ,
    Song("Cousins", "Ye", R.raw.cousins)
)

val DarkBackground = Color(0xFF121212)
val SpotifyGreen = Color(0xFF1DB954)

@Composable
fun SpotifyCloneTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = SpotifyGreen,
            background = DarkBackground,
            surface = Color(0xFF282828),
            onPrimary = Color.Black,
            onBackground = Color.White,
            onSurface = Color.White
        ),
        content = content
    )
}
