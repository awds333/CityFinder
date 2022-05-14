package com.fedchanka.cityfinder.di

import com.fedchanka.cityfinder.api.ApiConstants
import com.fedchanka.cityfinder.api.GeocodeApi
import com.fedchanka.cityfinder.repository.CityRepository
import com.fedchanka.cityfinder.repository.CityRepositoryImpl
import com.fedchanka.cityfinder.ui.MainViewModel
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val apiModule = module(createdAtStart = true) {
    single {
        Json {
            ignoreUnknownKeys = true
        }
    }
    single {
        Retrofit.Builder()
            .baseUrl(ApiConstants.baseUrl)
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
    }
    single<GeocodeApi> {
        get<Retrofit>()
            .create(GeocodeApi::class.java)
    }
}

val repositoryModule = module(createdAtStart = true) {
    single<CityRepository> {
        CityRepositoryImpl(get())
    }
}

val viewModelModule = module {
    viewModel {
        MainViewModel(get())
    }
}