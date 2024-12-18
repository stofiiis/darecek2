package com.example.darecek2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.darecek2.ui.theme.Darecek2Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Darecek2Theme {
                MainScreen("10868b190d6c404a866aae9cb444bec3", "9d55f48d99e94f9aaa85d00f7237455e")
            }
        }
    }
}

@Composable
fun MainScreen(clientId: String, clientSecret: String) {
    val messages = listOf(
        "Mám skvělou a super mámu!",
        "Moje máma je nejlepší!",
        "Máma je úžasná!"
    )
    var currentMessageIndex by remember { mutableStateOf(0) }
    var currentlyPlayingTrack by remember { mutableStateOf("Loading...") }

    // Rotace zpráv
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            currentMessageIndex = (currentMessageIndex + 1) % messages.size
        }
    }

    // Načtení aktuálně přehrávané skladby
    LaunchedEffect(clientId, clientSecret) {
        val accessToken = fetchAccessToken(clientId, clientSecret)
        currentlyPlayingTrack = fetchCurrentlyPlayingTrack(accessToken)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFC0CB)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = messages[currentMessageIndex],
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF00FF00))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Playing on Spotify: $currentlyPlayingTrack")
                Button(onClick = { /* Zatím prázdné tlačítko */ }) {
                    Text(text = "Jam")
                }
            }
        }
    }
}

suspend fun fetchAccessToken(clientId: String, clientSecret: String): String {
    return withContext(Dispatchers.IO) {
        val credentials = okhttp3.Credentials.basic(clientId, clientSecret)
        val response = SpotifyApi.authService.getAccessToken(credentials)
        response.accessToken
    }
}

suspend fun fetchCurrentlyPlayingTrack(accessToken: String): String {
    return withContext(Dispatchers.IO) {
        try {
            val response = SpotifyApi.apiService.getCurrentlyPlayingTrack("Bearer $accessToken")
            val track = response.item
            if (track != null) {
                val trackName = track.name
                val artistNames = track.artists.joinToString(", ") { it.name }
                "$trackName by $artistNames"
            } else {
                "No track playing"
            }
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}
