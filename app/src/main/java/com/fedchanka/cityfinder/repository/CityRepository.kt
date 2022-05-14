package com.fedchanka.cityfinder.repository

import com.fedchanka.cityfinder.model.Result
import com.fedchanka.cityfinder.model.domain.City
import com.fedchanka.cityfinder.model.domain.Position

interface CityRepository {
    suspend fun getCityForPosition(position: Position): Result<City?>
}