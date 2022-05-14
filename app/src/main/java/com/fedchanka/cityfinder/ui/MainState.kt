package com.fedchanka.cityfinder.ui

import com.fedchanka.cityfinder.model.domain.City

data class MainState(
    val foundCities: List<City> = emptyList(),
    val coordinatesInputState: CoordinatesInputState = CoordinatesInputState()
)

data class CoordinatesInputState(
    val raw: String = "",
    val searchState: SearchState = SearchState.Normal,
)

sealed class SearchState {
    object Normal : SearchState()
    object InProgress : SearchState()
    object NotFound: SearchState()
    data class Error(val message: Int) : SearchState()
}