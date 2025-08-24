package com.example.playlistmaker

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApiService {
    @GET("search?entity=song")
    fun getMusics(@Query("term") text: String): Call<ResponseBody>
}