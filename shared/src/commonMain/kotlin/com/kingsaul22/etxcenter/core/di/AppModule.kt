package com.kingsaul22.etxcenter.core.di

import com.kingsaul22.etxcenter.data.repository.AuthRepositoryImpl
import com.kingsaul22.etxcenter.data.repository.LiveRepositoryImpl
import com.kingsaul22.etxcenter.data.repository.PlayerRepositoryImpl
import com.kingsaul22.etxcenter.data.repository.TeamRepositoryImpl
import com.kingsaul22.etxcenter.data.repository.MatchRepositoryImpl
import com.kingsaul22.etxcenter.domain.repository.IAuthRepository
import com.kingsaul22.etxcenter.domain.repository.ILiveRepository
import com.kingsaul22.etxcenter.domain.repository.IPlayerRepository
import com.kingsaul22.etxcenter.domain.repository.ITeamRepository
import com.kingsaul22.etxcenter.domain.repository.IMatchRepository
import com.kingsaul22.etxcenter.feature.auth.AuthViewModel
import com.kingsaul22.etxcenter.feature.home.HomeViewModel
import com.kingsaul22.etxcenter.feature.live.LiveViewModel
import com.kingsaul22.etxcenter.feature.players.PlayersViewModel
import com.kingsaul22.etxcenter.feature.stats.StatsViewModel
import com.kingsaul22.etxcenter.feature.teams.TeamsViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.database.database
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule = module {
    // Global Firebase instances
    single { Firebase.auth }
    single { Firebase.database }

    // Bind the Interface to the Implementation
    single<IAuthRepository> { AuthRepositoryImpl(get()) }
    single<ILiveRepository> { LiveRepositoryImpl(get()) }
    single<IPlayerRepository> { PlayerRepositoryImpl(get()) }
    single<ITeamRepository> { TeamRepositoryImpl(get()) }
    single<IMatchRepository> { MatchRepositoryImpl(get()) }

    // Register the ViewModel (using modern Koin DSL)
    factory { AuthViewModel(get()) }
    factory { HomeViewModel(get(), get()) }
    factory { LiveViewModel(get()) }
    factory { StatsViewModel() }
    factory { TeamsViewModel(get()) }
    factory { PlayersViewModel(get()) }
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModule)
    }
}