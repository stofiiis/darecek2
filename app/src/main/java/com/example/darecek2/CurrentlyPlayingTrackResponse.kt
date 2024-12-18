package com.example.darecek2

data class CurrentlyPlayingTrackResponse(
    val item: Track?
)

data class Track(
    val name: String,
    val artists: List<Artist>
)

data class Artist(
    val name: String
)
