package com.example.darecek2

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface SpotifyApiService {
    // Endpoint pro získání přístupového tokenu pomocí Authorization Code
    @FormUrlEncoded
    @POST("api/token")
    suspend fun getAccessToken(
        @Field("grant_type") grantType: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Header("Authorization") authorization: String
    ): AccessTokenResponse

    // Endpoint pro aktuálně přehrávanou skladbu
    @GET("v1/me/player/currently-playing")
    suspend fun getCurrentlyPlayingTrack(
        @Header("Authorization") accessToken: String
    ): CurrentlyPlayingTrackResponse
}
