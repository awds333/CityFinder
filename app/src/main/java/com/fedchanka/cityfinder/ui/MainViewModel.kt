package com.fedchanka.cityfinder.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fedchanka.cityfinder.R
import com.fedchanka.cityfinder.model.Error
import com.fedchanka.cityfinder.model.Result
import com.fedchanka.cityfinder.model.Success
import com.fedchanka.cityfinder.model.asSuccess
import com.fedchanka.cityfinder.model.domain.Position
import com.fedchanka.cityfinder.repository.CityRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlin.math.abs

class MainViewModel(private val cityRepository: CityRepository) : ViewModel() {

    private val _mainState = MutableStateFlow(
        MainState()
    )
    val mainState: StateFlow<MainState>
        get() = _mainState

    private var citySearchingJob: Job? = null

    init {
        viewModelScope.launch {
            async {
                mainState
                    .distinctUntilCoordinatesChanged()
                    .collect {
                        val mainState = mainState.value
                        val position = parsePositionOrNull(mainState.coordinatesInputState.raw)

                        cancelCitySearch()

                        when (mainState.coordinatesInputState.searchState) {
                            is SearchState.InProgress -> {
                                if (position is Success) {
                                    startCitySearch(position.value)
                                } else if (mainState.coordinatesInputState.raw.isNotBlank()) {
                                    setSearchState(SearchState.Error((position as Error).message.toInt()))
                                } else {
                                    setSearchState(SearchState.Normal)
                                }
                            }
                            is SearchState.Error,
                            is SearchState.NotFound -> {
                                setSearchState(SearchState.Normal)
                            }
                        }
                    }
            }

            mainState
                .distinctUntilCoordinatesChanged()
                .debounce(1100)
                .collect {
                    val mainState = mainState.value
                    val position = parsePositionOrNull(mainState.coordinatesInputState.raw)

                    if (mainState.coordinatesInputState.searchState != SearchState.InProgress) {
                        if (position is Success) {
                            startCitySearch(position.value)
                        } else if (mainState.coordinatesInputState.raw.isNotBlank()) {
                            setSearchState(SearchState.Error((position as Error).message.toInt()))
                        }
                    }
                }
        }
    }

    fun coordinatesChanged(rawCoordinates: String) {
        if (rawCoordinates.length > 30)
            return

        viewModelScope.launch {
            val coordinatesInputState = _mainState.value.coordinatesInputState
            _mainState.emit(
                _mainState.value.copy(
                    coordinatesInputState = coordinatesInputState.copy(
                        raw = rawCoordinates
                    )
                )
            )
        }
    }

    private suspend fun startCitySearch(position: Position) {
        cancelCitySearch()

        citySearchingJob = viewModelScope.launch {
            try {
                setSearchState(SearchState.InProgress)
                val result = cityRepository.getCityForPosition(position)
                checkActive()
                if (result is Success) {
                    val city = result.value
                    if (city != null) {
                        val foundCities =
                            listOf(result.value) + (mainState.value.foundCities - result.value)
                        _mainState.emit(
                            _mainState.value.copy(foundCities = foundCities)
                        )
                        setSearchState(SearchState.Normal)
                    } else {
                        setSearchState(SearchState.NotFound)
                    }

                } else {
                    setSearchState(SearchState.Error(R.string.search_failed))
                }
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    setSearchState(SearchState.Error(R.string.search_failed))
                }
            } finally {
                if (isActive) {
                    citySearchingJob = null
                }
            }
        }
    }

    private suspend fun cancelCitySearch() {
        citySearchingJob?.cancel(CancellationException("Search canceled"))
        citySearchingJob = null
        setSearchState(SearchState.Normal)
    }

    //TODO In normal situation this should be separated use case, here use cases are overkill
    private fun parsePositionOrNull(raw: String): Result<Position> {
        val coordinates = raw.split(" ", ",")
        if (coordinates.size != 2)
            return Error("${R.string.wrong_coordinates_format}")

        val lat = coordinates[0].toDoubleOrNull()
        val lon = coordinates[1].toDoubleOrNull()

        return if (lat != null && lon != null) {
            if (abs(lat) > 90 || abs(lon) > 180) {
                return Error("${R.string.value_out_of_range}")
            } else {
                Position(lat = lat, lon = lon).asSuccess()
            }
        } else Error("${R.string.wrong_coordinates_format}")
    }

    private suspend fun setSearchState(state: SearchState) {
        val coordinatesInputState = _mainState.value.coordinatesInputState
        _mainState.emit(
            _mainState.value.copy(
                coordinatesInputState = coordinatesInputState.copy(
                    searchState = state
                )
            )
        )
    }

    private fun CoroutineScope.checkActive() {
        if (!isActive)
            throw CancellationException("Coroutine is canceled!")
    }

    private fun Flow<MainState>.distinctUntilCoordinatesChanged(): Flow<MainState> =
        this.distinctUntilChanged { old, new ->
            old.coordinatesInputState.raw == new.coordinatesInputState.raw
        }
}