package com.kingsaul22.etxcenter.core.di

import com.kingsaul22.etxcenter.data.repository.AuthRepositoryImpl
import com.kingsaul22.etxcenter.data.repository.PlayerRepositoryImpl
import com.kingsaul22.etxcenter.data.repository.TeamRepositoryImpl
import com.kingsaul22.etxcenter.domain.repository.IAuthRepository
import com.kingsaul22.etxcenter.domain.repository.IPlayerRepository
import com.kingsaul22.etxcenter.domain.repository.ITeamRepository
import com.kingsaul22.etxcenter.feature.auth.AuthViewModel
import com.kingsaul22.etxcenter.feature.home.HomeViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.database.database
import org.koin.core.context.startKoin
import org.koin.dsl.module

val appModule = module {
    // Global Firebase instances
    single { Firebase.auth }
    single { Firebase.database }

    // Bind the Interface to the Implementation
    single<IAuthRepository> { AuthRepositoryImpl(get()) }
    single<IPlayerRepository> { PlayerRepositoryImpl(get()) }
    single<ITeamRepository> { TeamRepositoryImpl(get()) }

    // Register the ViewModel (using modern Koin DSL)
    factory { AuthViewModel(get()) }
    factory { HomeViewModel(get(), get()) }
}

fun initKoin() {
    startKoin {
        modules(appModule)
    }
}