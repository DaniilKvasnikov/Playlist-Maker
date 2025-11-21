package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.ITunesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApiService {
    @GET("search?entity=song")
    suspend fun getMusics(@Query("term") text: String): Response<ITunesResponse>
}
