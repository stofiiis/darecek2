package com.example.darecek2

import android.content.Intent
import android.net.Uri
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
import kotlinx.coroutines.*
import okhttp3.Credentials

class MainActivity : ComponentActivity() {

    private val clientId = "10868b190d6c404a866aae9cb444bec3"
    private val clientSecret = "9d55f48d99e94f9aaa85d00f7237455e"
    private val redirectUri = "yourapp://callback"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Získání OAuth kódu
        if (intent?.data != null && intent.data?.scheme == "yourapp") {
            val code = intent.data?.getQueryParameter("code")
            code?.let {
                setContent {
                    Darecek2Theme {
                        MainScreen(clientId, clientSecret, it)
                    }
                }
            }
        } else {
            // Spuštění OAuth přihlašovací stránky
            val authUrl =
                "https://accounts.spotify.com/authorize?client_id=$clientId&response_type=code&redirect_uri=$redirectUri&scope=user-read-currently-playing"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)))
        }
    }
}

@Composable
fun MainScreen(clientId: String, clientSecret: String, authCode: String) {
    var currentlyPlayingTrack by remember { mutableStateOf("Loading...") }
    val momMessages = listOf(
        "Mami jsi ta nejlepší na světě! ❤️",
        "Díky mamce je každý den krásnější! 🌟",
        "Mami jsi prostě skvělá! 💪",
        "Bez mamky bych byl ztracený! 😍",
        "Mami máš srdce ze zlata! 💖"
    )
    var currentMomMessage by remember { mutableStateOf(momMessages[0]) }

    var accessToken by remember { mutableStateOf("") }

    // Rotace hlášek o mamce každých 5 sekund
    LaunchedEffect(Unit) {
        while (true) {
            for (message in momMessages) {
                currentMomMessage = message
                delay(5000)
            }
        }
    }

    // Získání tokenu a pravidelná aktualizace tracku
    LaunchedEffect(authCode) {
        accessToken = fetchAccessTokenOAuth(clientId, clientSecret, authCode)
        while (true) {
            currentlyPlayingTrack = fetchCurrentlyPlayingTrack(accessToken)
            delay(5000) // Aktualizace každých 5 sekund
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFC0CB)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hláška o mamce
            Text(
                text = currentMomMessage,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            // Spotify informace
            Text(
                text = "Aktuálně poslouchám:",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = currentlyPlayingTrack,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

suspend fun fetchAccessTokenOAuth(clientId: String, clientSecret: String, code: String): String {
    val redirectUri = "yourapp://callback"
    return withContext(Dispatchers.IO) {
        val credentials = Credentials.basic(clientId, clientSecret)
        val response = SpotifyApi.authService.getAccessToken(
            grantType = "authorization_code",
            code = code,
            redirectUri = redirectUri,
            authorization = credentials
        )
        response.accessToken
    }
}

suspend fun fetchCurrentlyPlayingTrack(accessToken: String): String {
    return withContext(Dispatchers.IO) {
        try {
            val response = SpotifyApi.apiService.getCurrentlyPlayingTrack("Bearer $accessToken")
            val trackName = response.item?.name ?: "Nic nehraje"
            val artistNames = response.item?.artists?.joinToString(", ") { it.name } ?: ""
            "$trackName by $artistNames"
        } catch (e: Exception) {
            "Nic"
        }
    }
}
