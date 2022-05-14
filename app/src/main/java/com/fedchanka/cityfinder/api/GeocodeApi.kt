package com.fedchanka.cityfinder.api

import com.fedchanka.cityfinder.model.ReverseGeocodeResponseDto
import com.fedchanka.cityfinder.model.domain.Position
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GeocodeApi {
    @GET("/search/2/reverseGeocode/{position}.json")
    suspend fun reverseGeocode(
        @Path("position") position: Position,
        @Query("key") key: String
    ): ReverseGeocodeResponseDto
}