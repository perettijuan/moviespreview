package com.jpp.moviespreview.datalayer.api

import com.jpp.moviespreview.datalayer.AppConfiguration
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API instance for Retrofit
 */
interface TheMovieDBApi {


    /**
     * Retrieves the current configuration from the server.
     */
    @GET("configuration")
    fun getAppConfiguration(@Query("api_key") api_key: String): Call<AppConfiguration>
}