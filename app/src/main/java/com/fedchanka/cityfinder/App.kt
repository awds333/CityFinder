package com.fedchanka.cityfinder

import android.app.Application
import com.fedchanka.cityfinder.di.apiModule
import com.fedchanka.cityfinder.di.repositoryModule
import com.fedchanka.cityfinder.di.viewModelModule
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin { modules(apiModule, repositoryModule, viewModelModule) }
    }
}