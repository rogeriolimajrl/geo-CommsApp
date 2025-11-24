package com.rogerio.geocomms.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MapboxSearchService {

    @GET("geocoding/v5/mapbox.places/{query}.json")
    suspend fun searchPlaces(
        @Path("query") query: String,
        @Query("proximity") proximity: String,
        @Query("limit") limit: Int = 10,
        @Query("access_token") token: String
    ): MapboxSearchResponse
}