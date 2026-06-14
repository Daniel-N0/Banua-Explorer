package com.example.banuaexplorer.network

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenRouteServiceApi {

    @Headers("Content-Type: application/json")
    @POST("v2/directions/driving-car/geojson")
    suspend fun getRoute(
        @Header("Authorization")
        apiKey: String,

        @Body
        request: RouteRequest
    ): RouteResponse
}