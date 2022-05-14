package com.fedchanka.cityfinder.repository

import com.fedchanka.cityfinder.api.ApiConstants
import com.fedchanka.cityfinder.api.GeocodeApi
import com.fedchanka.cityfinder.model.Error
import com.fedchanka.cityfinder.model.Result
import com.fedchanka.cityfinder.model.asSuccess
import com.fedchanka.cityfinder.model.domain.City
import com.fedchanka.cityfinder.model.domain.Position

class CityRepositoryImpl(private val geocodeApi: GeocodeApi) : CityRepository {
    override suspend fun getCityForPosition(position: Position): Result<City?> {
        return try {
            val response = geocodeApi.reverseGeocode(position, ApiConstants.key)
            val municipality = response.addresses?.firstNotNullOfOrNull { addressHolder ->
                addressHolder.address?.municipality
            }
            val city = municipality?.let {
                City(municipality)
            }
            city.asSuccess()
        } catch (e: Exception) {
            Error("Request failed!", e)
        }
    }
}