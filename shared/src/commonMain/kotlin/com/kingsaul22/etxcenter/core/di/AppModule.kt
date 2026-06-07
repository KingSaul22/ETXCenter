package com.kingsaul22.etxcenter.core.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.database.database
import org.koin.core.context.startKoin
import org.koin.dsl.module

val appModule = module {
    single { Firebase.auth }
    single { Firebase.database }
}

fun initKoin() {
    startKoin {
        modules(appModule)
    }
}