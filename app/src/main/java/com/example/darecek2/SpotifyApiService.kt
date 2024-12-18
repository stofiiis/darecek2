package com.example.darecek2

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import com.google.gson.annotations.SerializedName

// Rozhraní pro volání Spotify API
interface SpotifyApiService {

    // Endpoint pro získání přístupového tokenu
    @FormUrlEncoded
    @POST("api/token")
    suspend fun getAccessToken(
        @Header("Authorization") authorization: String,
        @Field("grant_type") grantType: String = "client_credentials"
    ): AccessTokenResponse

    // Endpoint pro aktuálně přehrávanou skladbu
    @GET("v1/me/player/currently-playing")
    suspend fun getCurrentlyPlayingTrack(
        @Header("Authorization") accessToken: String
    ): CurrentlyPlayingTrackResponse
}

// Objekt pro Retrofit instance
object SpotifyApi {
    private const val AUTH_BASE_URL = "https://accounts.spotify.com/"
    private const val API_BASE_URL = "https://api.spotify.com/"

    val authService: SpotifyApiService by lazy {
        Retrofit.Builder()
            .baseUrl(AUTH_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyApiService::class.java)
    }

    val apiService: SpotifyApiService by lazy {
        Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyApiService::class.java)
    }
}

// Datová třída pro odpověď s přístupovým tokenem
data class AccessTokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int
)

// Datová třída pro odpověď s aktuálně přehrávanou skladbou
data class CurrentlyPlayingTrackResponse(
    val item: Track?
)

// Datová třída pro skladbu
data class Track(
    val name: String,
    val artists: List<Artist>
)

// Datová třída pro interpreta
data class Artist(
    val name: String
)
